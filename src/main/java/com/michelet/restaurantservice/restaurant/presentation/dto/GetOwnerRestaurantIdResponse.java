package com.michelet.restaurantservice.restaurant.presentation.dto;

import java.util.UUID;

public record GetOwnerRestaurantIdResponse(
        UUID restaurantId
) {

    public static GetOwnerRestaurantIdResponse from(UUID restaurantId) {
        return new GetOwnerRestaurantIdResponse(restaurantId);
    }
}
