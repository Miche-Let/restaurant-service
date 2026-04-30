package com.michelet.restaurant.infrastructure.persistence;

import com.michelet.restaurant.domain.model.RestaurantCourseMenu;
import com.michelet.restaurant.domain.repository.RestaurantCourseMenuRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class RestaurantCourseMenuRepositoryImpl implements RestaurantCourseMenuRepository {

    private final RestaurantCourseMenuJpaRepository restaurantCourseMenuJpaRepository;

    public RestaurantCourseMenuRepositoryImpl(RestaurantCourseMenuJpaRepository restaurantCourseMenuJpaRepository) {
        this.restaurantCourseMenuJpaRepository = restaurantCourseMenuJpaRepository;
    }

    // 코스 등록 시 여러 코스 메뉴를 한 번에 저장
    @Override
    public List<RestaurantCourseMenu> saveAll(List<RestaurantCourseMenu> restaurantCourseMenus) {
        return restaurantCourseMenuJpaRepository.saveAll(restaurantCourseMenus);
    }

    // 코스 ID 기준으로 코스 메뉴 목록을 조회
    @Override
    public List<RestaurantCourseMenu> findAllByCourseIdOrderBySortOrderAsc(UUID courseId) {
        return restaurantCourseMenuJpaRepository.findAllByCourseIdOrderBySortOrderAsc(courseId);
    }

    // 여러 코스 ID 기준으로 코스 메뉴 목록을 한 번에 조회
    @Override
    public List<RestaurantCourseMenu> findAllByCourseIdInOrderByCourseIdAscSortOrderAsc(List<UUID> courseIds) {
        return restaurantCourseMenuJpaRepository.findAllByCourseIdInOrderByCourseIdAscSortOrderAsc(courseIds);
    }
}
