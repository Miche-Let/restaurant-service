package com.michelet.restaurant.presentation.controller.internal;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.michelet.common.exception.GlobalExceptionHandler;
import com.michelet.restaurant.application.service.query.RestaurantQueryService;
import com.michelet.restaurant.application.result.GetRestaurantResult;
import com.michelet.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurant.domain.model.RestaurantStatus;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 내부 식당 조회 컨트롤러 테스트
 *
 * 내부 식당 단건 조회 API의 정상 응답과 REST Docs 스니펫 생성을 검증
 */
@WebMvcTest(RestaurantInternalController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureRestDocs(
        uriScheme = "http",
        uriHost = "localhost",
        uriPort = 19300
)
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
                .andExpect(jsonPath("$.data.businessHours").value("MON-FRI 11:00-20:00 / SAT,SUN CLOSED"))
                .andDo(document("get-internal-restaurant",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("data.restaurantId").description("식당 ID"),
                                fieldWithPath("data.ownerId").description("식당 소유자 ID"),
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
                .andExpect(jsonPath("$.message").value("식당을 찾을 수 없습니다."))
                .andDo(document("get-internal-restaurant-not-found",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }
}