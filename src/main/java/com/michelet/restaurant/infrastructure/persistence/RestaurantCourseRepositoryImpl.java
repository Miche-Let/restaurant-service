package com.michelet.restaurant.infrastructure.persistence;

import com.michelet.restaurant.domain.model.RestaurantCourse;
import com.michelet.restaurant.domain.repository.RestaurantCourseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class RestaurantCourseRepositoryImpl implements RestaurantCourseRepository {

    private final RestaurantCourseJpaRepository restaurantCourseJpaRepository;

    public RestaurantCourseRepositoryImpl(RestaurantCourseJpaRepository restaurantCourseJpaRepository) {
        this.restaurantCourseJpaRepository = restaurantCourseJpaRepository;
    }

    @Override
    public RestaurantCourse save(RestaurantCourse restaurantCourse) {
        return restaurantCourseJpaRepository.save(restaurantCourse);
    }

    @Override
    public List<RestaurantCourse> findAllByRestaurantIdOrderByCreatedAtAsc(UUID restaurantId) {
        return restaurantCourseJpaRepository.findAllByRestaurantIdOrderByCreatedAtAsc(restaurantId);
    }
}
