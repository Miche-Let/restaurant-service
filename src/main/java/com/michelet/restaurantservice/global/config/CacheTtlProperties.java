package com.michelet.restaurantservice.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache.ttl")
public record CacheTtlProperties(
        long restaurantDetail,
        long restaurantCourses
) {
}
