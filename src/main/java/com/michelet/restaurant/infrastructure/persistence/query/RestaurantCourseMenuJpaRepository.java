package com.michelet.restaurant.infrastructure.persistence.query;

import com.michelet.restaurant.domain.model.RestaurantCourseMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantCourseMenuJpaRepository extends JpaRepository<RestaurantCourseMenu, UUID> {

    // 현재 Issue #11 범위는 코스 등록
    // 등록에는 JpaRepository 기본 saveAll 메서드만 필요하므로 별도 조회 메서드는 추가x

}
