package com.michelet.restaurant.presentation.controller.external;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.michelet.common.exception.GlobalExceptionHandler;
import com.michelet.restaurant.application.query.RestaurantSearchCondition;
import com.michelet.restaurant.application.result.CreateRestaurantResult;
import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.application.result.RestaurantSummaryResult;
import com.michelet.restaurant.application.service.command.RestaurantCommandService;
import com.michelet.restaurant.application.service.query.RestaurantQueryService;
import com.michelet.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurant.domain.model.RestaurantStatus;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 외부 식당 API 컨트롤러 테스트
 * <p>
 * 식당 등록, 외부 식당 상세 조회, 예외 응답을 검증
 */
@WebMvcTest(RestaurantController.class)
@Import(GlobalExceptionHandler.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantCommandService restaurantCommandService;

    @MockitoBean
    private RestaurantQueryService restaurantQueryService;

    @Test
    @DisplayName("식당 등록")
    void 식당등록() throws Exception {
        UUID ownerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();

        given(restaurantCommandService.createRestaurant(any()))
                .willReturn(new CreateRestaurantResult(restaurantId, ownerId));

        String requestBody = """
                {
                  "name": "MicheLet Dining",
                  "address": "서울특별시 강남구 테헤란로 123",
                  "phone": "02-1234-5678",
                  "description": "파인다이닝 레스토랑",
                  "reservationOpenAt": "10:00:00",
                  "avgMealDurationMin": 90,
                  "status": "OPEN",
                  "businessHours": "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
                }
                """;

        mockMvc.perform(post("/api/v1/restaurants")
                        .header("X-User-Id", ownerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId.toString()))
                .andExpect(jsonPath("$.data.ownerId").value(ownerId.toString()));
    }

    @Test
    @DisplayName("식당 상세 조회")
    void 식당상세조회() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        given(restaurantQueryService.getRestaurant(restaurantId))
                .willReturn(new GetRestaurantResult(
                        restaurantId,
                        UUID.randomUUID(),
                        "MicheLet Dining",
                        "서울특별시 강남구 테헤란로 123",
                        "02-1234-5678",
                        "파인다이닝 레스토랑",
                        LocalTime.of(10, 0),
                        90,
                        RestaurantStatus.OPEN,
                        "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
                ));

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId.toString()))
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
    @DisplayName("식당 상세 조회 실패")
    void 식당상세조회실패() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        given(restaurantQueryService.getRestaurant(restaurantId))
                .willThrow(new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("RESTAURANT_404_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("식당을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("식당 목록 조회")
    void 식당목록조회() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 2);

        List<RestaurantSummaryResult> content = List.of(
                new RestaurantSummaryResult(
                        UUID.randomUUID(),
                        "MicheLet Dining",
                        "서울특별시 강남구 테헤란로 123",
                        "02-1234-5678",
                        RestaurantStatus.OPEN,
                        "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
                ),
                new RestaurantSummaryResult(
                        UUID.randomUUID(),
                        "MicheLet Bistro",
                        "서울특별시 성동구 연무장길 10",
                        "02-2222-3333",
                        RestaurantStatus.CLOSED,
                        "MON-FRI 12:00-22:00"
                )
        );

        given(restaurantQueryService.getRestaurants(any(RestaurantSearchCondition.class), any()))
                .willReturn(new PageImpl<>(content, pageRequest, 2));

        mockMvc.perform(get("/api/v1/restaurants")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name").value("MicheLet Dining"))
                .andExpect(jsonPath("$.data.content[0].status").value("OPEN"))
                .andExpect(jsonPath("$.data.content[1].name").value("MicheLet Bistro"))
                .andExpect(jsonPath("$.data.content[1].status").value("CLOSED"))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.number").value(0));
    }


    @Test
    @DisplayName("식당 검색 조회")
    void 식당검색조회() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<RestaurantSummaryResult> content = List.of(
                new RestaurantSummaryResult(
                        UUID.randomUUID(),
                        "MicheLet Dining",
                        "서울특별시 강남구 테헤란로 123",
                        "02-1234-5678",
                        RestaurantStatus.OPEN,
                        "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
                )
        );

        given(restaurantQueryService.getRestaurants(any(RestaurantSearchCondition.class), any()))
                .willReturn(new PageImpl<>(content, pageRequest, 1));

        mockMvc.perform(get("/api/v1/restaurants")
                        .param("name", "MicheLet")
                        .param("status", "OPEN")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("MicheLet Dining"))
                .andExpect(jsonPath("$.data.content[0].status").value("OPEN"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.number").value(0));
    }



}