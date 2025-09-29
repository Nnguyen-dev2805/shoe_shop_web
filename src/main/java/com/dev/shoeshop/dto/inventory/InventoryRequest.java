package com.dev.shoeshop.dto.inventory;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InventoryRequest {
    protected Long id;
    protected Long productDetailId;
    private String title;
    private int quantity;
    private int size;
    private LocalDateTime createdAt;
}
