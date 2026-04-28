package com.michelet.restaurant.domain.exception;

import com.michelet.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RestaurantErrorCode implements ErrorCode {

    RESTAURANT_404_NOT_FOUND("RESTAURANT_404_NOT_FOUND", "식당을 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());

    private final String code;
    private final String message;
    private final int httpStatus;

    RestaurantErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
