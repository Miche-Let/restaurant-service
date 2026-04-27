package com.michelet.restaurant.application.result;

import com.michelet.restaurant.domain.model.Restaurant;

import java.util.UUID;

public record CreateRestaurantResult(
        UUID restaurantId,
        UUID ownerId
) {

    public static CreateRestaurantResult from(Restaurant restaurant) {
        return new CreateRestaurantResult(
                restaurant.getRestaurantId(),
                restaurant.getOwnerId()
        );
    }
}
