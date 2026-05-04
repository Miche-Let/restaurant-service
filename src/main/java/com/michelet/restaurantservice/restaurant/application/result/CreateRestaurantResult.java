package com.michelet.restaurantservice.restaurant.application.result;

import com.michelet.restaurantservice.restaurant.domain.model.Restaurant;

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
