package com.dev.shoeshop.dto;

import lombok.Data;

@Data
public class CartDetailDTO {
    private Long id;
    private Long cartId;
    private CartProductDTO product;
    private Long quantity;
    private Double price;
}
