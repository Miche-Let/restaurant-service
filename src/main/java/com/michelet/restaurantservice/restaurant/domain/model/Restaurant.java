package com.michelet.restaurantservice.restaurant.domain.model;

import com.michelet.common.entity.BaseEntity;
import com.michelet.restaurantservice.restaurant.domain.model.vo.BusinessHours;
import com.michelet.restaurantservice.restaurant.domain.model.vo.ReservationOpenAt;
import com.michelet.restaurantservice.restaurant.domain.model.vo.RestaurantAddress;
import com.michelet.restaurantservice.restaurant.domain.model.vo.RestaurantName;
import com.michelet.restaurantservice.restaurant.domain.model.vo.RestaurantPhone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "p_restaurant",
        indexes = {
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
        this.restaurantId = UUID.randomUUID();
        this.ownerId = validateOwnerId(ownerId);
        this.name = RestaurantName.of(name).value();
        this.address = RestaurantAddress.of(address).value();
        this.phone = RestaurantPhone.of(phone).value();
        this.description = normalizeDescription(description);
        this.reservationOpenAt = ReservationOpenAt.of(reservationOpenAt).value();
        this.avgMealDurationMin = validateAvgMealDuration(avgMealDurationMin);
        this.status = validateStatus(status);
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
        return new Restaurant(
                ownerId,
                name,
                address,
                phone,
                description,
                reservationOpenAt,
                avgMealDurationMin,
                status,
                businessHours
        );
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
        this.status = validateStatus(status);
        this.businessHours = BusinessHours.of(businessHours).value();
    }

    private UUID validateOwnerId(UUID ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("오너 ID는 필수입니다.");
        }
        return ownerId;
    }

    private Integer validateAvgMealDuration(Integer avgMealDurationMin) {
        if (avgMealDurationMin == null || avgMealDurationMin <= 0) {
            throw new IllegalArgumentException("평균 식사 시간은 1분 이상이어야 합니다.");
        }
        return avgMealDurationMin;
    }

    private RestaurantStatus validateStatus(RestaurantStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("식당 상태는 필수입니다.");
        }
        return status;
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String normalized = description.trim();
        return normalized.isBlank() ? null : normalized;
    }
}