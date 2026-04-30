package com.michelet.restaurant.domain.repository;

import com.michelet.restaurant.domain.model.RestaurantCourseMenu;

import java.util.List;
import java.util.UUID;

public interface RestaurantCourseMenuRepository {

    // 코스에 포함된 메뉴 목록을 한 번에 저장
    List<RestaurantCourseMenu> saveAll(List<RestaurantCourseMenu> restaurantCourseMenus);

    List<RestaurantCourseMenu> findAllByCourseIdOrderBySortOrderAsc(UUID courseId);
}
