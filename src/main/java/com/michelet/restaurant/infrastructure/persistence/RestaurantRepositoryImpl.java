package com.michelet.restaurant.infrastructure.persistence;

import com.michelet.restaurant.domain.model.Restaurant;
import com.michelet.restaurant.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;

    public RestaurantRepositoryImpl(RestaurantJpaRepository restaurantJpaRepository) {
        this.restaurantJpaRepository = restaurantJpaRepository;
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        return restaurantJpaRepository.save(restaurant);
    }

    @Override
    public Optional<Restaurant> findById(UUID restaurantId) {
        return restaurantJpaRepository.findById(restaurantId);
    }

    @Override
    public List<Restaurant> findAllByOwnerId(UUID ownerId) {
        return restaurantJpaRepository.findAllByOwnerId(ownerId);
    }
}