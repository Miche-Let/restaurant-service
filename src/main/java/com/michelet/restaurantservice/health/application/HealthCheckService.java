package com.michelet.restaurantservice.health.application;

import org.springframework.stereotype.Service;

/**
 * 헬스체크 전용 서비스
 *
 * restaurant-service의 기동 상태 확인에 필요한 메시지를 반환한다.
 * 도메인 조회 로직과 분리해 헬스체크 책임만 담당한다.
 */
@Service
public class HealthCheckService {

    /**
     * 헬스체크용 상태 메시지를 반환한다.
     *
     * 현재 restaurant-service가 정상적으로 기동 중인지 확인할 때 사용한다.
     */
    public String getHealthStatus() {
        return "Restaurant Query Service is Healthy";
    }
}
