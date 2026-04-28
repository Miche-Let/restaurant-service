package com.michelet.restaurant.presentation.controller.internal;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.michelet.common.exception.GlobalExceptionHandler;
import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.application.service.query.RestaurantQueryService;
import com.michelet.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurant.domain.model.RestaurantStatus;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 내부 식당 조회 컨트롤러 테스트
 *
 * 내부 식당 단건 조회 API의 정상 응답과 예외 응답을 검증
 */
@WebMvcTest(RestaurantInternalController.class)
@Import(GlobalExceptionHandler.class)
class RestaurantInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantQueryService restaurantQueryService;

    @Test
    @DisplayName("내부 식당 조회")
    void 내부식당조회() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        given(restaurantQueryService.getRestaurant(restaurantId))
                .willReturn(new GetRestaurantResult(
                        restaurantId,
                        ownerId,
                        "MicheLet Dining",
                        "서울특별시 강남구 테헤란로 123",
                        "02-1234-5678",
                        "파인다이닝 레스토랑",
                        LocalTime.of(10, 0),
                        90,
                        RestaurantStatus.OPEN,
                        "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
                ));

        mockMvc.perform(get("/internal/v1/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId.toString()))
                .andExpect(jsonPath("$.data.ownerId").value(ownerId.toString()))
                .andExpect(jsonPath("$.data.name").value("MicheLet Dining"))
                .andExpect(jsonPath("$.data.address").value("서울특별시 강남구 테헤란로 123"))
                .andExpect(jsonPath("$.data.phone").value("02-1234-5678"))
                .andExpect(jsonPath("$.data.description").value("파인다이닝 레스토랑"))
                .andExpect(jsonPath("$.data.reservationOpenAt").value("10:00:00"))
                .andExpect(jsonPath("$.data.avgMealDurationMin").value(90))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andExpect(jsonPath("$.data.businessHours").value("MON-FRI 11:00-20:00 / SAT,SUN CLOSED"));
    }

    @Test
    @DisplayName("내부 식당 조회 실패")
    void 내부식당조회실패() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        given(restaurantQueryService.getRestaurant(restaurantId))
                .willThrow(new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));

        mockMvc.perform(get("/internal/v1/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("RESTAURANT_404_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("식당을 찾을 수 없습니다."));
    }
}