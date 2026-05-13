package com.michelet.restaurantservice.restaurant.application.service;

import com.michelet.restaurantservice.course.domain.repository.RestaurantCourseRepository;
import com.michelet.restaurantservice.restaurant.application.query.repository.RestaurantQueryRepository;
import com.michelet.restaurantservice.restaurant.domain.model.Restaurant;
import com.michelet.restaurantservice.restaurant.domain.model.RestaurantStatus;
import com.michelet.restaurantservice.restaurant.domain.repository.RestaurantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {
        RestaurantQueryService.class,
        RestaurantQueryServiceCacheTest.CacheTestConfig.class
})
class RestaurantQueryServiceCacheTest {

    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantCourseRepository restaurantCourseRepository;
    private final CacheManager cacheManager;

    @Autowired
    RestaurantQueryServiceCacheTest(
            RestaurantQueryService restaurantQueryService,
            RestaurantRepository restaurantRepository,
            RestaurantCourseRepository restaurantCourseRepository,
            CacheManager cacheManager
    ) {
        this.restaurantQueryService = restaurantQueryService;
        this.restaurantRepository = restaurantRepository;
        this.restaurantCourseRepository = restaurantCourseRepository;
        this.cacheManager = cacheManager;
    }

    @AfterEach
    void tearDown() {
        Cache restaurantDetailCache = cacheManager.getCache("restaurantDetail");
        Cache restaurantCoursesCache = cacheManager.getCache("restaurantCourses");

        if (restaurantDetailCache != null) {
            restaurantDetailCache.clear();
        }

        if (restaurantCoursesCache != null) {
            restaurantCoursesCache.clear();
        }

        Mockito.reset(restaurantRepository, restaurantCourseRepository);
    }

    @Test
    @DisplayName("식당 상세 조회는 동일 restaurantId로 재조회 시 캐시를 사용한다")
    void 식당상세조회는동일RestaurantId로재조회시캐시를사용한다() {
        UUID restaurantId = UUID.randomUUID();

        Restaurant restaurant = Restaurant.create(
                UUID.randomUUID(),
                "Redis Cache Test Dining",
                "서울특별시 강남구 테헤란로 123",
                "02-1234-5678",
                "Redis 캐시 테스트용 식당",
                LocalTime.of(10, 0),
                90,
                RestaurantStatus.OPEN,
                "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
        );

        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        given(restaurantCourseRepository.findAllByRestaurantIdOrderByCreatedAtAsc(restaurantId))
                .willReturn(List.of());

        // 첫 번째 호출: cache miss → repository 조회 발생
        var firstResult = restaurantQueryService.getRestaurant(restaurantId);

        // 두 번째 호출: cache hit → repository 조회가 다시 발생하면 안 됨
        var secondResult = restaurantQueryService.getRestaurant(restaurantId);

        // cache hit 결과가 최초 조회 결과와 동일한지 확인
        assertThat(secondResult).isEqualTo(firstResult);

        then(restaurantRepository)
                .should(times(1))
                .findById(restaurantId);

        then(restaurantCourseRepository)
                .should(times(1))
                .findAllByRestaurantIdOrderByCreatedAtAsc(restaurantId);
    }

    @EnableCaching
    @TestConfiguration
    static class CacheTestConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("restaurantDetail", "restaurantCourses");
        }

        @Bean
        RestaurantRepository restaurantRepository() {
            return Mockito.mock(RestaurantRepository.class);
        }

        @Bean
        RestaurantCourseRepository restaurantCourseRepository() {
            return Mockito.mock(RestaurantCourseRepository.class);
        }

        @Bean
        RestaurantQueryRepository restaurantQueryRepository() {
            return Mockito.mock(RestaurantQueryRepository.class);
        }
    }
}
