package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.dashboard.DashboardStatsDTO;
import com.dev.shoeshop.dto.dashboard.TopCustomerDTO;
import com.dev.shoeshop.dto.dashboard.TopProductDTO;
import com.dev.shoeshop.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * REST API Controller cho admin dashboard statistics
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * Lấy tất cả thống kê cho dashboard với optional date range
     * GET /api/admin/dashboard/stats?startDate=2024-01-01&endDate=2024-12-31
     * 
     * @param startDate Ngày bắt đầu (optional, format: yyyy-MM-dd)
     * @param endDate Ngày kết thúc (optional, format: yyyy-MM-dd)
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Lấy danh sách sản phẩm theo doanh thu (giảm dần)
     * GET /api/admin/dashboard/products-by-revenue?limit=20
     */
    @GetMapping("/products-by-revenue")
    public ResponseEntity<List<TopProductDTO>> getProductsByRevenue(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<TopProductDTO> products = dashboardService.getProductsByRevenue(startDate, endDate, limit);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Lấy danh sách sản phẩm theo số lượng bán (giảm dần)
     * GET /api/admin/dashboard/products-by-quantity?limit=20
     */
    @GetMapping("/products-by-quantity")
    public ResponseEntity<List<TopProductDTO>> getProductsByQuantity(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<TopProductDTO> products = dashboardService.getProductsByQuantity(startDate, endDate, limit);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Lấy danh sách khách hàng mua nhiều nhất (giảm dần)
     * GET /api/admin/dashboard/top-customers?limit=20
     */
    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerDTO>> getTopCustomers(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<TopCustomerDTO> customers = dashboardService.getTopCustomers(startDate, endDate, limit);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Xuất báo cáo dashboard ra file Excel
     * GET /api/admin/dashboard/export-excel?type=all&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportDashboardExcel(
            @RequestParam String type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            Authentication authentication) {
        try {
            // Get username from authentication
            String username = authentication != null ? authentication.getName() : "Admin";
            
            // Generate Excel file
            byte[] excelBytes = dashboardService.exportToExcel(type, startDate, endDate, username);
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "dashboard_report_" + type + ".xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
