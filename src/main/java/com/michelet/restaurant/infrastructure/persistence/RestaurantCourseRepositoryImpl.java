package com.michelet.restaurant.infrastructure.persistence;

import com.michelet.restaurant.domain.model.RestaurantCourse;
import com.michelet.restaurant.domain.repository.RestaurantCourseRepository;
import org.springframework.stereotype.Repository;

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
}
