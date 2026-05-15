import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

const TEST_MODE = __ENV.TEST_MODE || 'load';

const commonThresholds = {
    // smoke / load 공통: 오류율 0.00%
    http_req_failed: ['rate==0'],

    // API별 실패율도 0.00%
    restaurant_detail_failed_rate: ['rate==0'],
    restaurant_courses_failed_rate: ['rate==0'],
    restaurant_search_failed_rate: ['rate==0'],
};

const loadThresholds = {
    // load 모드에서만 처리량 기준 적용
    http_reqs: ['rate>=200'],

    // load 모드에서만 응답 시간 기준 적용
    restaurant_detail_duration: ['p(95)<100', 'p(99)<200'],
    restaurant_courses_duration: ['p(95)<100', 'p(99)<200'],
    restaurant_search_duration: ['p(95)<100', 'p(99)<200'],
};

export const options = {
    scenarios: {
        cache_to_be_load_test: {
            executor: 'ramping-vus',
            stages: [
                { duration: __ENV.RAMP_UP || '1m', target: Number(__ENV.VUS || 100) },
                { duration: __ENV.HOLD_DURATION || '3m', target: Number(__ENV.VUS || 100) },
                { duration: __ENV.RAMP_DOWN || '30s', target: 0 },
            ],
            gracefulRampDown: '30s',
        },
    },

    thresholds:
        TEST_MODE === 'smoke'
            ? commonThresholds
            : {
                ...commonThresholds,
                ...loadThresholds,
            },

    summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

const restaurantDetailDuration = new Trend('restaurant_detail_duration', true);
const restaurantCoursesDuration = new Trend('restaurant_courses_duration', true);
const restaurantSearchDuration = new Trend('restaurant_search_duration', true);

const restaurantDetailFailedRate = new Rate('restaurant_detail_failed_rate');
const restaurantCoursesFailedRate = new Rate('restaurant_courses_failed_rate');
const restaurantSearchFailedRate = new Rate('restaurant_search_failed_rate');

const restaurantDetailRequests = new Counter('restaurant_detail_requests');
const restaurantCoursesRequests = new Counter('restaurant_courses_requests');
const restaurantSearchRequests = new Counter('restaurant_search_requests');

const BASE_URL = __ENV.BASE_URL;
const RESTAURANT_ID = __ENV.RESTAURANT_ID;
const RESTAURANT_IDS = (__ENV.RESTAURANT_IDS || '')
    .split(',')
    .map((id) => id.trim())
    .filter((id) => id.length > 0);

const AUTH_TOKEN = __ENV.AUTH_TOKEN;

const KEYWORD = __ENV.KEYWORD || '';
const REGION = __ENV.REGION || '';
const STATUS = __ENV.STATUS || 'OPEN';
const PAGE = __ENV.PAGE || '0';
const SIZE = __ENV.SIZE || '10';

const SLEEP_SECONDS = Number(__ENV.SLEEP_SECONDS || 0.2);
const DEBUG = __ENV.DEBUG === 'true';
const FAILURE_LOG_LIMIT = Number(__ENV.FAILURE_LOG_LIMIT || 20);
const SUMMARY_PATH = __ENV.SUMMARY_PATH || 'load-tests/restaurant/results/to-be-cache-summary.json';

let failureLogCount = 0;

if (!BASE_URL) {
    throw new Error('BASE_URL 환경변수는 필수입니다.');
}

if (!RESTAURANT_ID && RESTAURANT_IDS.length === 0) {
    throw new Error('RESTAURANT_ID 또는 RESTAURANT_IDS 환경변수는 필수입니다.');
}

if (!AUTH_TOKEN) {
    throw new Error('AUTH_TOKEN 환경변수는 필수입니다.');
}

function pickRestaurantId() {
    if (RESTAURANT_IDS.length > 0) {
        return RESTAURANT_IDS[Math.floor(Math.random() * RESTAURANT_IDS.length)];
    }

    return RESTAURANT_ID;
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

function isSuccessResponse(response) {
    if (response.status !== 200) {
        return false;
    }

    try {
        return response.json('success') === true;
    } catch (error) {
        return false;
    }
}

function debugResponse(apiName, response) {
    if (!DEBUG) {
        return;
    }

    const bodyPreview = response.body ? response.body.substring(0, 300) : '';

    console.log(`[${apiName}] status=${response.status}, duration=${response.timings.duration}ms`);
    console.log(`[${apiName}] bodyPreview=${bodyPreview}`);
}

function logFailedResponse(apiName, response) {
    const success = isSuccessResponse(response);

    if (success) {
        return;
    }

    if (failureLogCount >= FAILURE_LOG_LIMIT) {
        return;
    }

    failureLogCount += 1;

    const bodyPreview = response.body ? response.body.substring(0, 500) : '';

    console.error(
        `[실패 응답] api=${apiName}, status=${response.status}, duration=${response.timings.duration}ms, bodyPreview=${bodyPreview}`
    );
}

function recordApiMetrics(durationMetric, failedRateMetric, requestCounter, response) {
    const success = isSuccessResponse(response);

    durationMetric.add(response.timings.duration);
    failedRateMetric.add(!success);
    requestCounter.add(1);
}

function searchRestaurants() {
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
        logFailedResponse('식당 목록 검색 조회', response);

        recordApiMetrics(
            restaurantSearchDuration,
            restaurantSearchFailedRate,
            restaurantSearchRequests,
            response
        );

        check(
            response,
            {
                '식당 목록 검색 조회 HTTP 200': (res) => res.status === 200,
                '식당 목록 검색 조회 success true': (res) => isSuccessResponse(res),
                '식당 목록 검색 조회 응답 body 존재': (res) => !!res.body,
            },
            {
                api: 'restaurant-search',
            }
        );
    });
}

