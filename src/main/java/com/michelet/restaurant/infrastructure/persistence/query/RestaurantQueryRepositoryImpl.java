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
 * 검색 조건은 keyword, region, status
 */
@Repository
public class RestaurantQueryRepositoryImpl implements RestaurantQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public RestaurantQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    // 식당 목록/검색 결과를 페이지 단위로 조회
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
                .map(entity -> RestaurantSummaryResult.from(entity))
                .toList();

        Long total = jpaQueryFactory
                .select(restaurant.count())
                .from(restaurant)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanBuilder buildPredicate(RestaurantSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        // soft delete 된 식당은 목록/검색 조회에서 제외한다.
        builder.and(restaurant.deletedAt.isNull());

        if (condition == null) {
            return builder;
        }

        if (condition.hasKeyword()) {
            builder.and(restaurant.name.containsIgnoreCase(condition.keyword().trim()));
        }

        if (condition.hasRegion()) {
            builder.and(restaurant.address.containsIgnoreCase(condition.region().trim()));
        }

        if (condition.hasStatus()) {
            builder.and(restaurant.status.eq(condition.status()));
        }

        return builder;
    }

    // Pageable의 Sort 정보를 QueryDSL OrderSpecifier로 변환
    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order sortOrder : pageable.getSort()) {
            Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;

            switch (sortOrder.getProperty()) {
                case "createdAt" -> orderSpecifiers.add(new OrderSpecifier<>(direction, restaurant.createdAt));
                case "name" -> orderSpecifiers.add(new OrderSpecifier<>(direction, restaurant.name));
                case "status" -> orderSpecifiers.add(new OrderSpecifier<>(direction, restaurant.status));
                default -> {
                    // 허용하지 않은 정렬 필드는 무시
                }
            }
        }

        if (orderSpecifiers.isEmpty()) {
            return new OrderSpecifier[]{restaurant.createdAt.desc()};
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}
