package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho WebSocket sold_quantity realtime updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoldQuantityUpdateDTO {
    private Long productId;
    private String productTitle;
    private Long soldQuantity;
    private String updateType; // "INCREASE" (SHIPPED) hoặc "DECREASE" (CANCEL/RETURN)
    private Long orderId;
    private Long timestamp;
}
