package com.ordercraft.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Utility to convert Spring Data Page to ApiResponse.
 */
public class PagedResponse {

    private PagedResponse() {
        // utility class
    }

    public static <T> ApiResponse<List<T>> of(Page<T> page) {
        return ApiResponse.<List<T>>builder()
                .data(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
