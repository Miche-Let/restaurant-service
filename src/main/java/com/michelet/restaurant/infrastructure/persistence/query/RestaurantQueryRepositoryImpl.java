package com.michelet.restaurant.infrastructure.persistence.query;

import com.michelet.restaurant.application.query.RestaurantSearchCondition;
import com.michelet.restaurant.application.query.repository.RestaurantQueryRepository;
import com.michelet.restaurant.application.result.RestaurantSummaryResult;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.michelet.restaurant.domain.model.QRestaurant.restaurant;

/**
 * 식당 목록/검색 조회용 QueryDSL 구현체
 *
 * application 계층의 RestaurantQueryRepository를 QueryDSL 기반으로 구현
 * MVP 단계에서는 이름(name), 상태(status) 조건만 지원
 */
@Repository
public class RestaurantQueryRepositoryImpl implements RestaurantQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public RestaurantQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * 식당 목록/검색 결과를 페이지 단위로 조회
     *
     * 지원 조건
     * - name: 식당 이름 포함 검색
     * - status: 식당 상태 일치 검색
     *
     * 페이징은 Pageable의 offset, pageSize를 사용
     */
    @Override
    public Page<RestaurantSummaryResult> search(RestaurantSearchCondition condition, Pageable pageable) {
        BooleanBuilder predicate = buildPredicate(condition);

        List<RestaurantSummaryResult> content = jpaQueryFactory
                .selectFrom(restaurant)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable))
                .fetch()
                .stream()
                .map(RestaurantSummaryResult::from)
                .toList();

        Long total = jpaQueryFactory
                .select(restaurant.count())
                .from(restaurant)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    // 검색 조건을 QueryDSL predicate로 조합

    private BooleanBuilder buildPredicate(RestaurantSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        if (condition == null) {
            return builder;
        }

        if (condition.hasName()) {
            builder.and(restaurant.name.containsIgnoreCase(condition.name().trim()));
        }

        if (condition.hasStatus()) {
            builder.and(restaurant.status.eq(condition.status()));
        }

        return builder;
    }

    /**
     * Pageable의 Sort 정보를 QueryDSL OrderSpecifier로 변환
     *
     * 허용하지 않은 정렬 필드는 무시
     * 최종적으로 유효한 정렬 조건이 하나도 없으면 createdAt desc를 기본 정렬로 사용
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order sortOrder : pageable.getSort()) {
            Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;

            switch (sortOrder.getProperty()) {
                case "createAt" -> orderSpecifiers.add(new OrderSpecifier<>(direction, restaurant.createdAt));
                case "name" -> orderSpecifiers.add(new OrderSpecifier<>(direction, restaurant.name));
                case "status" -> orderSpecifiers.add(new OrderSpecifier<>(direction, restaurant.status));
                default -> {
                    // 허용하지 않은 정렬 필드는 아무 작업도 하지 않음
                }
            }
        }

        if (orderSpecifiers.isEmpty()) {
            return new OrderSpecifier[]{restaurant.createdAt.desc()};
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

}
