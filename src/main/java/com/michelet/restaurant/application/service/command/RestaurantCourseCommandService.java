package com.michelet.restaurant.application.service.command;

import com.michelet.restaurant.application.command.CreateCourseCommand;
import com.michelet.restaurant.application.command.CreateCourseMenuCommand;
import com.michelet.restaurant.application.result.CourseResult;
import com.michelet.restaurant.domain.exception.CourseErrorCode;
import com.michelet.restaurant.domain.exception.CourseException;
import com.michelet.restaurant.domain.exception.RestaurantErrorCode;
import com.michelet.restaurant.domain.exception.RestaurantException;
import com.michelet.restaurant.domain.model.RestaurantCourse;
import com.michelet.restaurant.domain.model.RestaurantCourseMenu;
import com.michelet.restaurant.domain.repository.RestaurantCourseMenuRepository;
import com.michelet.restaurant.domain.repository.RestaurantCourseRepository;
import com.michelet.restaurant.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class RestaurantCourseCommandService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantCourseRepository restaurantCourseRepository;
    private final RestaurantCourseMenuRepository restaurantCourseMenuRepository;

    public RestaurantCourseCommandService(RestaurantRepository restaurantRepository, RestaurantCourseRepository restaurantCourseRepository, RestaurantCourseMenuRepository restaurantCourseMenuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantCourseRepository = restaurantCourseRepository;
        this.restaurantCourseMenuRepository = restaurantCourseMenuRepository;
    }

    /**
     * 식당 코스를 등록
     *
     * 메뉴 요약 문자열(menuComposition)은 클라이언트가 직접 보내지 않고
     * 서버가 menus를 sortOrder 기준으로 정렬해 생성
     */
    @Transactional
    public CourseResult createCourse(CreateCourseCommand command) {
        validateRestaurantExists(command.restaurantId());
        validateMenus(command.menus());

        String menuComposition = CourseMenuCompositionGenerator.generate(command.menus());

        RestaurantCourse course = RestaurantCourse.create(
                command.restaurantId(),
                command.name(),
                command.price(),
                menuComposition,
                command.sessionType(),
                command.status()
        );

        RestaurantCourse savedCourse = restaurantCourseRepository.save(course);

        List<RestaurantCourseMenu> courseMenus = command.menus().stream()
                .map(menu -> RestaurantCourseMenu.create(
                        savedCourse.getCourseId(),
                        menu.coursePart(),
                        menu.menuName(),
                        menu.sortOrder()
                ))
                .toList();

        restaurantCourseMenuRepository.saveAll(courseMenus);

        return CourseResult.from(savedCourse);
    }

    private void validateRestaurantExists(UUID restaurantId) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_404_NOT_FOUND));
    }

    private void validateMenus(List<CreateCourseMenuCommand> menus) {
        if (menus == null || menus.isEmpty()) {
            throw new CourseException(CourseErrorCode.COURSE_400_INVALID_REQUEST);
        }

        validateDuplicatedSortOrder(menus);
    }

    private void validateDuplicatedSortOrder(List<CreateCourseMenuCommand> menus) {
        // 중복 허용 방지
        Set<Integer> sortOrders = new HashSet<>();

        for (CreateCourseMenuCommand menu : menus) {
            if (!sortOrders.add(menu.sortOrder())) {
                throw new CourseException(CourseErrorCode.COURSE_400_INVALID_REQUEST);
            }
        }
    }
}
