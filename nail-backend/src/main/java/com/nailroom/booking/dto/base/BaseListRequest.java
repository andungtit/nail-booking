package com.nailroom.booking.dto.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class BaseListRequest {
    private int page = 0;
    private int size = 20;
    private String sort;

    public Pageable toPageable() {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;

        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String prop = parts[0].trim();
            Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(safePage, safeSize, Sort.by(dir, prop));
        }

        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "id"));
    }
}
