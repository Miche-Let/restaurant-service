package com.michelet.restaurant.domain.exception;

import com.michelet.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CourseErrorCode implements ErrorCode {

    COURSE_400_INVALID_REQUEST("COURSE_400_INVALID_REQUEST", "코스요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST.value());

    private final String code;
    private final String message;
    private final int httpStatus;

    CourseErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
