package com.michelet.restaurant.infrastructure.persistence;

import com.michelet.restaurant.domain.model.RestaurantCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantCourseJpaRepository extends JpaRepository<RestaurantCourse, UUID> {

    /**
     * 식당 상세 조회에서 해당 식당의 코스 요약 목록 조회
     * MVP에서 코스 메뉴 상세목록 까지 조회 하지 않음
     */
    List<RestaurantCourse> findAllByRestaurantIdOrderByCreatedAtAsc(UUID restaurantId);
}
