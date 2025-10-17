package com.dev.shoeshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho Product kèm thông tin Flash Sale (nếu có)
 * Dùng để hiển thị flash sale badge trong product grid
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductWithFlashSaleResponse {
    
    // Product info
    private Long id;
    private String title;
    private String image;
    private Double price;
    
    // Flash Sale info (null nếu không có flash sale)
    private FlashSaleInfo flashSale;
    
    /**
     * Nested class chứa thông tin flash sale
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FlashSaleInfo {
        private boolean active;                // Flash sale có active không
        private Long flashSaleId;              // ID của flash sale
        private String flashSaleName;          // Tên flash sale
        private Double flashSalePrice;         // Giá flash sale (giá thấp nhất trong các sizes)
        private Double discountPercent;        // % giảm giá
        private Integer stock;                 // Tổng stock của tất cả sizes
        private Integer sold;                  // Tổng đã bán
        private Integer remaining;             // Còn lại
        private Double soldPercentage;         // % đã bán
    }
}
