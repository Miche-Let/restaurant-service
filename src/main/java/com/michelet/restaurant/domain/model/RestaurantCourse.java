package com.michelet.restaurant.domain.model;

import com.michelet.common.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "p_restaurant_course",
        indexes = {
                @Index(name = "idx_restaurant_course_restaurant_id", columnList = "restaurant_id"),
                @Index(name = "idx_restaurant_course_status", columnList = "status")
        }
)
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantCourse extends BaseEntity {

    @Id
    @Column(name = "course_id", nullable = false, updatable = false)
    private UUID courseId;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "menu_composition", nullable = false, columnDefinition = "text")
    private String menuComposition;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 20)
    private CourseSessionType sessionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CourseStatus status;

    private RestaurantCourse(
            UUID restaurantId,
            String name,
            Long price,
            String menuComposition,
            CourseSessionType sessionType,
            CourseStatus status
    ) {
        this.courseId = UUID.randomUUID();
        this.restaurantId = validateRestaurantId(restaurantId);
        this.name = validateName(name);
        this.price = validatePrice(price);
        this.menuComposition = validateMenuComposition(menuComposition);
        this.sessionType = validateSessionType(sessionType);
        this.status = validateStatus(status);
    }

    /**
     * 식당 코스를 생성
     *
     * courseId는 애플리케이션에서 UUID를 직접 생성
     * menuComposition은 서비스 계층에서 menus[] 기반으로 생성한 값을 전달
     */
    public static RestaurantCourse create(
            UUID restaurantId,
            String name,
            Long price,
            String menuComposition,
            CourseSessionType sessionType,
            CourseStatus status
    ) {
        return new RestaurantCourse(
                restaurantId,
                name,
                price,
                menuComposition,
                sessionType,
                status
        );
    }

    private UUID validateRestaurantId(UUID restaurantId) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("식당 ID는 필수입니다.");
        }
        return restaurantId;
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("코스 이름은 필수입니다.");
        }

        String normalized = name.trim();
        if (normalized.length() > 100) {
            throw new IllegalArgumentException("코스 이름은 100자를 초과할 수 없습니다.");
        }
        return normalized;
    }

    private Long validatePrice(Long price) {
        if (price == null || price <= 0L) {
            throw new IllegalArgumentException("코스 가격은 1 이상이어야 합니다.");
        }
        return price;
    }

    private String validateMenuComposition(String menuComposition) {
        if (menuComposition == null || menuComposition.isBlank()) {
            throw new IllegalArgumentException("코스 메뉴 구성 정보는 필수입니다.");
        }
        return menuComposition.trim();
    }

    private CourseSessionType validateSessionType(CourseSessionType sessionType) {
        if (sessionType == null) {
            throw new IllegalArgumentException("코스 세션 타입은 필수입니다.");
        }
        return sessionType;
    }

    private CourseStatus validateStatus(CourseStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("코스 상태는 필수입니다.");
        }
        return status;
    }
}
