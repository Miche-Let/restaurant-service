package com.michelet.restaurantservice.checkin.domain.repository;

import com.michelet.restaurantservice.checkin.domain.model.RestaurantCheckInLog;

public interface RestaurantCheckInLogRepository {

    // 체크인 이력 저장
    RestaurantCheckInLog save(RestaurantCheckInLog restaurantCheckInLog);
}
