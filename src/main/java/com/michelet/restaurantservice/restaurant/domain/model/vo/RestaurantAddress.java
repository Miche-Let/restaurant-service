package com.michelet.restaurantservice.restaurant.domain.model.vo;

public record RestaurantAddress(String value) {

    public RestaurantAddress {
        value = normalize(value);
        validate(value);
    }

    public static RestaurantAddress of(String value) {
        return new RestaurantAddress(value);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("식당 주소는 필수입니다.");
        }

        if (value.length() > 255) {
            throw new IllegalArgumentException("식당 주소는 255자를 초과할 수 없습니다.");
        }
    }
}
