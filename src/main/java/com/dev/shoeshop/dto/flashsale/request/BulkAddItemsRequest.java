package com.dev.shoeshop.dto.flashsale.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkAddItemsRequest {

    // Danh sách product detail cần thêm vào flash sale
    @NotEmpty(message = "Danh sách sản phẩm không được rỗng")
    private List<Long> productDetailIds;

    // Phần trăm giảm giá áp dụng cho TẤT CẢ sản phẩm
    @NotNull(message = "Phần trăm giảm giá không được null")
    @Min(value = 1, message = "Phần trăm giảm giá phải >= 1")
    @Max(value = 99, message = "Phần trăm giảm giá phải <= 99")
    private Double discountPercent;
}
