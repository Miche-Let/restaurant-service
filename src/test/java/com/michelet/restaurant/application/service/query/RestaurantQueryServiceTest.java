package com.michelet.restaurant.application.service.query;

import com.michelet.restaurant.application.query.repository.RestaurantQueryRepository;
import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.domain.model.*;
import com.michelet.restaurant.domain.repository.RestaurantCourseRepository;
import com.michelet.restaurant.domain.repository.RestaurantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class RestaurantQueryServiceTest {

    @InjectMocks
    private RestaurantQueryService restaurantQueryService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantCourseRepository restaurantCourseRepository;

    @Mock
    private RestaurantQueryRepository restaurantQueryRepository;

    @Test
    @DisplayName("식당 상세 조회")
    void 식당상세조회() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = Restaurant.create(
                UUID.randomUUID(),
                "MicheLet Dining",
                "서울특별시 강남구 테헤란로 123",
                "02-1234-5678",
                "파인다이닝 레스토랑",
                LocalTime.of(10, 0),
                90,
                RestaurantStatus.OPEN,
                "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
        );

        RestaurantCourse course = RestaurantCourse.create(
                restaurantId,
                "Dinner Course",
                150000L,
                "애피타이저: 제철 샐러드 / 메인: 양갈비 / 디저트: 바닐라 무스",
                CourseSessionType.DINNER,
                CourseStatus.AVAILABLE
        );

        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        given(restaurantCourseRepository.findAllByRestaurantIdOrderByCreatedAtAsc(restaurantId))
                .willReturn(List.of(course));

        GetRestaurantResult result = restaurantQueryService.getRestaurant(restaurantId);

        assertThat(result.restaurantId()).isEqualTo(restaurant.getRestaurantId());
        assertThat(result.name()).isEqualTo("MicheLet Dining");
        assertThat(result.address()).isEqualTo("서울특별시 강남구 테헤란로 123");

        assertThat(result.courses()).hasSize(1);
        assertThat(result.courses().get(0).courseId()).isEqualTo(course.getCourseId());
        assertThat(result.courses().get(0).name()).isEqualTo("Dinner Course");
        assertThat(result.courses().get(0).price()).isEqualTo(150000L);
        assertThat(result.courses().get(0).menuComposition()).isEqualTo("애피타이저: 제철 샐러드 / 메인: 양갈비 / 디저트: 바닐라 무스");
        assertThat(result.courses().get(0).sessionType()).isEqualTo(CourseSessionType.DINNER);
        assertThat(result.courses().get(0).status()).isEqualTo(CourseStatus.AVAILABLE);

    }
}