function getRestaurantDetail(restaurantId) {
    group('식당 상세 조회', function () {
        const response = http.get(
            `${BASE_URL}/api/v1/restaurants/${restaurantId}`,
            requestParams('restaurant-detail')
        );

        debugResponse('식당 상세 조회', response);
        logFailedResponse('식당 상세 조회', response);

        recordApiMetrics(
            restaurantDetailDuration,
            restaurantDetailFailedRate,
            restaurantDetailRequests,
            response
        );

        check(
            response,
            {
                '식당 상세 조회 HTTP 200': (res) => res.status === 200,
                '식당 상세 조회 success true': (res) => isSuccessResponse(res),
                '식당 상세 조회 응답 body 존재': (res) => !!res.body,
            },
            {
                api: 'restaurant-detail',
            }
        );
    });
}

function getRestaurantCourses(restaurantId) {
    group('코스 목록 조회', function () {
        const response = http.get(
            `${BASE_URL}/api/v1/restaurants/${restaurantId}/courses`,
            requestParams('restaurant-courses')
        );

        debugResponse('코스 목록 조회', response);
        logFailedResponse('코스 목록 조회', response);

        recordApiMetrics(
            restaurantCoursesDuration,
            restaurantCoursesFailedRate,
            restaurantCoursesRequests,
            response
        );

        check(
            response,
            {
                '코스 목록 조회 HTTP 200': (res) => res.status === 200,
                '코스 목록 조회 success true': (res) => isSuccessResponse(res),
                '코스 목록 조회 응답 body 존재': (res) => !!res.body,
            },
            {
                api: 'restaurant-courses',
            }
        );
    });
}

export default function () {
    const restaurantId = pickRestaurantId();

    // 실제 사용자 흐름에 가깝게 검색 → 상세 → 코스 목록 순서로 호출한다.
    searchRestaurants();
    getRestaurantDetail(restaurantId);
    getRestaurantCourses(restaurantId);

    sleep(SLEEP_SECONDS);
}

function metricValues(data, metricName) {
    return data.metrics[metricName] ? data.metrics[metricName].values : {};
}

function thresholdValues(data, metricName) {
    const metric = data.metrics[metricName];

    if (!metric || !metric.thresholds) {
        return {};
    }

    return metric.thresholds;
}

function toPercent(rate) {
    if (rate === undefined || rate === null) {
        return null;
    }

    return `${(rate * 100).toFixed(2)}%`;
}

function round(value) {
    if (value === undefined || value === null) {
        return null;
    }

    return Number(value.toFixed(2));
}

function createResponseTimeSummary(duration) {
    return {
        평균: round(duration.avg),
        최소: round(duration.min),
        중앙값: round(duration.med),
        p90: round(duration['p(90)']),
        p95: round(duration['p(95)']),
        p99: round(duration['p(99)']),
        최대: round(duration.max),
    };
}

function createApiSummary(data, durationMetricName, failedRateMetricName, requestMetricName) {
    const duration = metricValues(data, durationMetricName);
    const failedRate = metricValues(data, failedRateMetricName);
    const requests = metricValues(data, requestMetricName);

    return {
        요청_수: requests.count || 0,
        초당_요청_수: round(requests.rate),
        실패율: toPercent(failedRate.rate),
        응답_시간_ms: createResponseTimeSummary(duration),
        threshold_결과: {
            실패율: thresholdValues(data, failedRateMetricName),
            응답_시간: thresholdValues(data, durationMetricName),
        },
    };
}

