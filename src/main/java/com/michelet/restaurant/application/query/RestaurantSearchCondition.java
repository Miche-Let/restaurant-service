package com.michelet.restaurant.application.query;

import com.michelet.restaurant.domain.model.RestaurantStatus;

/**
 * 식당 목록/검색 조회 조건 객체
 *
 * QueryDSL 기반 목록 조회에서 사용하는 검색 조건을 정의
 * 페이징 정보는 Pageable로 별도 전달하므로 이 객체에는 검색 조건만 포함
 */
public record RestaurantSearchCondition(
        String keyword,
        String region,
        RestaurantStatus status
) {

    // 식당명 검색어가 존재하는지 확인
    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }

    // 지역 검색어가 존재하는지 확인
    // MVP에서는 별도 region 컬럼이 없으므로 address 검색 조건으로 사용
    public boolean hasRegion() {
        return region != null && !region.isBlank();
    }

    // 식당 상태 조건이 존재하는지 확인
    public boolean hasStatus() {
        return status != null;
    }
}
