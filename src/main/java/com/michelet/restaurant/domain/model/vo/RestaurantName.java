package com.michelet.restaurant.domain.model.vo;

public record RestaurantName(String value) {

    public RestaurantName {
        value = normalize(value);
        validate(value);
    }

    public static RestaurantName of(String value) {
        return new RestaurantName(value);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("식당 이름은 필수입니다.");
        }

        if (value.length() > 100) {
            throw new IllegalArgumentException("식당 이름은 100자를 초과할 수 없습니다.");
        }
    }
}