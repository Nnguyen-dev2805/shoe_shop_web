package com.dev.shoeshop.dto.flashsale.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO Request cho việc mua sản phẩm flash sale
// Frontend gửi lên khi user click "MUA NGAY"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseFlashSaleRequest {

    @NotNull(message = "Flash sale item ID không được null")
    private Long flashSaleItemId;

    @NotNull(message = "Số lượng không được null")
    @Min(value = 1, message = "Số lượng phải >= 1")
    private Integer quantity;
}
