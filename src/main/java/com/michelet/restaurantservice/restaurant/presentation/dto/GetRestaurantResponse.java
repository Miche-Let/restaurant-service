package com.michelet.restaurantservice.restaurant.presentation.dto;

import com.michelet.restaurantservice.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurantservice.course.presentation.dto.CourseSummaryResponse;
import com.michelet.restaurantservice.restaurant.domain.model.RestaurantStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * 외부 식당 상세 조회 응답 DTO
 *
 * 사용자/외부 클라이언트가 식당 상세 정보를 조회할 때 사용하는 응답 객체다
 * application 계층의 GetRestaurantResult를 presentation 계층 응답으로 변환하는 역할
 */
public record GetRestaurantResponse(
        UUID restaurantId,
        String name,
        String address,
        String phone,
        String description,
        LocalTime reservationOpenAt,
        Integer avgMealDurationMin,
        RestaurantStatus status,
        String businessHours,
        List<CourseSummaryResponse> courses
) {

    /**
     * 단건 조회 결과 DTO를 외부 응답 DTO로 변환
     *
     * ownerId는 내부 관리용 식별자이므로 외부 응답에서는 제외
     */
    public static GetRestaurantResponse from(GetRestaurantResult result) {
        return new GetRestaurantResponse(
                result.restaurantId(),
                result.name(),
                result.address(),
                result.phone(),
                result.description(),
                result.reservationOpenAt(),
                result.avgMealDurationMin(),
                result.status(),
                result.businessHours(),
                result.courses().stream()
                        .map(course -> CourseSummaryResponse.from(course))
                        .toList()
        );
    }
}
