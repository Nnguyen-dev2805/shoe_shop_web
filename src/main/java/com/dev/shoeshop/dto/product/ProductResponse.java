package com.dev.shoeshop.dto.product;

import com.dev.shoeshop.entity.Brand;
import com.dev.shoeshop.entity.Category;
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
    private Category category;
    private Brand brand;
}
