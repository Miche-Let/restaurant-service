package com.michelet.restaurant.infrastructure.persistence.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelet.restaurant.application.query.RestaurantSearchCondition;
import com.michelet.restaurant.application.query.repository.RestaurantQueryRepository;
import com.michelet.restaurant.application.result.RestaurantSummaryResult;
import com.michelet.restaurant.domain.model.Restaurant;
import com.michelet.restaurant.domain.model.RestaurantStatus;
import com.michelet.restaurant.infrastructure.config.JpaAuditingConfig;
import com.michelet.restaurant.infrastructure.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// 식당 목록/검색 QueryDSL 구현체 테스트
@DataJpaTest
@Import({
        QuerydslConfig.class,
        JpaAuditingConfig.class,
        RestaurantQueryRepositoryImpl.class,
        RestaurantQueryRepositoryImplTest.JpaAuditingTestConfig.class
})
class RestaurantQueryRepositoryImplTest {

    @Autowired
    private RestaurantQueryRepository restaurantQueryRepository;

    @Autowired
    private EntityManager entityManager;

    // 조건이 없을 때 전체 식당 목록이 조회되는지 확인
    @Test
    @DisplayName("식당 목록 조회")
    void 식당목록조회() {
        저장(
                생성("MicheLet Dining", "서울특별시 강남구 테헤란로 123", RestaurantStatus.OPEN),
                생성("MicheLet Bistro", "서울특별시 성동구 연무장길 10", RestaurantStatus.CLOSED),
                생성("Another Restaurant", "서울특별시 마포구 양화로 20", RestaurantStatus.OPEN)
        );

        RestaurantSearchCondition condition = new RestaurantSearchCondition(
                null,
                null,
                null
        );
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RestaurantSummaryResult> result =
                restaurantQueryRepository.search(condition, pageable);

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .extracting(resultItem -> resultItem.name())
                .contains("MicheLet Dining", "MicheLet Bistro", "Another Restaurant");
    }

    /**
     * 이름 + 지역 + 상태 조건이 실제로 적용되고
     * Pageable 기준으로 페이징이 동작하는지 확인
     */
    @Test
    @DisplayName("식당 검색 조회")
    void 식당검색조회() {
        저장(
                생성("MicheLet Dining", "서울특별시 강남구 테헤란로 123", RestaurantStatus.OPEN),
                생성("MicheLet Closed", "서울특별시 강남구 테헤란로 456", RestaurantStatus.CLOSED),
                생성("Another Restaurant", "서울특별시 강남구 테헤란로 789", RestaurantStatus.OPEN),
                생성("MicheLet Bistro", "서울특별시 성동구 연무장길 10", RestaurantStatus.OPEN)
        );

        RestaurantSearchCondition condition = new RestaurantSearchCondition(
                "MicheLet",
                "강남구",
                RestaurantStatus.OPEN
        );
        PageRequest pageable = PageRequest.of(0, 1);

        Page<RestaurantSummaryResult> result =
                restaurantQueryRepository.search(condition, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).status()).isEqualTo(RestaurantStatus.OPEN);
        assertThat(result.getContent().get(0).name()).isEqualTo("MicheLet Dining");
        assertThat(result.getContent().get(0).address()).contains("강남구");
    }

    @Test
    @DisplayName("지역으로 식당을 검색")
    void 지역으로식당검색() {
        저장(
                생성("MicheLet Gangnam", "서울특별시 강남구 테헤란로 123", RestaurantStatus.OPEN),
                생성("MicheLet Seongsu", "서울특별시 성동구 연무장길 10", RestaurantStatus.OPEN),
                생성("MicheLet Mapo", "서울특별시 마포구 양화로 20", RestaurantStatus.OPEN)
        );

        RestaurantSearchCondition condition = new RestaurantSearchCondition(
                null,
                "강남",
                null
        );

        Page<RestaurantSummaryResult> result = restaurantQueryRepository.search(
                condition,
                PageRequest.of(0, 10)
        );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("MicheLet Gangnam");
        assertThat(result.getContent().get(0).address()).contains("강남구");
    }

    // 테스트용 식당 엔티티를 생성
    private Restaurant 생성(String name, String address, RestaurantStatus status) {
        return Restaurant.create(
                UUID.randomUUID(),
                name,
                address,
                "02-1234-5678",
                "테스트 식당 설명",
                LocalTime.of(10, 0),
                90,
                status,
                "MON-FRI 11:00-20:00 / SAT,SUN CLOSED"
        );
    }

    /**
     * 테스트 데이터를 저장하고 영속성 컨텍스트를 초기화
     */
    private void 저장(Restaurant... restaurants) {
        for (Restaurant restaurant : restaurants) {
            entityManager.persist(restaurant);
        }
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * @DataJpaTest 환경에서도 BaseEntity auditing 필드가 정상 동작하도록
     * 테스트 전용으로 JPA Auditing을 활성화
     */
    @TestConfiguration
    @EnableJpaAuditing(auditorAwareRef = "auditorAware")
    static class JpaAuditingTestConfig {
    }
}