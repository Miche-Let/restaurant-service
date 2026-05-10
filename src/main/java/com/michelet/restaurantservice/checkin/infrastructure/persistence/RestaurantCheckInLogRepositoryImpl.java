package com.michelet.restaurantservice.checkin.infrastructure.persistence;

import com.michelet.restaurantservice.checkin.domain.model.RestaurantCheckInLog;
import com.michelet.restaurantservice.checkin.domain.repository.RestaurantCheckInLogRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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

    // reservationId 기준으로 이미 처리된 체크인 로그인지 확인
    // Consumer에서 같은 reservationId 이벤트가 재전달되면 저장하지 않고 skip하기 위해 사용
    @Override
    public boolean existsByReservationId(UUID reservationId) {
        return restaurantCheckInLogJpaRepository.existsByReservationId(reservationId);
    }
}
