import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
    vus: Number(__ENV.VUS || 10),
    duration: __ENV.DURATION || '1m',

    thresholds: {
        http_req_failed: ['rate<0.05'],
    },

    summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

const BASE_URL = __ENV.BASE_URL;
const RESTAURANT_ID = __ENV.RESTAURANT_ID;
const AUTH_TOKEN = __ENV.AUTH_TOKEN;

const KEYWORD = __ENV.KEYWORD || '';
const REGION = __ENV.REGION || '';
const STATUS = __ENV.STATUS || 'OPEN';
const PAGE = __ENV.PAGE || '0';
const SIZE = __ENV.SIZE || '10';

const DEBUG = __ENV.DEBUG === 'true';

if (!BASE_URL) {
    throw new Error('BASE_URL 환경변수는 필수입니다.');
}

if (!RESTAURANT_ID) {
    throw new Error('RESTAURANT_ID 환경변수는 필수입니다.');
}

if (!AUTH_TOKEN) {
    throw new Error('AUTH_TOKEN 환경변수는 필수입니다.');
}

function requestParams(apiName) {
    return {
        headers: {
            Authorization: `Bearer ${AUTH_TOKEN}`,
        },
        tags: {
            api: apiName,
        },
    };
}

function debugResponse(apiName, response) {
    if (!DEBUG) {
        return;
    }

    const bodyPreview = response.body ? response.body.substring(0, 300) : '';

    console.log(`[${apiName}] status=${response.status}`);
    console.log(`[${apiName}] bodyPreview=${bodyPreview}`);
}

export default function () {
    group('식당 상세 조회', function () {
        const response = http.get(
            `${BASE_URL}/api/v1/restaurants/${RESTAURANT_ID}`,
            requestParams('restaurant-detail')
        );

        debugResponse('식당 상세 조회', response);

        check(response, {
            '식당 상세 조회 status 200': (res) => res.status === 200,
            '식당 상세 조회 응답 body 존재': (res) => !!res.body,
        });
    });

    group('코스 목록 조회', function () {
        const response = http.get(
            `${BASE_URL}/api/v1/restaurants/${RESTAURANT_ID}/courses`,
            requestParams('restaurant-courses')
        );

        debugResponse('코스 목록 조회', response);

        check(response, {
            '코스 목록 조회 status 200': (res) => res.status === 200,
            '코스 목록 조회 응답 body 존재': (res) => !!res.body,
        });
    });

    group('식당 목록 검색 조회', function () {
        const queryString = [
            `keyword=${encodeURIComponent(KEYWORD)}`,
            `region=${encodeURIComponent(REGION)}`,
            `status=${encodeURIComponent(STATUS)}`,
            `page=${encodeURIComponent(PAGE)}`,
            `size=${encodeURIComponent(SIZE)}`,
        ].join('&');

        const response = http.get(
            `${BASE_URL}/api/v1/restaurants?${queryString}`,
            requestParams('restaurant-search')
        );

        debugResponse('식당 목록 검색 조회', response);

        check(response, {
            '식당 목록 검색 조회 status 200': (res) => res.status === 200,
            '식당 목록 검색 조회 응답 body 존재': (res) => !!res.body,
        });
    });

    sleep(1);
}