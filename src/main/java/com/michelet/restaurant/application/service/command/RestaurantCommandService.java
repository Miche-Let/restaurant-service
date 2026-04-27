package com.michelet.restaurant.application.service.command;

import com.michelet.restaurant.application.command.CreateRestaurantCommand;
import com.michelet.restaurant.application.result.CreateRestaurantResult;
import com.michelet.restaurant.domain.model.Restaurant;
import com.michelet.restaurant.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantCommandService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantCommandService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * 식당 기본 정보 등록
     * 현재 단계에선 전달받은 ownerId를 그대로 사용
     * 추후 인증인가 붙으면 전달 해더 기반으로 대체
     */
    @Transactional
    public CreateRestaurantResult createRestaurant(CreateRestaurantCommand command) {
        Restaurant restaurant = Restaurant.create(
                command.ownerId(),
                command.name(),
                command.address(),
                command.phone(),
                command.description(),
                command.reservationOpenAt(),
                command.avgMealDurationMin(),
                command.status(),
                command.businessHours()
        );

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return CreateRestaurantResult.from(savedRestaurant);
    }
}
