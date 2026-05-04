package com.michelet.restaurant.infrastructure.persistence;

import com.michelet.restaurant.domain.model.RestaurantCheckInLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantCheckInLogJpaRepository extends JpaRepository<RestaurantCheckInLog, UUID> {

    // 저장만 필요(JpaRepository가 이미 제공) 추후 추가
}
