package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.dashboard.DashboardStatsDTO;
import com.dev.shoeshop.dto.dashboard.TopCustomerDTO;
import com.dev.shoeshop.dto.dashboard.TopProductDTO;

import java.util.Date;
import java.util.List;

/**
 * Service interface cho dashboard statistics
 */
public interface DashboardService {
    
    /**
     * Lấy tất cả thống kê cho admin dashboard với date range filter
     * @param startDate Ngày bắt đầu (nullable - nếu null thì không filter)
     * @param endDate Ngày kết thúc (nullable - nếu null thì không filter)
     * @return DashboardStatsDTO chứa tất cả dữ liệu thống kê
     */
    DashboardStatsDTO getDashboardStats(Date startDate, Date endDate);
    
    /**
     * Lấy danh sách sản phẩm theo doanh thu (giảm dần)
     */
    List<TopProductDTO> getProductsByRevenue(Date startDate, Date endDate, int limit);
    
    /**
     * Lấy danh sách sản phẩm theo số lượng bán (giảm dần)
     */
    List<TopProductDTO> getProductsByQuantity(Date startDate, Date endDate, int limit);
    
    /**
     * Lấy danh sách khách hàng mua nhiều nhất (giảm dần theo tổng tiền chi)
     */
    List<TopCustomerDTO> getTopCustomers(Date startDate, Date endDate, int limit);
    
    /**
     * Xuất báo cáo dashboard ra file Excel
     * @param type Loại báo cáo: 'all', 'revenue', 'products-sold', 'customers'
     * @param startDate Ngày bắt đầu filter (nullable)
     * @param endDate Ngày kết thúc filter (nullable)
     * @param username Tên người tạo báo cáo
     * @return byte array của file Excel
     */
    byte[] exportToExcel(String type, Date startDate, Date endDate, String username) throws Exception;
}