function createKoreanSummary(data) {
    const totalDuration = metricValues(data, 'http_req_duration');
    const totalFailedRate = metricValues(data, 'http_req_failed');
    const totalRequests = metricValues(data, 'http_reqs');
    const checks = metricValues(data, 'checks');
    const vus = metricValues(data, 'vus');
    const vusMax = metricValues(data, 'vus_max');

    return {
        테스트_요약: {
            테스트_종류: 'Redis 캐시 적용 후 To-Be 부하테스트',
            테스트_모드: TEST_MODE,
            전체_요청_수: totalRequests.count || 0,
            전체_초당_요청_수: round(totalRequests.rate),
            전체_실패율: toPercent(totalFailedRate.rate),
            체크_성공률: toPercent(checks.rate),
            현재_VUS: vus.value || null,
            최대_VUS: vusMax.value || vusMax.max || null,
            테스트_실행_시간_ms: round(data.state.testRunDurationMs),
            전체_응답_시간_ms: createResponseTimeSummary(totalDuration),
            threshold_결과: {
                전체_HTTP_실패율: thresholdValues(data, 'http_req_failed'),
                전체_처리량: thresholdValues(data, 'http_reqs'),
            },
        },

        API별_지표: {
            식당_목록_검색_조회_API: createApiSummary(
                data,
                'restaurant_search_duration',
                'restaurant_search_failed_rate',
                'restaurant_search_requests'
            ),
            식당_상세_조회_API: createApiSummary(
                data,
                'restaurant_detail_duration',
                'restaurant_detail_failed_rate',
                'restaurant_detail_requests'
            ),
            코스_목록_조회_API: createApiSummary(
                data,
                'restaurant_courses_duration',
                'restaurant_courses_failed_rate',
                'restaurant_courses_requests'
            ),
        },

        요구사항_검증_기준: {
            테스트_도구: 'k6',
            목표_VUS: Number(__ENV.VUS || 100),
            ramp_up: __ENV.RAMP_UP || '1m',
            목표_처리량: '200 req/s 이상',
            목표_오류율: '0.00%',
            응답시간_threshold: {
                p95: '100ms 미만',
                p99: '200ms 미만',
            },
        },
    };
}

export function handleSummary(data) {
    const koreanSummary = createKoreanSummary(data);

    const summary = {};
    summary[SUMMARY_PATH] = JSON.stringify(koreanSummary, null, 2);

    summary.stdout = [
        '',
        'To-Be summary.json 저장 완료',
        `저장 경로=${SUMMARY_PATH}`,
        '',
        '[이번 실행 요약]',
        `- 테스트 모드: ${koreanSummary.테스트_요약.테스트_모드}`,
        `- 전체 요청 수: ${koreanSummary.테스트_요약.전체_요청_수}`,
        `- 전체 초당 요청 수: ${koreanSummary.테스트_요약.전체_초당_요청_수}`,
        `- 전체 실패율: ${koreanSummary.테스트_요약.전체_실패율}`,
        `- 체크 성공률: ${koreanSummary.테스트_요약.체크_성공률}`,
        `- 전체 평균 응답 시간: ${koreanSummary.테스트_요약.전체_응답_시간_ms.평균}ms`,
        `- 전체 p95 응답 시간: ${koreanSummary.테스트_요약.전체_응답_시간_ms.p95}ms`,
        '',
        '[API별 p95 응답 시간]',
        `- 식당 목록 검색 조회 API: ${koreanSummary.API별_지표.식당_목록_검색_조회_API.응답_시간_ms.p95}ms`,
        `- 식당 상세 조회 API: ${koreanSummary.API별_지표.식당_상세_조회_API.응답_시간_ms.p95}ms`,
        `- 코스 목록 조회 API: ${koreanSummary.API별_지표.코스_목록_조회_API.응답_시간_ms.p95}ms`,
        '',
        '[API별 실패율]',
        `- 식당 목록 검색 조회 API: ${koreanSummary.API별_지표.식당_목록_검색_조회_API.실패율}`,
        `- 식당 상세 조회 API: ${koreanSummary.API별_지표.식당_상세_조회_API.실패율}`,
        `- 코스 목록 조회 API: ${koreanSummary.API별_지표.코스_목록_조회_API.실패율}`,
        '',
    ].join('\n');

    return summary;
}