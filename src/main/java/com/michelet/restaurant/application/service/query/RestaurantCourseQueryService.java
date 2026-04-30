package com.michelet.restaurant.application.service.query;

import com.michelet.restaurant.application.result.CourseListItemResult;
import com.michelet.restaurant.application.result.CourseMenuResult;
import com.michelet.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurant.domain.repository.RestaurantCourseMenuRepository;
import com.michelet.restaurant.domain.repository.RestaurantCourseRepository;
import com.michelet.restaurant.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
    public List<CourseListItemResult> getCourse(UUID restaurantId) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));

        return restaurantCourseRepository.findAllByRestaurantIdOrderByCreatedAtAsc(restaurantId)
                .stream()
                .map(course -> {
                    List<CourseMenuResult> menus = restaurantCourseMenuRepository
                            .findAllByCourseIdOrderBySortOrderAsc(course.getCourseId())
                            .stream()
                            .map(menu -> CourseMenuResult.from(menu))
                            .toList();

                    return CourseListItemResult.of(course, menus);
                })
                .toList();
    }
}
