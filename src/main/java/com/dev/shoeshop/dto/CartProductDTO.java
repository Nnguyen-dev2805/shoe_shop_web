package com.dev.shoeshop.dto;

import lombok.Data;

@Data
public class CartProductDTO {
    private Long id;
    private Integer size;
    private Double priceadd;
    private ProductInfo product;
    
    @Data
    public static class ProductInfo {
        private Long id;
        private String title;
        private String image;
        private Double price;
    }
}
