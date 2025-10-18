package com.dev.shoeshop.dto.inventory;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InventoryResponse {
    private Long id;
    private String productName;
    private String productImage;
    private Integer size;
    private String warehouseName;
    private Long warehouseId;
    private Integer quantity;
}
