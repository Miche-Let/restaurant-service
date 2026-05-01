package com.michelet.restaurant.domain.model;

import com.michelet.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "p_restaurant_checkin_log",
        indexes = {
                // 식당 기준 체크인 이력 조회 대비
                @Index(name = "idx_checkin_log_restaurant_id", columnList = "restaurant_id"),
                // 예약 ID 기준 중복 체크/추적 대비
                @Index(name = "idx_checkin_log_reservation_id", columnList = "reservation_id"),
                // 방문 일자 기준 이력 조회 대비
                @Index(name = "idx_checkin_log_visit_date", columnList = "visit_date")
        }
)
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantCheckInLog extends BaseEntity {

    @Id
    @Column(name = "checkin_log_id", nullable = false, updatable = false)
    private UUID checkinLogId;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "reservation_id", nullable = false)
    private UUID reservationId;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CheckInStatus status;

    @Column(name = "checked_in_by", nullable = false)
    private UUID checkedInBy;

    @Column(name = "checked_in_at", nullable = false)
    private LocalDateTime checkedInAt;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    private RestaurantCheckInLog(
            UUID restaurantId,
            UUID reservationId,
            LocalDate visitDate,
            CheckInStatus status,
            UUID checkedInBy,
            LocalDateTime checkedInAt,
            String note
    ) {
        this.checkinLogId = UUID.randomUUID();
        this.restaurantId = validateRestaurantId(restaurantId);
        this.reservationId = validateReservationId(reservationId);
        this.visitDate = validateVisitDate(visitDate);
        this.status = validateStatus(status);
        this.checkedInBy = validateCheckedInBy(checkedInBy);
        this.checkedInAt = validateCheckedInAt(checkedInAt);
        this.note = normalizeNote(note);
    }

    public static RestaurantCheckInLog createCheckedIn(
            UUID restaurantId,
            UUID reservationId,
            UUID checkedInBy,
            LocalDateTime checkedInAt
    ) {
        if (checkedInAt == null) {
            throw new IllegalArgumentException("체크인 처리 시각은 필수입니다.");
        }

        return new RestaurantCheckInLog(
                restaurantId,
                reservationId,
                checkedInAt.toLocalDate(),
                CheckInStatus.CHECKED_IN,
                checkedInBy,
                checkedInAt,
                null
        );
    }

    private UUID validateRestaurantId(UUID restaurantId) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("식당 ID는 필수입니다.");
        }
        return restaurantId;
    }

    private UUID validateReservationId(UUID reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }
        return reservationId;
    }

    private LocalDate validateVisitDate(LocalDate visitDate) {
        if (visitDate == null) {
            throw new IllegalArgumentException("방문 일자는 필수입니다.");
        }
        return visitDate;
    }

    private CheckInStatus validateStatus(CheckInStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("체크인 상태는 필수입니다.");
        }
        return status;
    }

    private UUID validateCheckedInBy(UUID checkedInBy) {
        if (checkedInBy == null) {
            throw new IllegalArgumentException("체크인 처리자 ID는 필수입니다.");
        }
        return checkedInBy;
    }

    private LocalDateTime validateCheckedInAt(LocalDateTime checkedInAt) {
        if (checkedInAt == null) {
            throw new IllegalArgumentException("체크인 처리 시각은 필수입니다.");
        }
        return checkedInAt;
    }

    private String normalizeNote(String note) {
        if (note == null) {
            return null;
        }

        String normalized = note.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
