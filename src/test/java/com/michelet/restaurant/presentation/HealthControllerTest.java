package com.michelet.restaurant.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.michelet.restaurant.application.RestaurantQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(com.michelet.restaurant.presentation.RestaurantController.class)
@AutoConfigureRestDocs(
        uriScheme = "http",
        uriHost = "localhost",
        uriPort = 19300
)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantQueryService restaurantQueryService;

    @Test
    void healthCheck() throws Exception {
        given(restaurantQueryService.getHealthStatus())
                .willReturn("Restaurant Query Service is Healthy");

        mockMvc.perform(get("/api/v1/restaurants/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Restaurant Query Service is Healthy"))
                .andExpect(jsonPath("$.message").value("Restaurant Query Service is running"))
                .andDo(document("restaurant-health-check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}