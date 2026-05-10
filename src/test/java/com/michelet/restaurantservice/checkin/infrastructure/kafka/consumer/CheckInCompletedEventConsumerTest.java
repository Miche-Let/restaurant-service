package com.michelet.restaurantservice.checkin.infrastructure.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.michelet.restaurantservice.checkin.application.service.CheckInCompletedEventHandler;
import com.michelet.restaurantservice.checkin.infrastructure.kafka.event.subscribe.CheckInCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class CheckInCompletedEventConsumerTest {

    private CheckInCompletedEventHandler checkInCompletedEventHandler;
    private CheckInCompletedEventConsumer checkInCompletedEventConsumer;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        checkInCompletedEventHandler = mock(CheckInCompletedEventHandler.class);
        checkInCompletedEventConsumer = new CheckInCompletedEventConsumer(
                objectMapper,
                checkInCompletedEventHandler
        );
    }

    @Test
    @DisplayName("체크인 완료 이벤트 JSON 수신 시 Handler로 위임")
    void 체크인완료이벤트Json수신시Handler로위임() {
        UUID eventId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID checkedInBy = UUID.randomUUID();

        String message = """
                {
                  "eventId": "%s",
                  "eventType": "CHECK_IN_COMPLETED",
                  "reservationId": "%s",
                  "restaurantId": "%s",
                  "visitDate": "2026-05-10",
                  "checkedInBy": "%s",
                  "checkedInAt": "2026-05-10T12:30:00",
                  "eventCreatedAt": "2026-05-10T12:31:00"
                }
                """.formatted(eventId, reservationId, restaurantId, checkedInBy);

        checkInCompletedEventConsumer.consume(message);

        ArgumentCaptor<CheckInCompletedEvent> eventCaptor = ArgumentCaptor.forClass(CheckInCompletedEvent.class);

        then(checkInCompletedEventHandler)
                .should()
                .handle(eventCaptor.capture());

        CheckInCompletedEvent event = eventCaptor.getValue();

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.eventType()).isEqualTo(CheckInCompletedEvent.CHECK_IN_COMPLETED);
        assertThat(event.reservationId()).isEqualTo(reservationId);
        assertThat(event.restaurantId()).isEqualTo(restaurantId);
        assertThat(event.checkedInBy()).isEqualTo(checkedInBy);
        assertThat(event.visitDate()).isEqualTo(java.time.LocalDate.of(2026, 5, 10));
        assertThat(event.checkedInAt()).isEqualTo(java.time.LocalDateTime.of(2026, 5, 10, 12, 30));
        assertThat(event.eventCreatedAt()).isEqualTo(java.time.LocalDateTime.of(2026, 5, 10, 12, 31));
    }

    @Test
    @DisplayName("잘못된 체크인 완료 이벤트 JSON이면 예외 발생")
    void 잘못된체크인완료이벤트Json이면예외발생() {
        String invalidMessage = """
                {
                  "eventId": "invalid-uuid",
                  "eventType": "CHECK_IN_COMPLETED"
                }
                """;

        assertThatThrownBy(() -> checkInCompletedEventConsumer.consume(invalidMessage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("체크인 완료 이벤트가 올바르지 않습니다");

        then(checkInCompletedEventHandler)
                .shouldHaveNoInteractions();
    }
}