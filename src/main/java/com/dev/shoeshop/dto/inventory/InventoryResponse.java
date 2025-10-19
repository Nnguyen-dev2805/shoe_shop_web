package com.dev.shoeshop.dto.inventory;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InventoryResponse {
    private Long id;
    private Long productDetailId; // ✅ THÊM: Để WebSocket tìm element
    private Long productId;       // ✅ THÊM: Để WebSocket update summary
    private String productName;
    private String productImage;
    private Integer size;
    private String warehouseName;
    private Long warehouseId;
    private Integer quantity;
}
