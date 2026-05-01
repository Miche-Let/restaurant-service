package com.michelet.restaurant.domain.repository;

import com.michelet.restaurant.domain.model.RestaurantCheckInLog;

public interface RestaurantCheckInLogRepository {

    // 체크인 이력 저장
    RestaurantCheckInLog save(RestaurantCheckInLog restaurantCheckInLog);
}
