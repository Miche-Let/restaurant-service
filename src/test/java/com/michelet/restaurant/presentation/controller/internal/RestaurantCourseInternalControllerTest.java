package com.michelet.restaurant.presentation.controller.internal;


import com.michelet.common.exception.GlobalExceptionHandler;
import com.michelet.restaurant.application.result.CourseSummaryResult;
import com.michelet.restaurant.application.service.query.RestaurantCourseQueryService;
import com.michelet.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurant.domain.exception.RestaurantException;
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

@WebMvcTest(RestaurantCourseInternalController.class)
@Import(GlobalExceptionHandler.class)
class RestaurantCourseInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantCourseQueryService restaurantCourseQueryService;

    @Test
    @DisplayName("내부 코스 목록 조회")
    void 내부코스목록조회() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        given(restaurantCourseQueryService.getInternalCourses(restaurantId))
                .willReturn(List.of(
                        new CourseSummaryResult(
                                courseId,
                                "Dinner Course",
                                150000L,
                                "아뮤즈 부쉬: 한우 타르타르 / 애피타이저: 제철 샐러드 / 생선: 제철 생선 구이 / 메인: 양갈비 / 디저트: 바닐라 무스",
                                CourseSessionType.DINNER,
                                CourseStatus.AVAILABLE
                        )
                ));

        mockMvc.perform(get("/internal/v1/restaurants/{restaurantId}/courses", restaurantId))
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
                .andExpect(jsonPath("$.data[0].menus").doesNotExist());
    }

    @Test
    @DisplayName("존재하지 않는 식당의 내부 코스 목록 조회")
    void 존재하지않는식당의_내부코스목록조회() throws Exception {
        UUID restaurantId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        given(restaurantCourseQueryService.getInternalCourses(restaurantId))
                .willThrow(new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));

        mockMvc.perform(get("/internal/v1/restaurants/{restaurantId}/courses", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}