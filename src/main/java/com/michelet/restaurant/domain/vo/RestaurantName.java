package com.michelet.restaurant.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class RestaurantName {

    @Column(name = "name", nullable = false, length = 100)
    private String value;

    protected RestaurantName() {

    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantName that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
