package com.dev.shoeshop.dto.pagination;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PaginationResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int size;
}
