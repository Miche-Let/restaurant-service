package com.michelet.restaurant.presentation.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(

        // 현재 페이지에 포함된 실제 응답 목록
        List<T> content,

        // 현재 페이지 번호 Spring Pageable 기준으로 0부터 시작
        int page,

        // 한 페이지에 요청한 데이터 개수
        int size,

        // 검색 조건에 해당하는 전체 데이터 개수
        long totalElements

) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}
