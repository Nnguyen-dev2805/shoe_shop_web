package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho WebSocket inventory realtime updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryUpdateDTO {
    private Long productDetailId;
    private Long productId;
    private String productTitle;
    private Integer size; // Changed from String to Integer to match ProductDetail.size
    private Integer newQuantity;
    private String updateType; // "DECREASE" (SHIPPED) hoặc "INCREASE" (CANCEL/RETURN)
    private Long orderId;
    private Long timestamp;
}
