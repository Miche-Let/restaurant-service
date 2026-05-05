package com.michelet.restaurantservice.restaurant.presentation.dto;

import com.michelet.restaurantservice.restaurant.application.result.RestaurantSummaryResult;
import com.michelet.restaurantservice.restaurant.domain.model.RestaurantStatus;

import java.util.UUID;

/**
 * 식당 목록 조회 응답 DTO
 *
 * 외부 클라이언트가 식당 목록 또는 검색 결과를 조회할 때
 * 각 식당의 요약 정보를 응답하기 위해 사용
 */
public record RestaurantSummaryResponse(
        UUID restaurantId,
        String name,
        String address,
        String phone,
        RestaurantStatus status,
        String businessHours
) {

    //  목록 조회 결과 DTO를 외부 응답 DTO로 변환
    public static RestaurantSummaryResponse from(RestaurantSummaryResult result) {
        return new RestaurantSummaryResponse(
                result.restaurantId(),
                result.name(),
                result.address(),
                result.phone(),
                result.status(),
                result.businessHours()
        );
    }
}
