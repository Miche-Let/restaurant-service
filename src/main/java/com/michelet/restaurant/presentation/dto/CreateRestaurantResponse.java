package com.michelet.restaurant.presentation.dto;

import java.util.UUID;

public record CreateRestaurantResponse(
        UUID restaurantId,
        UUID ownerId
) {

    public static CreateRestaurantResponse of(UUID restaurantId, UUID ownerId) {
        return new CreateRestaurantResponse(restaurantId, ownerId);
    }
}
