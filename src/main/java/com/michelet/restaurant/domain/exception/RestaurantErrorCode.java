package com.michelet.restaurant.domain.exception;

import com.michelet.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RestaurantErrorCode implements ErrorCode {

    RESTAURANT_404_NOT_FOUND("RESTAURANT_404_NOT_FOUND", "식당을 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    RESTAURANT_401_INVALID_AUTH_USER_ID("RESTAURANT_401_INVALID_AUTH_USER_ID","인증 사용자 ID 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED.value());

    private final String code;
    private final String message;
    private final int httpStatus;

    RestaurantErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
