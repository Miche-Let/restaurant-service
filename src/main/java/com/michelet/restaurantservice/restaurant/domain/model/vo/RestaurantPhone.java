package com.michelet.restaurantservice.restaurant.domain.model.vo;

public record RestaurantPhone(String value) {

    public RestaurantPhone {
        value = normalize(value);
        validate(value);
    }

    public static RestaurantPhone of(String value) {
        return new RestaurantPhone(value);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("식당 전화번호는 필수입니다.");
        }

        if (value.length() > 30) {
            throw new IllegalArgumentException("식당 전화번호는 30자를 초과할 수 없습니다.");
        }
    }
}