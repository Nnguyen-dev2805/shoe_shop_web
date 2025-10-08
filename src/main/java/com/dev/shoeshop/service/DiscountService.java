package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.discount.DiscountCreateRequest;
import com.dev.shoeshop.dto.discount.DiscountResponse;
import com.dev.shoeshop.dto.discount.DiscountUpdateRequest;
import com.dev.shoeshop.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiscountService {
    
    /**
     * Lưu discount mới
     */
    Discount saveDiscount(Discount discount);
    
    /**
     * Tạo discount mới từ DiscountCreateRequest
     */
    Discount createDiscount(DiscountCreateRequest request, Long createdBy);
    
    /**
     * Cập nhật discount từ DiscountUpdateRequest
     */
    Discount updateDiscount(Long id, DiscountUpdateRequest request, Long updatedBy);
    
    /**
     * Lấy tất cả discount với pagination
     */
    Page<DiscountResponse> getAllDiscounts(Pageable pageable);
    
    /**
     * Lấy tất cả discount không phân trang
     */
    List<DiscountResponse> getAllDiscounts();
    
    /**
     * Lấy discount theo ID
     */
    Discount getDiscountById(Long id);
    
    /**
     * Lấy discount response theo ID
     */
    DiscountResponse getDiscountResponseById(Long id);
    
    /**
     * Lấy discount theo status với pagination
     */
    Page<DiscountResponse> getDiscountsByStatus(String status, Pageable pageable);
    
    /**
     * Lấy discount theo status không phân trang
     */
    List<DiscountResponse> getDiscountsByStatus(String status);
    
    /**
     * Lấy discount đang active
     */
    List<DiscountResponse> getActiveDiscounts();
    
    /**
     * Lấy discount sắp bắt đầu
     */
    List<DiscountResponse> getComingDiscounts();
    
    /**
     * Lấy discount đã hết hạn
     */
    List<DiscountResponse> getExpiredDiscounts();
    
    /**
     * Tìm kiếm discount theo tên
     */
    List<DiscountResponse> searchDiscountsByName(String name);
    
    /**
     * Lấy discount có thể sử dụng
     */
    List<DiscountResponse> getUsableDiscounts();
    
    /**
     * Lấy discount có sẵn cho user (alias for getUsableDiscounts)
     */
    List<DiscountResponse> getAvailableDiscounts();
    
    /**
     * Cập nhật discount
     */
    Discount updateDiscount(Discount discount);
    
    /**
     * Xóa discount (soft delete)
     */
    void deleteDiscount(Long id);
    
    /**
     * Xóa vĩnh viễn discount
     */
    void permanentDeleteDiscount(Long id);
    
    /**
     * Khôi phục discount đã bị xóa
     */
    void restoreDiscount(Long id);
    
    /**
     * Đếm số discount theo status
     */
    long countByStatus(String status);
    
    /**
     * Đếm tổng số discount
     */
    long getTotalCount();
    
    /**
     * Tự động cập nhật status của discount dựa trên ngày tháng
     */
    void autoUpdateDiscountStatus();
}
