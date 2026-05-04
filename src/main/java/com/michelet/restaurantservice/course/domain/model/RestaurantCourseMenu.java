package com.michelet.restaurantservice.course.domain.model;

import com.michelet.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(
        name = "p_restaurant_course_menu",
        indexes = {
                @Index(name = "idx_course_menu_course_id_sort_order", columnList = "course_id, sort_order")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_restaurant_course_menu_course_id_sort_order",
                        columnNames = {"course_id", "sort_order"}
                )
        }
)
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantCourseMenu extends BaseEntity {

    @Id
    @Column(name = "course_menu_id", nullable = false, updatable = false)
    private UUID courseMenuId;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_part", nullable = false, length = 30)
    private CoursePart coursePart;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    private RestaurantCourseMenu(
            UUID courseId,
            CoursePart coursePart,
            String menuName,
            Integer sortOrder
    ) {
        this.courseMenuId = UUID.randomUUID();
        this.courseId = validateCourseId(courseId);
        this.coursePart = validateCoursePart(coursePart);
        this.menuName = validateMenuName(menuName);
        this.sortOrder = validateSortOrder(sortOrder);
    }

    public static RestaurantCourseMenu create(
            UUID courseId,
            CoursePart coursePart,
            String menuName,
            Integer sortOrder
    ) {
        return new RestaurantCourseMenu(
                courseId,
                coursePart,
                menuName,
                sortOrder);
    }

    private UUID validateCourseId(UUID courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("코스 ID는 필수입니다.");
        }
        return courseId;
    }

    private CoursePart validateCoursePart(CoursePart coursePart) {
        if (coursePart == null) {
            throw new IllegalArgumentException("코스 메뉴 파트는 필수입니다.");
        }
        return coursePart;
    }

    private String validateMenuName(String menuName) {
        if (menuName == null || menuName.isBlank()) {
            throw new IllegalArgumentException("코스 메뉴 이름은 필수입니다.");
        }

        String normalized = menuName.trim();
        if (normalized.length() > 100) {
            throw new IllegalArgumentException("코스 메뉴 이름은 100자를 초과할 수 없습니다.");
        }

        return normalized;
    }


    private Integer validateSortOrder(Integer sortOrder) {
        if (sortOrder == null || sortOrder <= 0) {
            throw new IllegalArgumentException("코스 메뉴 순서는 1 이상이어야 합니다.");
        }
        return sortOrder;
    }
}
