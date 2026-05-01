package com.michelet.restaurant.domain.exception;

import com.michelet.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CheckInErrorCode implements ErrorCode {

    CHECKIN_409_INVALID_STATUS("CHECKIN_409_INVALID_STATUS", "체크인할 수 없는 예약 상태입니다.", HttpStatus.CONFLICT.value()),

    CHECKIN_502_INVALID_RESERVATION_RESPONSE("CHECKIN_502_INVALID_RESERVATION_RESPONSE", "예약 서비스 체크인 응답이 올바르지 않습니다.", HttpStatus.BAD_GATEWAY.value());

    private final String code;
    private final String message;
    private final int httpStatus;

    CheckInErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
