package com.michelet.restaurant.application.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.michelet.restaurant.application.command.CreateCourseCommand;
import com.michelet.restaurant.application.command.CreateCourseMenuCommand;
import com.michelet.restaurant.application.result.CourseResult;
import com.michelet.restaurant.domain.exception.CourseErrorCode;
import com.michelet.restaurant.domain.exception.CourseException;
import com.michelet.restaurant.domain.exception.RestaurantException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantCourseCommandServiceTest {

    @InjectMocks
    private RestaurantCourseCommandService restaurantCourseCommandService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantCourseRepository restaurantCourseRepository;

    @Mock
    private RestaurantCourseMenuRepository restaurantCourseMenuRepository;

    @Test
    @DisplayName("코스 등록")
    void 코스등록() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = createRestaurant();

        CreateCourseCommand command = new CreateCourseCommand(
                restaurantId,
                "Dinner Course",
                150000L,
                CourseSessionType.DINNER,
                CourseStatus.AVAILABLE,
                List.of(
                        new CreateCourseMenuCommand(
                                CoursePart.MAIN,
                                "양갈비",
                                2
                        ),
                        new CreateCourseMenuCommand(
                                CoursePart.APPETIZER,
                                "제철 샐러드",
                                1
                        )
                )
        );

        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        given(restaurantCourseRepository.save(any(RestaurantCourse.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(restaurantCourseMenuRepository.saveAll(anyList()))
                .willAnswer(invocation -> invocation.getArgument(0));

        CourseResult result = restaurantCourseCommandService.createCourse(command);

        ArgumentCaptor<RestaurantCourse> courseCaptor = ArgumentCaptor.forClass(RestaurantCourse.class);
        then(restaurantCourseRepository).should().save(courseCaptor.capture());

        RestaurantCourse savedCourse = courseCaptor.getValue();

        assertThat(result.courseId()).isEqualTo(savedCourse.getCourseId());
        assertThat(result.restaurantId()).isEqualTo(restaurantId);

        assertThat(savedCourse.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(savedCourse.getName()).isEqualTo("Dinner Course");
        assertThat(savedCourse.getPrice()).isEqualTo(150000L);
        assertThat(savedCourse.getSessionType()).isEqualTo(CourseSessionType.DINNER);
        assertThat(savedCourse.getStatus()).isEqualTo(CourseStatus.AVAILABLE);
        assertThat(savedCourse.getMenuComposition())
                .isEqualTo("애피타이저: 제철 샐러드 / 메인: 양갈비");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RestaurantCourseMenu>> courseMenusCaptor =
                ArgumentCaptor.forClass(List.class);

        then(restaurantCourseMenuRepository).should().saveAll(courseMenusCaptor.capture());

        List<RestaurantCourseMenu> savedCourseMenus = courseMenusCaptor.getValue();

        assertThat(savedCourseMenus).hasSize(2);
        assertThat(savedCourseMenus)
                .extracting(RestaurantCourseMenu::getCourseId)
                .containsOnly(savedCourse.getCourseId());
        assertThat(savedCourseMenus)
                .extracting(RestaurantCourseMenu::getSortOrder)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @DisplayName("없는 식당이면 예외 발생")
    void 없는식당이면_예외발생() {
        UUID restaurantId = UUID.randomUUID();

        CreateCourseCommand command = new CreateCourseCommand(
                restaurantId,
                "Dinner Course",
                150000L,
                CourseSessionType.DINNER,
                CourseStatus.AVAILABLE,
                List.of(
                        new CreateCourseMenuCommand(
                                CoursePart.APPETIZER,
                                "제철 샐러드",
                                1
                        )
                )
        );

        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.empty());

        RestaurantException exception = assertThrows(
                RestaurantException.class,
                () -> restaurantCourseCommandService.createCourse(command)
        );

        assertThat(exception.getErrorCode()).isEqualTo("RESTAURANT_404_NOT_FOUND");

        then(restaurantCourseRepository).should(never()).save(any(RestaurantCourse.class));
        then(restaurantCourseMenuRepository).should(never()).saveAll(anyList());
    }

    @Test
    @DisplayName("정렬 순서가 중복되면 예외 발생")
    void 정렬순서가중복되면_예외발생() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = createRestaurant();

        CreateCourseCommand command = new CreateCourseCommand(
                restaurantId,
                "Dinner Course",
                150000L,
                CourseSessionType.DINNER,
                CourseStatus.AVAILABLE,
                List.of(
                        new CreateCourseMenuCommand(
                                CoursePart.APPETIZER,
                                "제철 샐러드",
                                1
                        ),
                        new CreateCourseMenuCommand(
                                CoursePart.MAIN,
                                "양갈비",
                                1
                        )
                )
        );

        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        CourseException exception = assertThrows(
                CourseException.class,
                () -> restaurantCourseCommandService.createCourse(command)
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(CourseErrorCode.COURSE_400_INVALID_REQUEST.getCode());

        then(restaurantCourseRepository).should(never()).save(any(RestaurantCourse.class));
        then(restaurantCourseMenuRepository).should(never()).saveAll(anyList());
    }

    @Test
    @DisplayName("코스 메뉴 항목이 null이면 예외 발생")
    void 코스메뉴항목이null이면_예외발생() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = createRestaurant();

        CreateCourseCommand command = new CreateCourseCommand(
                restaurantId,
                "Dinner Course",
                150000L,
                CourseSessionType.DINNER,
                CourseStatus.AVAILABLE,
                Collections.singletonList(null)
        );

        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        CourseException exception = assertThrows(
                CourseException.class,
                () -> restaurantCourseCommandService.createCourse(command)
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(CourseErrorCode.COURSE_400_INVALID_REQUEST.getCode());

        then(restaurantCourseRepository).should(never()).save(any(RestaurantCourse.class));
        then(restaurantCourseMenuRepository).should(never()).saveAll(anyList());
    }


    private Restaurant createRestaurant() {
        return Restaurant.create(
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
    }
}