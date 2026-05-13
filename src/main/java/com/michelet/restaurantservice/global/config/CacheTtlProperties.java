package com.michelet.restaurantservice.global.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "cache.ttl")
public record CacheTtlProperties(
        @Positive
        long restaurantDetail,

        @Positive
        long restaurantCourses
) {
}
