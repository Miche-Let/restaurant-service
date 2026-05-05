package com.michelet.restaurantservice.course.domain.exception;

import com.michelet.common.exception.BusinessException;
import com.michelet.common.exception.ErrorCode;

public class CourseException extends BusinessException {

    public CourseException(ErrorCode errorCode) {
        super(errorCode);
    }
}
