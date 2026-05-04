package com.michelet.restaurantservice.course.application.service;

import com.michelet.restaurantservice.course.application.command.CreateCourseMenuCommand;

import java.util.List;
import java.util.stream.Collectors;

public final class CourseMenuCompositionGenerator {

    private CourseMenuCompositionGenerator() {

    }

    public static String generate(List<CreateCourseMenuCommand> menus) {
        return menus.stream()
                // 사용자가 입력한 sortOrder 기준으로 메뉴 노출 순서를 확정
                .sorted((left, right) -> Integer.compare(left.sortOrder(), right.sortOrder()))
                // 예: 애피타이저: 제철 샐러드
                .map(menu -> menu.coursePart().getDisplayName() + ": " + menu.menuName().trim())
                // 예: 애피타이저: 제철 샐러드 / 메인: 양갈비
                .collect(Collectors.joining(" / "));
    }
}
