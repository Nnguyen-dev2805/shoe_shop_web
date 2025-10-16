package com.dev.shoeshop.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String image;
    private String categoryName;
    private String brandName;
    private Long totalReviews;
    private Double averageRating;
    private int totalQuantity;
    private boolean isDelete;
}
