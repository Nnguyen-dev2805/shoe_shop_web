package com.dev.shoeshop.dto.product;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDetailResponse {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String image;
    private String categoryName;
    private String brandName;
    private List<SizeOption> sizeOptions;
    private Double avgRating;
    private Integer totalReviews;
    private Long soldQuantity; // ← NEW: Total sold quantity
    private FlashSaleInfo flashSale; // ← NEW: Flash Sale information

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class SizeOption {
        private Long id;
        private Integer size;
        private Double priceAdd;
        private Integer stock; // Quantity available in inventory
    }
    
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class FlashSaleInfo {
        private Long id; // 🔥 THÊM Flash Sale ID
        private Boolean active;
        private Double flashSalePrice;
        private Double discountPercent;
        private LocalDateTime endTime;
        private Integer stock;
        private Integer sold;
        private Integer remaining;
        private Double soldPercentage;
    }
}
