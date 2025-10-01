package com.dev.shoeshop.dto.inventory;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InventoryRequest {
    private Long productId;
    private Map<Integer, Integer> sizes;
    private LocalDateTime createdAt;
}
