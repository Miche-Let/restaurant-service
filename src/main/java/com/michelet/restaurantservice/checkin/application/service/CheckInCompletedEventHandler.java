package com.michelet.restaurantservice.checkin.application.service;

import com.michelet.restaurantservice.checkin.domain.model.RestaurantCheckInLog;
import com.michelet.restaurantservice.checkin.domain.repository.RestaurantCheckInLogRepository;
import com.michelet.restaurantservice.checkin.infrastructure.kafka.event.subscribe.CheckInCompletedEvent;
import com.michelet.restaurantservice.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurantservice.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurantservice.restaurant.domain.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckInCompletedEventHandler {

    private static final Logger log = LoggerFactory.getLogger(CheckInCompletedEventHandler.class);

    private final RestaurantRepository restaurantRepository;
    private final RestaurantCheckInLogRepository restaurantCheckInLogRepository;

    public CheckInCompletedEventHandler(RestaurantRepository restaurantRepository, RestaurantCheckInLogRepository restaurantCheckInLogRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantCheckInLogRepository = restaurantCheckInLogRepository;
    }

    // 체크인 완료 이벤트를 처리
    // reservationId 기준으로 이미 저장된 체크인 로그가 있으면 중복 이벤트로 보고 저장x
    @Transactional
    public void handle(CheckInCompletedEvent event) {
        validateEvent(event);

        if (restaurantCheckInLogRepository.existsByReservationId(event.reservationId())) {
            log.info(
                    "중복 체크인 완료 이벤트를 스킵 eventId={}, reservationId={}, restaurantId={}",
                    event.eventId(),
                    event.reservationId(),
                    event.restaurantId()
            );
            return;
        }

        validateRestaurantExists(event);

        RestaurantCheckInLog checkInLog = RestaurantCheckInLog.createCheckedIn(
                event.restaurantId(),
                event.reservationId(),
                event.visitDate(),
                event.checkedInBy(),
                event.checkedInAt()
        );

        boolean saved = saveCheckInLog(checkInLog, event);

        if (!saved) {
            return;
        }

        log.info(
                "체크인 완료 이벤트 처리를 완료했습니다. eventId={}, reservationId={}, restaurantId={}, checkedInBy={}, checkedInAt={}",
                event.eventId(),
                event.reservationId(),
                event.restaurantId(),
                event.checkedInBy(),
                event.checkedInAt()
        );
    }

    /**
     * 이벤트 기본 계약 검증
     * 잘못된 이벤트가 들어오면 체크인 로그를 저장하지 않고 즉시 실패
     * Consumer 단계에서 실패 로그와 재처리 정책을 붙일 수 있도록 예외던짐
     */
    private void validateEvent(CheckInCompletedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("체크인 완료 이벤트는 필수입니다.");
        }

        if (!event.isCheckInCompleted()) {
            throw new IllegalArgumentException("지원하지 않는 이벤트 타입입니다. eventType=" + event.eventType());
        }

        validateRequired(event.eventId(), "eventId");
        validateRequired(event.reservationId(), "reservationId");
        validateRequired(event.restaurantId(), "restaurantId");
        validateRequired(event.visitDate(), "visitDate");
        validateRequired(event.checkedInBy(), "checkedInBy");
        validateRequired(event.checkedInAt(), "checkedInAt");
        validateRequired(event.eventCreatedAt(), "eventCreatedAt");
    }

    // 이벤트에 포함된 restaurantId가 restaurant-service에 존재하는 식당인지 확인

    private void validateRestaurantExists(CheckInCompletedEvent event) {
        restaurantRepository.findById(event.restaurantId())
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));
    }
    private void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("체크인 완료 이벤트 필수값이 누락되었습니다. field=" + fieldName);
        }
    }

    /**
     * 체크인 로그를 저장
     *
     * existsByReservationId() 확인 이후 save() 사이에 동일 reservationId 이벤트가 동시에 처리될 수 있음
     */
    private boolean saveCheckInLog(RestaurantCheckInLog checkInLog, CheckInCompletedEvent event) {
        try {
            restaurantCheckInLogRepository.save(checkInLog);
            return true;
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateReservationIdException(exception)) {
                log.info(
                        "동시에 처리된 중복 체크인 완료 이벤트를 스킵합니다. eventId={}, reservationId={}, restaurantId={}",
                        event.eventId(),
                        event.reservationId(),
                        event.restaurantId()
                );
                return false;
            }

            throw exception;
        }
    }

    // reservation_id unique constraint 위반 여부를 확인
    private boolean isDuplicateReservationIdException(Throwable exception) {
        Throwable current = exception;

        while (current != null) {
            String message = current.getMessage();

            if (message != null && (message.contains("uk_checkin_log_reservation_id") || message.contains("reservation_id"))) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}
