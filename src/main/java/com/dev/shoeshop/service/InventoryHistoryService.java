package com.dev.shoeshop.service;

import com.dev.shoeshop.entity.InventoryHistory;
import com.dev.shoeshop.entity.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryHistoryService {
    
    /**
     * Record import (nhập hàng)
     * @param productDetail Sản phẩm (size cụ thể)
     * @param quantity Số lượng nhập
     * @param costPrice ⭐ Giá nhập cho 1 đôi (VD: 1.500.000đ)
     * @param note Ghi chú
     */
    InventoryHistory recordImport(
        ProductDetail productDetail,
        Integer quantity,
        Double costPrice,
        String note
    );
    
    /**
     * Get import history by product detail
     */
    List<InventoryHistory> getImportHistoryByProductDetail(Long productDetailId);
    
    /**
     * Get import history by product detail with pagination
     */
    Page<InventoryHistory> getImportHistoryByProductDetail(Long productDetailId, Pageable pageable);
    
    /**
     * Get import history by date range
     */
    List<InventoryHistory> getImportHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get recent imports
     */
    Page<InventoryHistory> getRecentImports(Pageable pageable);
    
    /**
     * Get total imported quantity for product detail
     */
    Integer getTotalImportedQuantity(Long productDetailId);
    
    /**
     * Get import count for product detail
     */
    Long getImportCount(Long productDetailId);
    
    /**
     * ⭐ Tính giá vốn trung bình (Weighted Average Cost)
     * Công thức: Tổng (giá nhập × số lượng) / Tổng số lượng
     * @param productDetail Sản phẩm cần tính
     * @return Giá vốn trung bình hoặc null nếu không có lịch sử
     */
    Double getAverageCostPrice(ProductDetail productDetail);
}
