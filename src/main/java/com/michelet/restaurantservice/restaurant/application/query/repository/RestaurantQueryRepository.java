package com.michelet.restaurantservice.restaurant.application.query.repository;

import com.michelet.restaurantservice.restaurant.application.query.RestaurantSearchCondition;
import com.michelet.restaurantservice.restaurant.application.result.RestaurantSummaryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// 식당 목록/검색 조회용 Query Repository
public interface RestaurantQueryRepository {

    /**
     * 식당 목록/검색 결과를 페이지 단위로 조회
     *
     * @param condition 검색 조건
     * @param pageable 페이징 정보
     * @return 식당 목록 조회 결과 페이지
     */
    Page<RestaurantSummaryResult> search(RestaurantSearchCondition condition, Pageable pageable);
}
