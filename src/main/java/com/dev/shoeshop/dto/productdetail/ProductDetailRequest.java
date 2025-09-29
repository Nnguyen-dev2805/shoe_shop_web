package com.dev.shoeshop.dto.productdetail;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDetailRequest {
    private int size;
    private double priceAdd;
}
