package com.michelet.restaurant.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode(of = "value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantPhone {

    @Column(name = "phone", nullable = false, length = 30)
    private String value;

    private RestaurantPhone(String value) {
        String normalized = normalize(value);
        validate(normalized);
        this.value = normalized;
    }

    public static RestaurantPhone of(String value) {
        return new RestaurantPhone(value);
    }

    private void validate(String normalized) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("식당 전화번호는 필수입니다.");
        }

        if (value.length() > 30) {
            throw new IllegalArgumentException("식당 전화번호는 30자를 초과할 수 없습니다.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
