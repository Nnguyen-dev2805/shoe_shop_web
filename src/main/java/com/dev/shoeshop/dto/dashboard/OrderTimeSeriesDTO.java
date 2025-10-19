package com.dev.shoeshop.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho thống kê đơn hàng theo thời gian
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimeSeriesDTO {
    private String date; // Ngày (yyyy-MM-dd)
    private Long orderCount; // Số lượng đơn hàng
}
