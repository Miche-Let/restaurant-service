package com.michelet.restaurant.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@EqualsAndHashCode(of = "value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class RestaurantName {

    @Column(name = "name", nullable = false, length = 100)
    private String value;

    private RestaurantName(String value) {
        String normalizedValue = normalize(value);
        validate(normalizedValue);
        this.value = value;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("식당 이름은 필수입니다.");
        }

        if (value.length() > 100) {
            throw new IllegalArgumentException("식당 이름은 100자를 초과할 수 없습니다.");
        }
    }

}
