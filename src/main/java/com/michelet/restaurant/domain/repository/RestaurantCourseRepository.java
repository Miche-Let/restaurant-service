package com.michelet.restaurant.domain.repository;

import com.michelet.restaurant.domain.model.RestaurantCourse;

public interface RestaurantCourseRepository {

    // 코스 엔티티를 저장
    // 실제 저장 방식은 infrastructure 계층의 JpaRepository 구현체에 위임
    RestaurantCourse save(RestaurantCourse restaurantCourse);
}
