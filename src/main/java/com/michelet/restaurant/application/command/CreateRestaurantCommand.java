package com.michelet.restaurant.application.command;

import com.michelet.restaurant.domain.model.RestaurantStatus;
import com.michelet.restaurant.presentation.dto.CreateRestaurantRequest;

import java.time.LocalTime;
import java.util.UUID;

public record CreateRestaurantCommand(
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

    public static CreateRestaurantCommand of(UUID ownerId, CreateRestaurantRequest request) {
        return new CreateRestaurantCommand(
                ownerId,
                request.name(),
                request.address(),
                request.phone(),
                request.description(),
                request.reservationOpenAt(),
                request.avgMealDurationMin(),
                request.status(),
                request.businessHours()
        );
    }
}
