package com.dev.shoeshop.dto.inventory;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InventoryRequest {
    private Long productId;
    private Map<Integer, Integer> sizes;  // Map<Size, Quantity>
    
    /**
     * ⭐ Giá nhập cho 1 đôi (áp dụng cho tất cả size trong lô này)
     * Ví dụ: 1.500.000đ/đôi
     */
    private Double costPrice;
    
    /**
     * Ghi chú nhập hàng (optional)
     */
    private String note;
}
