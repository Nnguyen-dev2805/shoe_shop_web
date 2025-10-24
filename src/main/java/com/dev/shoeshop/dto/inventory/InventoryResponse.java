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
    private Integer quantity;     // Số lượng còn lại
    
    // ✅ NEW FIELDS - Cost Tracking
    private Double costPrice;     // Giá nhập / đôi
    private Integer soldQuantity; // Số lượng đã bán
    private Integer initialQuantity; // Số lượng nhập ban đầu
    private String note;          // Ghi chú
}
