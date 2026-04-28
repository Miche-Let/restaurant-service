package com.michelet.restaurant.infrastructure.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 설정 클래스
 *
 * QueryDSL 기반 조회 로직에서 사용할 JPAQueryFactory Bean을 등록
 * 목록 조회 / 검색 조회 / 페이징 조회 구현 시 공통으로 사용
 */
@Configuration
public class QuerydslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * QueryDSL의 핵심 진입점인 JPAQueryFactory를 Bean으로 등록
     *
     * 이후 infrastructure 계층의 QueryDSL 조회 구현체에서 주입받아 사용
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
