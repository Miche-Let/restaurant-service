package com.michelet.restaurant.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = "value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class RestaurantAddress {

    @Column(name = "address", nullable = false, length = 255)
    private String value;

    private RestaurantAddress(String value) {
        String normalizedValue = normalize(value);
        validate(normalizedValue);
        this.value = normalizedValue;
    }

    public static RestaurantAddress of(String value) {
        return new RestaurantAddress(value);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("식당 주소는 필수입니다.");
        }

        if (value.length() > 255) {
            throw new IllegalArgumentException("식당 주소는 255자를 초과할 수 없습니다.");
        }
    }
}
