package com.michelet.restaurantservice.course.application.service;

import com.michelet.restaurantservice.course.application.result.CourseListItemResult;
import com.michelet.restaurantservice.course.application.result.CourseMenuResult;
import com.michelet.restaurantservice.course.application.result.CourseSummaryResult;
import com.michelet.restaurantservice.domain.exception.RestaurantErrorCode;
import com.michelet.restaurantservice.domain.exception.RestaurantException;
import com.michelet.restaurantservice.course.domain.model.RestaurantCourse;
import com.michelet.restaurantservice.course.domain.model.RestaurantCourseMenu;
import com.michelet.restaurantservice.course.domain.repository.RestaurantCourseMenuRepository;
import com.michelet.restaurantservice.course.domain.repository.RestaurantCourseRepository;
import com.michelet.restaurantservice.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RestaurantCourseQueryService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantCourseRepository restaurantCourseRepository;
    private final RestaurantCourseMenuRepository restaurantCourseMenuRepository;

    public RestaurantCourseQueryService(RestaurantRepository restaurantRepository, RestaurantCourseRepository restaurantCourseRepository, RestaurantCourseMenuRepository restaurantCourseMenuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantCourseRepository = restaurantCourseRepository;
        this.restaurantCourseMenuRepository = restaurantCourseMenuRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseListItemResult> getCourses(UUID restaurantId) {
        // 코스 목록 조회 전에 식당 존재 여부를 먼저 검증
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));

        // 식당에 등록된 코스 목록을 등록 순서 기준으로 조회
        List<RestaurantCourse> courses = restaurantCourseRepository.findAllByRestaurantIdOrderByCreatedAtAsc(restaurantId);

        // 등록된 코스가 없으면 메뉴 조회를 수행하지 않고 빈 목록을 반환
        if (courses.isEmpty()) {
            return List.of();
        }

        // 메뉴 목록을 한 번에 조회하기 위해 courseId 목록을 추출
        List<UUID> courseIds = courses.stream()
                .map(course -> course.getCourseId())
                .toList();

        // courseId IN 조건으로 모든 메뉴를 한 번에 조회
        List<RestaurantCourseMenu> courseMenus = restaurantCourseMenuRepository
                .findAllByCourseIdInOrderByCourseIdAscSortOrderAsc(courseIds);

        // 조회한 메뉴 목록을 courseId 기준으로 그룹핑
        Map<UUID, List<CourseMenuResult>> menusByCourseId = courseMenus.stream()
                .collect(Collectors.groupingBy(
                        menu -> menu.getCourseId(),
                        Collectors.mapping(
                                menu -> CourseMenuResult.from(menu),
                                Collectors.toList()
                        )
                ));

        // 각 코스에 해당하는 menus[]를 붙여 외부 코스 목록 조회 결과로 변환
        return courses.stream()
                .map(course -> CourseListItemResult.of(
                        course,
                        menusByCourseId.getOrDefault(course.getCourseId(), List.of())
                ))
                .toList();
    }

    // 식당 ID 기준으로 내부 코스 목록을 조회
    @Transactional(readOnly = true)
    public List<CourseSummaryResult> getInternalCourses(UUID restaurantId) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));

        return restaurantCourseRepository.findAllByRestaurantIdOrderByCreatedAtAsc(restaurantId)
                .stream()
                .map(course -> CourseSummaryResult.from(course))
                .toList();
    }
}
