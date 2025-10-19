package com.dev.shoeshop.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho thống kê doanh thu theo thời gian
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueTimeSeriesDTO {
    private String date; // Ngày (yyyy-MM-dd)
    private Double revenue; // Doanh thu
}
