package com.michelet.restaurantservice.restaurant.presentation.dto;

import java.util.UUID;

public record GetOwnerRestaurantIdResponse(
        UUID ownerId,
        UUID restaurantId
) {

    public static GetOwnerRestaurantIdResponse of(UUID ownerId, UUID restaurantId) {
        return new GetOwnerRestaurantIdResponse(ownerId, restaurantId);
    }
}