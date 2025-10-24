package com.dev.shoeshop.dto.inventory;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InventoryResponse {
    private Long id;
    private Long productDetailId;
    private Long productId;
    private String productName;
    private String productImage;
    private Integer size;
    private String warehouseName;
    private Long warehouseId;
    
    // ===== INVENTORY FIELDS =====
    private Integer quantity;          // Số lượng còn lại (remainingQuantity)
    private Integer totalQuantity;     // Tổng số lượng đã nhập từ đầu
    private Integer soldQuantity;      // Số lượng đã bán (calculated)
    private Double soldPercentage;     // % đã bán (calculated)
}
