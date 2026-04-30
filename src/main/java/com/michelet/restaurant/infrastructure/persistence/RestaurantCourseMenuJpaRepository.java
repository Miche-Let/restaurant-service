package com.michelet.restaurant.infrastructure.persistence;

import com.michelet.restaurant.domain.model.RestaurantCourseMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantCourseMenuJpaRepository extends JpaRepository<RestaurantCourseMenu, UUID> {

    /**
     * 코스 ID 기준으로 코스 메뉴 목록을 조회
     *
     * 메뉴 노출 순서는 클라이언트 요청 순서가 아니라 sortOrder 기준
     * courseId 메뉴 목록을 조회할 코스 ID
     * sortOrder 오름차순으로 정렬된 코스 메뉴 목록
     */
    List<RestaurantCourseMenu> findAllByCourseIdOrderBySortOrderAsc(UUID courseId);
}
