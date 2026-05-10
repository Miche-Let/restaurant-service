package com.michelet.restaurantservice.checkin.infrastructure.persistence;

import com.michelet.restaurantservice.checkin.domain.model.RestaurantCheckInLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantCheckInLogJpaRepository extends JpaRepository<RestaurantCheckInLog, UUID> {

    // reservationId 기준으로 체크인 로그 존재 여부 확인
    boolean existsByReservationId(UUID reservationId);
}
