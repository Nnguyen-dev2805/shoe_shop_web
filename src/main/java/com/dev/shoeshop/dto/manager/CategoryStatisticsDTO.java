package com.dev.shoeshop.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryStatisticsDTO {
    private long totalCategories;        // Tổng danh mục
    private long totalProducts;          // Tổng sản phẩm
    private long categoriesWithProducts; // Danh mục có sản phẩm
    private long emptyCategories;        // Danh mục trống
}
