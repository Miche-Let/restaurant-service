package com.michelet.restaurant.presentation.controller.external;


import com.michelet.common.exception.GlobalExceptionHandler;
import com.michelet.restaurant.application.result.CourseListItemResult;
import com.michelet.restaurant.application.result.CourseMenuResult;
import com.michelet.restaurant.application.service.command.RestaurantCourseCommandService;
import com.michelet.restaurant.application.service.query.RestaurantCourseQueryService;
import com.michelet.restaurant.domain.model.CoursePart;
import com.michelet.restaurant.domain.model.CourseSessionType;
import com.michelet.restaurant.domain.model.CourseStatus;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantCourseController.class)
@Import(GlobalExceptionHandler.class)
class RestaurantCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantCourseCommandService restaurantCourseCommandService;

    @MockitoBean
    private RestaurantCourseQueryService restaurantCourseQueryService;

    @Test
    @DisplayName("코스 목록 조회")
    void 코스목록조회() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        given(restaurantCourseQueryService.getCourses(restaurantId))
                .willReturn(List.of(
                        new CourseListItemResult(
                                courseId,
                                "Dinner Course",
                                150000L,
                                "아뮤즈 부쉬: 한우 타르타르 / 애피타이저: 제철 샐러드 / 생선: 제철 생선 구이 / 메인: 양갈비 / 디저트: 바닐라 무스",
                                CourseSessionType.DINNER,
                                CourseStatus.AVAILABLE,
                                List.of(
                                        new CourseMenuResult(
                                                CoursePart.AMUSE_BOUCHE,
                                                "한우 타르타르",
                                                1
                                        ),
                                        new CourseMenuResult(
                                                CoursePart.APPETIZER,
                                                "제철 샐러드",
                                                2
                                        ),
                                        new CourseMenuResult(
                                                CoursePart.FISH,
                                                "제철 생선 구이",
                                                3
                                        ),
                                        new CourseMenuResult(
                                                CoursePart.MAIN,
                                                "양갈비",
                                                4
                                        ),
                                        new CourseMenuResult(
                                                CoursePart.DESSERT,
                                                "바닐라 무스",
                                                5
                                        )
                                )
                        )
                ));

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/courses", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].courseId").value(courseId.toString()))
                .andExpect(jsonPath("$.data[0].name").value("Dinner Course"))
                .andExpect(jsonPath("$.data[0].price").value(150000))
                .andExpect(jsonPath("$.data[0].menuComposition")
                        .value("아뮤즈 부쉬: 한우 타르타르 / 애피타이저: 제철 샐러드 / 생선: 제철 생선 구이 / 메인: 양갈비 / 디저트: 바닐라 무스"))
                .andExpect(jsonPath("$.data[0].sessionType").value("DINNER"))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.data[0].menus.length()").value(5))
                .andExpect(jsonPath("$.data[0].menus[0].coursePart").value("AMUSE_BOUCHE"))
                .andExpect(jsonPath("$.data[0].menus[0].menuName").value("한우 타르타르"))
                .andExpect(jsonPath("$.data[0].menus[0].sortOrder").value(1))
                .andExpect(jsonPath("$.data[0].menus[1].coursePart").value("APPETIZER"))
                .andExpect(jsonPath("$.data[0].menus[1].menuName").value("제철 샐러드"))
                .andExpect(jsonPath("$.data[0].menus[1].sortOrder").value(2))
                .andExpect(jsonPath("$.data[0].menus[4].coursePart").value("DESSERT"))
                .andExpect(jsonPath("$.data[0].menus[4].menuName").value("바닐라 무스"))
                .andExpect(jsonPath("$.data[0].menus[4].sortOrder").value(5));
    }
}