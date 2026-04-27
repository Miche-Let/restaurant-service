package com.michelet.restaurant.domain.repository;

import com.michelet.restaurant.domain.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    List<Restaurant> findAllByOwnerId(UUID ownerId);
}
