package com.michelet.restaurant.application.result;

import com.michelet.restaurant.domain.model.Restaurant;
import com.michelet.restaurant.domain.model.RestaurantStatus;

import java.time.LocalTime;
import java.util.UUID;

/**
 * 식당 단건 조회 결과 DTO
 *
 * application 계층에서 domain entity를 presentation 계층에 직접 넘기지 않기 위해 사용
 * 외부 조회 응답 DTO / 내부 조회 응답 DTO는 이 결과 객체를 기반으로 변환
 */
public record GetRestaurantResult(
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

    // Restaurant 엔티티를 단건 조회 결과 DTO로 변환
    public static GetRestaurantResult from(Restaurant restaurant) {
        return new GetRestaurantResult(
                restaurant.getRestaurantId(),
                restaurant.getOwnerId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getPhone(),
                restaurant.getDescription(),
                restaurant.getReservationOpenAt(),
                restaurant.getAvgMealDurationMin(),
                restaurant.getStatus(),
                restaurant.getBusinessHours()
        );
    }

}
