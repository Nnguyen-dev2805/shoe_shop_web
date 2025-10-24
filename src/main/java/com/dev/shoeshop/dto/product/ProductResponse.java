package com.dev.shoeshop.dto.product;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductResponse {
    private Long id;
    private String title;
    private Double price;
    private String image;
    private String categoryName;
    private String brandName;
    private Long soldQuantity;  // ← NEW: Số lượng đã bán
    
    // ========== RATING & REVIEW FIELDS ==========
    private Double averageRating;  // Điểm rating trung bình
    private Long totalReviews;     // Tổng số đánh giá
    
    // ========== FLASH SALE FIELDS ==========
    private FlashSaleInfo flashSale;  // Thông tin flash sale nếu product đang trong flash sale
    
    /**
     * Nested class cho Flash Sale Info
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class FlashSaleInfo {
        private Long id;                     // 🔥 Flash Sale ID
        private boolean active;              // Flash sale có đang active không
        private Double flashSalePrice;       // Giá flash sale
        private Double originalPrice;        // Giá gốc
        private Double discountPercent;      // % giảm giá
        private Integer stock;               // Số lượng còn lại (optional)
        private String flashSaleName;        // Tên flash sale (optional)
    }
}
