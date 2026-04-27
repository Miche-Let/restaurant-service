package com.michelet.restaurant.domain.model;

import com.michelet.common.entity.BaseEntity;
import com.michelet.restaurant.domain.model.vo.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "p_restaurant",
        indexes = {
                // owner 기준 식당 조회가 있을 수 있으므로 인덱스 추가
                @Index(name = "idx_restaurant_owner_id", columnList = "owner_id"),

                // 상태별 조회/필터링 대비
                @Index(name = "idx_restaurant_status", columnList = "status"),

                // 식당 이름 검색/조회 대비
                @Index(name = "idx_restaurant_name", columnList = "name")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "restaurant_id", nullable = false, updatable = false)
    private UUID restaurantId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "reservation_open_at", nullable = false)
    private LocalTime reservationOpenAt;

    @Column(name = "avg_meal_duration_min", nullable = false)
    private Integer avgMealDurationMin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RestaurantStatus status;

    @Column(name = "business_hours", nullable = false, columnDefinition = "text")
    private String businessHours;

    @Builder(access = AccessLevel.PRIVATE)
    private Restaurant(
            UUID ownerId,
            String name,
            String address,
            String phone,
            String description,
            LocalTime reservationOpenAt,
            Integer avgMealDurationMin,
            RestaurantStatus status,
            String businessHours
    ) {
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
        this.name = RestaurantName.of(name).value();
        this.address = RestaurantAddress.of(address).value();
        this.phone = RestaurantPhone.of(phone).value();
        this.description = normalizeDescription(description);
        this.reservationOpenAt = ReservationOpenAt.of(reservationOpenAt).value();
        this.avgMealDurationMin = validateAvgMealDuration(avgMealDurationMin);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.businessHours = BusinessHours.of(businessHours).value();
    }

    public static Restaurant create(
            UUID ownerId,
            String name,
            String address,
            String phone,
            String description,
            LocalTime reservationOpenAt,
            Integer avgMealDurationMin,
            RestaurantStatus status,
            String businessHours
    ) {
        return Restaurant.builder()
                .ownerId(ownerId)
                .name(name)
                .address(address)
                .phone(phone)
                .description(description)
                .reservationOpenAt(reservationOpenAt)
                .avgMealDurationMin(avgMealDurationMin)
                .status(status)
                .businessHours(businessHours)
                .build();
    }


    public void updateBasicInfo(
            String name,
            String address,
            String phone,
            String description,
            LocalTime reservationOpenAt,
            Integer avgMealDurationMin,
            RestaurantStatus status,
            String businessHours
    ) {
        this.name = RestaurantName.of(name).value();
        this.address = RestaurantAddress.of(address).value();
        this.phone = RestaurantPhone.of(phone).value();
        this.description = normalizeDescription(description);
        this.reservationOpenAt = ReservationOpenAt.of(reservationOpenAt).value();
        this.avgMealDurationMin = validateAvgMealDuration(avgMealDurationMin);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.businessHours = BusinessHours.of(businessHours).value();
    }

    /**
     * 평균 식사 시간 검증
     * 0 이하 값은 비정상 데이터로 간주한다.
     */
    private Integer validateAvgMealDuration(Integer avgMealDurationMin) {
        if (avgMealDurationMin == null || avgMealDurationMin <= 0) {
            throw new IllegalArgumentException("평균 식사 시간은 1분 이상이어야 합니다.");
        }
        return avgMealDurationMin;
    }

    /**
     * description은 null 허용.
     * 공백만 들어온 경우는 null로 정규화한다.
     */
    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String normalized = description.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
