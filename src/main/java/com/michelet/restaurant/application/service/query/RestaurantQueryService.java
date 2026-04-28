package com.michelet.restaurant.application.service.query;

import com.michelet.restaurant.application.query.RestaurantSearchCondition;
import com.michelet.restaurant.application.query.repository.RestaurantQueryRepository;
import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.application.result.RestaurantSummaryResult;
import com.michelet.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurant.domain.model.Restaurant;
import com.michelet.restaurant.domain.repository.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantQueryRepository restaurantQueryRepository;

    public RestaurantQueryService(RestaurantRepository restaurantRepository, RestaurantQueryRepository restaurantQueryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantQueryRepository = restaurantQueryRepository;
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

    /**
     * 식당 목록/검색 결과를 페이지 단위로 조회
     *
     * QueryDSL 기반 Query Repository에 검색 조건과 Pageable을 전달해
     * 목록 조회 및 검색 조회를 공통 처리
     */
    public Page<RestaurantSummaryResult> getRestaurants(RestaurantSearchCondition condition, Pageable pageable) {

        return restaurantQueryRepository.search(condition, pageable);
    }
}