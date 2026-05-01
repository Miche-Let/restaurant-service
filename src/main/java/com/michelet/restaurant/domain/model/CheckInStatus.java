package com.michelet.restaurant.domain.model;

// 식당 체크인 이력 상태 표현
public enum CheckInStatus {

    // 체크인 대기
    PENDING,
    // 체크인 완료
    CHECKED_IN,
    // 방문하지 않은 상태
    NO_SHOW,
}
