package com.michelet.restaurant.application.service.query;

import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurant.domain.model.Restaurant;
import com.michelet.restaurant.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantQueryService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * 식당 단건 정보를 조회
     *
     * 외부 상세 조회 API / 내부 조회 API에서 공통으로 사용하는 조회 메서드
     * 식당이 존재하지 않으면 RESTAURANT_404_NOT_FOUND 예외던짐
     */
    public GetRestaurantResult getRestaurant(UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));

        return GetRestaurantResult.from(restaurant);
    }
}