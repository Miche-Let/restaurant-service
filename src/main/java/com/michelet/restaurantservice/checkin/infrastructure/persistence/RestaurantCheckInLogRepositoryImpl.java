package com.michelet.restaurantservice.checkin.infrastructure.persistence;

import com.michelet.restaurantservice.checkin.domain.model.RestaurantCheckInLog;
import com.michelet.restaurantservice.checkin.domain.repository.RestaurantCheckInLogRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RestaurantCheckInLogRepositoryImpl implements RestaurantCheckInLogRepository {

    private final RestaurantCheckInLogJpaRepository restaurantCheckInLogJpaRepository;

    public RestaurantCheckInLogRepositoryImpl(RestaurantCheckInLogJpaRepository restaurantCheckInLogJpaRepository) {
        this.restaurantCheckInLogJpaRepository = restaurantCheckInLogJpaRepository;
    }

    // 체크인 이력 저장
    @Override
    public RestaurantCheckInLog save(RestaurantCheckInLog restaurantCheckInLog) {
        return restaurantCheckInLogJpaRepository.save(restaurantCheckInLog);
    }
}
