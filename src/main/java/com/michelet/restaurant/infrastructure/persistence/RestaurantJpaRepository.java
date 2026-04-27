package com.michelet.restaurant.infrastructure.persistence;

import com.michelet.restaurant.domain.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantJpaRepository extends JpaRepository<Restaurant, UUID> {

    List<Restaurant> findAllByOwnerId(UUID ownerId);
}
