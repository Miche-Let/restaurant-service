package com.michelet.restaurant.infrastructure.persistence;

import com.michelet.restaurant.domain.model.RestaurantCourseMenu;
import com.michelet.restaurant.domain.repository.RestaurantCourseMenuRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RestaurantCourseMenuRepositoryImpl implements RestaurantCourseMenuRepository {

    private final RestaurantCourseMenuJpaRepository restaurantCourseMenuJpaRepository;

    public RestaurantCourseMenuRepositoryImpl(RestaurantCourseMenuJpaRepository restaurantCourseMenuJpaRepository) {
        this.restaurantCourseMenuJpaRepository = restaurantCourseMenuJpaRepository;
    }

    @Override
    public List<RestaurantCourseMenu> saveAll(List<RestaurantCourseMenu> restaurantCourseMenus) {
        return restaurantCourseMenuJpaRepository.saveAll(restaurantCourseMenus);
    }
}
