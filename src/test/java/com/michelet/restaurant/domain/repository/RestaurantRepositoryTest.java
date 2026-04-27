package com.michelet.restaurant.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelet.restaurant.domain.model.Restaurant;
import com.michelet.restaurant.domain.model.RestaurantStatus;
import com.michelet.restaurant.infrastructure.config.JpaAuditingConfig;
import com.michelet.restaurant.infrastructure.persistence.RestaurantJpaRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.beans.factory.annotation.Autowired;

@DataJpaTest
@Import({
        JpaAuditingConfig.class,
        RestaurantRepositoryTest.JpaAuditingTestConfig.class
})
class RestaurantRepositoryTest {

    @Autowired
    private RestaurantJpaRepository restaurantRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("식당을 저장하고 ID로 조회할 수 있다")
    void saveAndFindRestaurant() {
        UUID ownerId = UUID.randomUUID();

        Restaurant restaurant = Restaurant.create(
                ownerId,
                "MicheLet Dining",
                "서울특별시 강남구 테헤란로 123",
                "02-1234-5678",
                "파인다이닝 레스토랑",
                LocalTime.of(10, 0),
                90,
                RestaurantStatus.OPEN,
                "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
        );

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        entityManager.flush();
        entityManager.clear();

        Restaurant foundRestaurant = restaurantRepository.findById(savedRestaurant.getRestaurantId())
                .orElseThrow();

        assertThat(foundRestaurant.getRestaurantId()).isNotNull();
        assertThat(foundRestaurant.getOwnerId()).isEqualTo(ownerId);
        assertThat(foundRestaurant.getName()).isEqualTo("MicheLet Dining");
        assertThat(foundRestaurant.getCreatedAt()).isNotNull();
        assertThat(foundRestaurant.getCreatedBy()).isNotNull();
    }

    @Test
    @DisplayName("ownerId로 식당 목록을 조회할 수 있다")
    void findAllByOwnerId() {
        UUID ownerId = UUID.randomUUID();
        UUID anotherOwnerId = UUID.randomUUID();

        Restaurant firstRestaurant = Restaurant.create(
                ownerId,
                "MicheLet Lunch",
                "서울특별시 강남구 1",
                "02-1111-1111",
                "런치 중심 레스토랑",
                LocalTime.of(10, 0),
                60,
                RestaurantStatus.OPEN,
                "MON-FRI 11:00-15:00"
        );

        Restaurant secondRestaurant = Restaurant.create(
                ownerId,
                "MicheLet Dinner",
                "서울특별시 강남구 2",
                "02-2222-2222",
                "디너 중심 레스토랑",
                LocalTime.of(17, 0),
                120,
                RestaurantStatus.OPEN,
                "MON-FRI 17:00-22:00"
        );

        Restaurant anotherOwnersRestaurant = Restaurant.create(
                anotherOwnerId,
                "Another Owner Restaurant",
                "서울특별시 서초구 1",
                "02-3333-3333",
                "다른 사장 식당",
                LocalTime.of(12, 0),
                80,
                RestaurantStatus.OPEN,
                "MON-FRI 12:00-21:00"
        );

        restaurantRepository.save(firstRestaurant);
        restaurantRepository.save(secondRestaurant);
        restaurantRepository.save(anotherOwnersRestaurant);

        entityManager.flush();
        entityManager.clear();

        var restaurants = restaurantRepository.findAllByOwnerId(ownerId);

        assertThat(restaurants).hasSize(2);
        assertThat(restaurants)
                .extracting(Restaurant::getName)
                .containsExactlyInAnyOrder("MicheLet Lunch", "MicheLet Dinner")
                .doesNotContain("Another Owner Restaurant");
    }

    @TestConfiguration
    @EnableJpaAuditing(auditorAwareRef = "auditorAware")
    static class JpaAuditingTestConfig {
    }
}