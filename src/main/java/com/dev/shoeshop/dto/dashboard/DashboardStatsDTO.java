package com.dev.shoeshop.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO chứa tất cả dữ liệu thống kê cho admin dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    
    // Thống kê tổng quan
    private Long totalOrders;
    private Double totalRevenue;
    private Long totalProductsSold;
    private Long totalCustomers;
    
    // Số lượng đơn hàng theo trạng thái
    private Map<String, Long> ordersByStatus;
    
    // Thống kê theo thời gian (7 ngày gần nhất)
    private List<OrderTimeSeriesDTO> orderTimeSeries;
    
    // Top sản phẩm bán chạy
    private List<TopProductDTO> topProducts;
    
    // Doanh thu theo thời gian
    private List<RevenueTimeSeriesDTO> revenueTimeSeries;
}
