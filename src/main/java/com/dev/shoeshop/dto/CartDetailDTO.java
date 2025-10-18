package com.dev.shoeshop.dto;

import lombok.Data;

@Data
public class CartDetailDTO {
    private Long id;
    private Long cartId;
    private CartProductDTO product;
    private Long quantity;
    private Double pricePerUnit; // Renamed from 'price' to match CartDetail entity
}
