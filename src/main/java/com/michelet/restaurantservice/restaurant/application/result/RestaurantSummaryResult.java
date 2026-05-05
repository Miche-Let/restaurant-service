package com.michelet.restaurantservice.restaurant.application.result;

import com.michelet.restaurantservice.restaurant.domain.model.Restaurant;
import com.michelet.restaurantservice.restaurant.domain.model.RestaurantStatus;

import java.util.UUID;

/**
 * 식당 목록 조회용 결과 DTO
 *
 * 목록/검색 조회에서 각 식당의 요약 정보를 반환할 때 사용
 * 단건 상세 조회와 달리 목록 화면에 필요한 최소 정보만 포함
 */
public record RestaurantSummaryResult(
        UUID restaurantId,
        String name,
        String address,
        String phone,
        RestaurantStatus status,
        String businessHours
) {

    /**
     * Restaurant 엔티티를 목록 조회용 결과 DTO로 변환
     */
    public static RestaurantSummaryResult from(Restaurant restaurant) {
        return new RestaurantSummaryResult(
                restaurant.getRestaurantId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getPhone(),
                restaurant.getStatus(),
                restaurant.getBusinessHours()
        );
    }
}
