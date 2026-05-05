package com.michelet.restaurantservice.restaurant.domain.repository;

import com.michelet.restaurantservice.restaurant.domain.model.Restaurant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {

    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(UUID restaurantId);

    List<Restaurant> findAllByOwnerId(UUID ownerId);
}