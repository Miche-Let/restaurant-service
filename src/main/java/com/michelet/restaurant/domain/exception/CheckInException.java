package com.michelet.restaurant.domain.exception;

import com.michelet.common.exception.BusinessException;
import com.michelet.common.exception.ErrorCode;

public class CheckInException extends BusinessException {

    public CheckInException(ErrorCode errorCode) {
        super(errorCode);
    }
}
