package com.michelet.restaurant.domain.exception;

import com.michelet.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CheckInErrorCode implements ErrorCode {

    CHECKIN_400_RESERVATION_CHECK_IN_FAILED("CHECKIN_400_RESERVATION_CHECK_IN_FAILED", "예약 체크인 처리에 실패했습니다.", HttpStatus.BAD_REQUEST.value()),
    CHECKIN_403_FORBIDDEN("CHECKIN_403_FORBIDDEN", "체크인 처리 권한이 없습니다.", HttpStatus.FORBIDDEN.value()),
    CHECKIN_404_RESERVATION_NOT_FOUND("CHECKIN_404_RESERVATION_NOT_FOUND", "예약을 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    CHECKIN_409_RESERVATION_CHECK_IN_FAILED("CHECKIN_409_RESERVATION_CHECK_IN_FAILED", "예약 체크인 처리에 실패했습니다.", HttpStatus.CONFLICT.value()),
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
