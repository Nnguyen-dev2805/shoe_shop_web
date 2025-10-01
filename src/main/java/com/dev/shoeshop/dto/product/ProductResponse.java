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
}
