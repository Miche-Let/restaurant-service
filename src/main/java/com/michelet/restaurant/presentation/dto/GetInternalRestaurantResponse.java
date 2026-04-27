package com.michelet.restaurant.presentation.dto;

import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.domain.model.RestaurantStatus;

import java.time.LocalTime;
import java.util.UUID;

/**
 * 내부 식당 단건 조회 응답 DTO
 *
 * 내부 서비스 간 조회 또는 관리자/백오피스 성격의 내부 API에서 사용하는 응답 객체
 * 외부 응답과 달리 ownerId를 포함해 내부 로직에서 필요한 식별 정보를 함께 전달
 */
public record GetInternalRestaurantResponse(
        UUID restaurantId,
        UUID ownerId,
        String name,
        String address,
        String phone,
        String description,
        LocalTime reservationOpenAt,
        Integer avgMealDurationMin,
        RestaurantStatus status,
        String businessHours
) {

    /**
     * 단건 조회 결과 DTO를 내부 응답 DTO로 변환
     *
     * 내부 API는 식당 소유자 식별값도 필요 ownerId를 포함
     */
    public static GetInternalRestaurantResponse from(GetRestaurantResult result) {
        return new GetInternalRestaurantResponse(
                result.restaurantId(),
                result.ownerId(),
                result.name(),
                result.address(),
                result.phone(),
                result.description(),
                result.reservationOpenAt(),
                result.avgMealDurationMin(),
                result.status(),
                result.businessHours()
        );
    }
}
