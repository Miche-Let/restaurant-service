package com.michelet.restaurantservice.course.domain.repository;

import com.michelet.restaurantservice.course.domain.model.RestaurantCourse;

import java.util.List;
import java.util.UUID;

public interface RestaurantCourseRepository {

    // 코스 엔티티를 저장
    // 실제 저장 방식은 infrastructure 계층의 JpaRepository 구현체에 위임
    RestaurantCourse save(RestaurantCourse restaurantCourse);

    // 식당 상세 조회에서 해당 식당의 코스 요약 목록 조회
    List<RestaurantCourse> findAllByRestaurantIdOrderByCreatedAtAsc(UUID restaurantId);
}
