package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    
    private Long id;
    private Integer quantity;
    private Double price;
    private Double amount;
    private Integer size;
    private String product_name;
    private String image;
    
    // Thông tin sản phẩm
    private ProductDetailDTO productDetail;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDetailDTO {
        private Long id;
        private Integer size;
        private Double priceadd;
        
        // Thông tin sản phẩm chính
        private ProductInfo product;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ProductInfo {
            private Long id;
            private String title;
            private String description;
            private Double price;
            private String image;
            private String brandName;
            private String categoryName;
        }
    }
}