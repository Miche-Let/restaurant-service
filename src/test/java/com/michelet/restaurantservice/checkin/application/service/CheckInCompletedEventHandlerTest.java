package com.michelet.restaurantservice.checkin.application.service;

import com.michelet.restaurantservice.checkin.domain.model.RestaurantCheckInLog;
import com.michelet.restaurantservice.checkin.domain.repository.RestaurantCheckInLogRepository;
import com.michelet.restaurantservice.checkin.infrastructure.kafka.event.subscribe.CheckInCompletedEvent;
import com.michelet.restaurantservice.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurantservice.restaurant.domain.model.Restaurant;
import com.michelet.restaurantservice.restaurant.domain.model.RestaurantStatus;
import com.michelet.restaurantservice.restaurant.domain.repository.RestaurantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CheckInCompletedEventHandlerTest {

    @InjectMocks
    private CheckInCompletedEventHandler checkInCompletedEventHandler;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantCheckInLogRepository restaurantCheckInLogRepository;

    @Test
    @DisplayName("체크인 완료 이벤트 처리")
    void 체크인완료이벤트처리() {
        UUID eventId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID checkedInBy = UUID.randomUUID();
        LocalDate visitDate = LocalDate.of(2026, 5, 10);
        LocalDateTime checkedInAt = LocalDateTime.of(2026, 5, 10, 12, 30);
        LocalDateTime eventCreatedAt = LocalDateTime.of(2026, 5, 10, 12, 31);

        CheckInCompletedEvent event = new CheckInCompletedEvent(
                eventId,
                CheckInCompletedEvent.CHECK_IN_COMPLETED,
                reservationId,
                restaurantId,
                visitDate,
                checkedInBy,
                checkedInAt,
                eventCreatedAt
        );

        Restaurant restaurant = createRestaurant(restaurantId);

        // reservationId 기준으로 아직 처리된 체크인 로그가 없으면 저장 대상이다.
        given(restaurantCheckInLogRepository.existsByReservationId(reservationId))
                .willReturn(false);

        // 이벤트의 restaurantId가 restaurant-service에 존재하는 식당인지 검증한다.
        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        checkInCompletedEventHandler.handle(event);

        ArgumentCaptor<RestaurantCheckInLog> checkInLogCaptor = ArgumentCaptor.forClass(RestaurantCheckInLog.class);

        then(restaurantCheckInLogRepository)
                .should()
                .save(checkInLogCaptor.capture());

        RestaurantCheckInLog savedLog = checkInLogCaptor.getValue();

        assertThat(savedLog.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(savedLog.getReservationId()).isEqualTo(reservationId);
        assertThat(savedLog.getVisitDate()).isEqualTo(visitDate);
        assertThat(savedLog.getCheckedInBy()).isEqualTo(checkedInBy);
        assertThat(savedLog.getCheckedInAt()).isEqualTo(checkedInAt);
    }

    @Test
    @DisplayName("동일 reservationId 이벤트 재수신 시 중복 저장하지 않음")
    void 동일ReservationId이벤트재수신시중복저장하지않음() {
        UUID reservationId = UUID.randomUUID();
        CheckInCompletedEvent event = createEvent(reservationId);

        // 이미 같은 reservationId의 체크인 로그가 있으면 중복 이벤트로 보고 skip한다.
        given(restaurantCheckInLogRepository.existsByReservationId(reservationId))
                .willReturn(true);

        checkInCompletedEventHandler.handle(event);

        // 중복 이벤트는 식당 존재 여부를 다시 확인할 필요 없이 바로 종료한다.
        then(restaurantRepository)
                .shouldHaveNoInteractions();

        // 중복 이벤트는 체크인 로그를 다시 저장하면 안 된다.
        then(restaurantCheckInLogRepository)
                .should(never())
                .save(any(RestaurantCheckInLog.class));
    }

    @Test
    @DisplayName("지원하지 않는 이벤트 타입이면 예외 발생")
    void 지원하지않는이벤트타입이면예외발생() {
        CheckInCompletedEvent event = new CheckInCompletedEvent(
                UUID.randomUUID(),
                "UNKNOWN_EVENT",
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.of(2026, 5, 10),
                UUID.randomUUID(),
                LocalDateTime.of(2026, 5, 10, 12, 30),
                LocalDateTime.of(2026, 5, 10, 12, 31)
        );

        assertThatThrownBy(() -> checkInCompletedEventHandler.handle(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 이벤트 타입입니다.");

        // 잘못된 이벤트는 체크인 로그 조회/저장까지 진행되면 안 된다.
        then(restaurantCheckInLogRepository)
                .shouldHaveNoInteractions();

        then(restaurantRepository)
                .shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("필수값이 누락되면 예외 발생")
    void 필수값이누락되면예외발생() {
        CheckInCompletedEvent event = new CheckInCompletedEvent(
                UUID.randomUUID(),
                CheckInCompletedEvent.CHECK_IN_COMPLETED,
                null,
                UUID.randomUUID(),
                LocalDate.of(2026, 5, 10),
                UUID.randomUUID(),
                LocalDateTime.of(2026, 5, 10, 12, 30),
                LocalDateTime.of(2026, 5, 10, 12, 31)
        );

        assertThatThrownBy(() -> checkInCompletedEventHandler.handle(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reservationId");

        // 필수값이 누락된 이벤트는 repository 접근 전에 실패해야 한다.
        then(restaurantCheckInLogRepository)
                .shouldHaveNoInteractions();

        then(restaurantRepository)
                .shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 식당 이벤트면 예외 발생")
    void 존재하지않는식당이벤트면예외발생() {
        UUID reservationId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();

        CheckInCompletedEvent event = new CheckInCompletedEvent(
                UUID.randomUUID(),
                CheckInCompletedEvent.CHECK_IN_COMPLETED,
                reservationId,
                restaurantId,
                LocalDate.of(2026, 5, 10),
                UUID.randomUUID(),
                LocalDateTime.of(2026, 5, 10, 12, 30),
                LocalDateTime.of(2026, 5, 10, 12, 31)
        );

        given(restaurantCheckInLogRepository.existsByReservationId(reservationId))
                .willReturn(false);

        given(restaurantRepository.findById(restaurantId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> checkInCompletedEventHandler.handle(event))
                .isInstanceOf(RestaurantException.class);

        // 식당이 없으면 체크인 로그를 저장하면 안 된다.
        then(restaurantCheckInLogRepository)
                .should(never())
                .save(any(RestaurantCheckInLog.class));
    }

    private CheckInCompletedEvent createEvent(UUID reservationId) {
        return new CheckInCompletedEvent(
                UUID.randomUUID(),
                CheckInCompletedEvent.CHECK_IN_COMPLETED,
                reservationId,
                UUID.randomUUID(),
                LocalDate.of(2026, 5, 10),
                UUID.randomUUID(),
                LocalDateTime.of(2026, 5, 10, 12, 30),
                LocalDateTime.of(2026, 5, 10, 12, 31)
        );
    }

    private Restaurant createRestaurant(UUID restaurantId) {
        Restaurant restaurant = Restaurant.create(
                UUID.randomUUID(),
                "MicheLet Dining",
                "서울특별시 강남구 테헤란로 123",
                "02-1234-5678",
                "파인다이닝 레스토랑",
                LocalTime.of(10, 0),
                90,
                RestaurantStatus.OPEN,
                "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
        );

        ReflectionTestUtils.setField(restaurant, "restaurantId", restaurantId);

        return restaurant;
    }
}