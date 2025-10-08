package com.dev.shoeshop.dto.product;

import lombok.*;

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
}
