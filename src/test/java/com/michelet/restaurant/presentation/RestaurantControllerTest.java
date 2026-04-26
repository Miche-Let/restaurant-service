package com.michelet.restaurant.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.michelet.restaurant.RestDocsConfig;
import com.michelet.restaurant.application.RestaurantQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("removal")
@WebMvcTest(RestaurantController.class)
@AutoConfigureRestDocs
@Import(RestDocsConfig.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantQueryService restaurantQueryService;

    @Test
    void healthCheck() throws Exception {
        given(restaurantQueryService.getHealthStatus())
                .willReturn("restaurant-service is healthy");

        mockMvc.perform(get("/api/v1/restaurants/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("restaurant-service is healthy"))
                .andExpect(jsonPath("$.message").value("Restaurant Query Service is running"))
                .andDo(document("restaurant-health-check",
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("data").description("헬스체크 결과"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }
}