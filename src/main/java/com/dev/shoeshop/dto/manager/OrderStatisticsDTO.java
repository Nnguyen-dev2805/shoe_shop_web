package com.dev.shoeshop.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatisticsDTO {
    private long inStock;    // Trong Kho
    private long shipping;   // Đang Giao
    private long delivered;  // Đã Giao
    private long cancel;     // Đã Hủy
    private long preturn;    // Trả Hàng (product return)
    private long total;      // Tổng đơn hàng
    private double totalRevenue; // Tổng doanh thu
}
