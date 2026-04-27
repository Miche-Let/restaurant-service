package com.michelet.restaurant.presentation.controller.external;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;


import java.util.UUID;

import com.michelet.restaurant.application.result.CreateRestaurantResult;
import com.michelet.restaurant.application.service.command.RestaurantCommandService;
import com.michelet.restaurant.application.service.query.RestaurantQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @MockitoBean
    private RestaurantQueryService restaurantQueryService;

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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId.toString()))
                .andExpect(jsonPath("$.data.ownerId").value(ownerId.toString()))
                .andDo(document("create-restaurant",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("X-User-Id").description("식당 소유자 사용자 ID(UUID)")
                        ),
                        requestFields(
                                fieldWithPath("name").description("식당 이름"),
                                fieldWithPath("address").description("식당 주소"),
                                fieldWithPath("phone").description("식당 전화번호"),
                                fieldWithPath("description").description("식당 설명").optional(),
                                fieldWithPath("reservationOpenAt").description("예약 오픈 시각"),
                                fieldWithPath("avgMealDurationMin").description("평균 식사 시간(분)"),
                                fieldWithPath("status").description("식당 상태"),
                                fieldWithPath("businessHours").description("운영시간 정보")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("data.restaurantId").description("생성된 식당 ID"),
                                fieldWithPath("data.ownerId").description("식당 소유자 ID"),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }
}