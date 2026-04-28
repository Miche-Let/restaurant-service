package com.michelet.restaurant.application.query;

import com.michelet.restaurant.domain.model.RestaurantStatus;

/**
 * 식당 목록/검색 조회 조건 객체
 *
 * QueryDSL 기반 목록 조회에서 사용하는 검색 조건을 정의
 * 페이징 정보는 Pageable로 별도 전달하므로 이 객체에는 검색 조건만 포함
 */
public record RestaurantSearchCondition(
        String name,
        RestaurantStatus status
) {

    // 식당 이름 검색어가 존재하는지 여부를 반환
    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    // 식당 상태 조건이 존재하는지 여부를 반환
    public boolean hasStatus() {
        return status != null;
    }
}
