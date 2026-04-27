package com.michelet.restaurant.presentation.controller.external;

import com.michelet.restaurant.application.result.CreateRestaurantResult;
import com.michelet.restaurant.application.service.command.RestaurantCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
@AutoConfigureRestDocs(
        uriScheme = "http",
        uriHost = "localhost",
        uriPort = 19300
)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantCommandService restaurantCommandService;

    @Test
    @DisplayName("식당을 등록할 수 있다")
    void createRestaurant() throws Exception {
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
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId.toString()))
                .andExpect(jsonPath("$.data.ownerId").value(ownerId.toString()))
                .andDo(document("create-restaurant",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}