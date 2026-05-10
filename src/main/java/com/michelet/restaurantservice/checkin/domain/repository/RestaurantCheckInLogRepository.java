package com.michelet.restaurantservice.checkin.domain.repository;

import com.michelet.restaurantservice.checkin.domain.model.RestaurantCheckInLog;

import java.util.UUID;

public interface RestaurantCheckInLogRepository {

    // 체크인 이력 저장
    RestaurantCheckInLog save(RestaurantCheckInLog restaurantCheckInLog);

    boolean existsByReservationId(UUID reservationId);
}
