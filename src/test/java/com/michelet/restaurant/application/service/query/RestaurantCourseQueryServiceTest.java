package com.michelet.restaurant.application.service.query;


import com.michelet.restaurant.application.result.CourseListItemResult;
import com.michelet.restaurant.domain.model.CoursePart;
import com.michelet.restaurant.domain.model.CourseSessionType;
import com.michelet.restaurant.domain.model.CourseStatus;
import com.michelet.restaurant.domain.model.Restaurant;
import com.michelet.restaurant.domain.model.RestaurantCourse;
import com.michelet.restaurant.domain.model.RestaurantCourseMenu;
import com.michelet.restaurant.domain.model.RestaurantStatus;
import com.michelet.restaurant.domain.repository.RestaurantCourseMenuRepository;
import com.michelet.restaurant.domain.repository.RestaurantCourseRepository;
import com.michelet.restaurant.domain.repository.RestaurantRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RestaurantCourseQueryServiceTest {

    @InjectMocks
    private RestaurantCourseQueryService restaurantCourseQueryService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantCourseRepository restaurantCourseRepository;

    @Mock
    private RestaurantCourseMenuRepository restaurantCourseMenuRepository;

    @Test
    @DisplayName("코스 목록 조회")
    void 코스목록조회() {
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
        ReflectionTestUtils.setField(restaurant, "restaurantId", restaurantId);

        RestaurantCourse dinnerCourse = RestaurantCourse.create(
                restaurantId,
                "Dinner Course",
                150000L,
                "아뮤즈 부쉬: 한우 타르타르 / 애피타이저: 제철 샐러드 / 메인: 양갈비",
                CourseSessionType.DINNER,
                CourseStatus.AVAILABLE
        );

        RestaurantCourse lunchCourse = RestaurantCourse.create(
                restaurantId,
                "Lunch Course",
                90000L,
                "애피타이저: 제철 샐러드 / 메인: 광어구이",
                CourseSessionType.LUNCH,
                CourseStatus.AVAILABLE
        );

        RestaurantCourseMenu dinnerMenu1 = RestaurantCourseMenu.create(
                dinnerCourse.getCourseId(),
                CoursePart.AMUSE_BOUCHE,
                "한우 타르타르",
                1
        );

        RestaurantCourseMenu dinnerMenu2 = RestaurantCourseMenu.create(
                dinnerCourse.getCourseId(),
                CoursePart.APPETIZER,
                "제철 샐러드",
                2
        );

        RestaurantCourseMenu dinnerMenu3 = RestaurantCourseMenu.create(
                dinnerCourse.getCourseId(),
                CoursePart.MAIN,
                "양갈비",
                3
        );

        RestaurantCourseMenu lunchMenu1 = RestaurantCourseMenu.create(
                lunchCourse.getCourseId(),
                CoursePart.APPETIZER,
                "제철 샐러드",
                1
        );

        RestaurantCourseMenu lunchMenu2 = RestaurantCourseMenu.create(
                lunchCourse.getCourseId(),
                CoursePart.FISH,
                "광어구이",
                2
        );

        List<UUID> courseIds = List.of(
                dinnerCourse.getCourseId(),
                lunchCourse.getCourseId()
        );

        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        given(restaurantCourseRepository.findAllByRestaurantIdOrderByCreatedAtAsc(restaurantId))
                .willReturn(List.of(dinnerCourse, lunchCourse));

        given(restaurantCourseMenuRepository.findAllByCourseIdInOrderByCourseIdAscSortOrderAsc(courseIds))
                .willReturn(List.of(
                        dinnerMenu1,
                        dinnerMenu2,
                        dinnerMenu3,
                        lunchMenu1,
                        lunchMenu2
                ));

        List<CourseListItemResult> results = restaurantCourseQueryService.getCourses(restaurantId);

        assertThat(results).hasSize(2);

        CourseListItemResult dinnerResult = results.get(0);
        assertThat(dinnerResult.courseId()).isEqualTo(dinnerCourse.getCourseId());
        assertThat(dinnerResult.name()).isEqualTo("Dinner Course");
        assertThat(dinnerResult.price()).isEqualTo(150000L);
        assertThat(dinnerResult.menuComposition())
                .isEqualTo("아뮤즈 부쉬: 한우 타르타르 / 애피타이저: 제철 샐러드 / 메인: 양갈비");
        assertThat(dinnerResult.sessionType()).isEqualTo(CourseSessionType.DINNER);
        assertThat(dinnerResult.status()).isEqualTo(CourseStatus.AVAILABLE);
        assertThat(dinnerResult.menus()).hasSize(3);
        assertThat(dinnerResult.menus().get(0).coursePart()).isEqualTo(CoursePart.AMUSE_BOUCHE);
        assertThat(dinnerResult.menus().get(0).menuName()).isEqualTo("한우 타르타르");
        assertThat(dinnerResult.menus().get(0).sortOrder()).isEqualTo(1);

        CourseListItemResult lunchResult = results.get(1);
        assertThat(lunchResult.courseId()).isEqualTo(lunchCourse.getCourseId());
        assertThat(lunchResult.name()).isEqualTo("Lunch Course");
        assertThat(lunchResult.price()).isEqualTo(90000L);
        assertThat(lunchResult.menuComposition())
                .isEqualTo("애피타이저: 제철 샐러드 / 메인: 광어구이");
        assertThat(lunchResult.sessionType()).isEqualTo(CourseSessionType.LUNCH);
        assertThat(lunchResult.status()).isEqualTo(CourseStatus.AVAILABLE);
        assertThat(lunchResult.menus()).hasSize(2);
        assertThat(lunchResult.menus().get(0).coursePart()).isEqualTo(CoursePart.APPETIZER);
        assertThat(lunchResult.menus().get(0).menuName()).isEqualTo("제철 샐러드");
        assertThat(lunchResult.menus().get(0).sortOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("코스가 없으면 빈 목록 반환")
    void 코스가없으면_빈목록반환() {
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
        ReflectionTestUtils.setField(restaurant, "restaurantId", restaurantId);

        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        given(restaurantCourseRepository.findAllByRestaurantIdOrderByCreatedAtAsc(restaurantId))
                .willReturn(List.of());

        List<CourseListItemResult> results = restaurantCourseQueryService.getCourses(restaurantId);

        assertThat(results).isEmpty();

        verify(restaurantCourseMenuRepository, never())
                .findAllByCourseIdInOrderByCourseIdAscSortOrderAsc(List.of());
    }
}