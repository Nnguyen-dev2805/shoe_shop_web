package com.dev.shoeshop.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho top sản phẩm bán chạy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDTO {
    private Long productId;
    private String productName;
    private String productImage;
    private Long quantitySold;
    private Double totalRevenue;
}
