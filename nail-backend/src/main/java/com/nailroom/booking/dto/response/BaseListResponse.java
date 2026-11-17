package com.nailroom.booking.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record BaseListResponse<T>(
        List<T> content,
        List<T> items,
        int page,
        int size,
        long totalElements,
        long totalItems,
        int totalPages
) {
    public static <T> BaseListResponse<T> of(Page<T> p) {
        return new BaseListResponse<>(
                p.getContent(),
                p.getContent(),
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalElements(),
                p.getTotalPages()
        );
    }
}
