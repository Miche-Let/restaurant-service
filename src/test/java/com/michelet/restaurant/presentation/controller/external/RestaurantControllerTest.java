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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;


import java.time.LocalTime;
import java.util.UUID;

import com.michelet.restaurant.application.result.CreateRestaurantResult;
import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.application.service.command.RestaurantCommandService;
import com.michelet.restaurant.application.service.query.RestaurantQueryService;
import com.michelet.restaurant.domain.model.RestaurantStatus;
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

    @Test
    @DisplayName("외부 식당 상세 정보를 조회할 수 있다")
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
                .andExpect(jsonPath("$.data.businessHours").value("MON-FRI 11:00-20:00 / SAT,SUN CLOSED"))
                .andDo(document("get-restaurant",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("data.restaurantId").description("식당 ID"),
                                fieldWithPath("data.name").description("식당 이름"),
                                fieldWithPath("data.address").description("식당 주소"),
                                fieldWithPath("data.phone").description("식당 전화번호"),
                                fieldWithPath("data.description").description("식당 설명").optional(),
                                fieldWithPath("data.reservationOpenAt").description("예약 오픈 시각"),
                                fieldWithPath("data.avgMealDurationMin").description("평균 식사 시간(분)"),
                                fieldWithPath("data.status").description("식당 상태"),
                                fieldWithPath("data.businessHours").description("운영시간 정보"),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }
}