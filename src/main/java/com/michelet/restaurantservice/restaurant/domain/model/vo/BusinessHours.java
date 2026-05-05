package com.michelet.restaurantservice.restaurant.domain.model.vo;

public record BusinessHours(String value) {

    public BusinessHours {
        value = normalize(value);
        validate(value);
    }

    public static BusinessHours of(String value) {
        return new BusinessHours(value);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("운영시간 정보는 필수입니다.");
        }

        if (value.length() > 1000) {
            throw new IllegalArgumentException("운영시간 정보는 1000자를 초과할 수 없습니다.");
        }
    }
}
