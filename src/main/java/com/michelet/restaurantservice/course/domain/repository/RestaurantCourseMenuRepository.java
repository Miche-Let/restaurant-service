package com.michelet.restaurantservice.course.domain.repository;

import com.michelet.restaurantservice.course.domain.model.RestaurantCourseMenu;

import java.util.List;
import java.util.UUID;

public interface RestaurantCourseMenuRepository {

    // 코스에 포함된 메뉴 목록을 한 번에 저장
    List<RestaurantCourseMenu> saveAll(List<RestaurantCourseMenu> restaurantCourseMenus);
    // 코스 ID 기준으로 코스 메뉴 목록을 조회
    List<RestaurantCourseMenu> findAllByCourseIdOrderBySortOrderAsc(UUID courseId);
    // 여러 코스 ID 기준으로 코스 메뉴 목록을 한 번에 조회
    List<RestaurantCourseMenu> findAllByCourseIdInOrderByCourseIdAscSortOrderAsc(List<UUID> courseIds);
}
