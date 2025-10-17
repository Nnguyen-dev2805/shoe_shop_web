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
    
    // ========== SHIPPING VOUCHER METHODS ==========
    
    /**
     * Lấy tất cả order vouchers đang active
     */
    List<DiscountResponse> getActiveOrderVouchers();
    
    /**
     * Lấy tất cả shipping vouchers đang active
     */
    List<DiscountResponse> getActiveShippingVouchers();
    
    /**
     * Tính giá trị giảm phí ship
     * @param voucherId ID của shipping voucher
     * @param shippingFee Phí ship ban đầu
     * @param orderValue Giá trị đơn hàng (để check minOrderValue)
     * @return Số tiền được giảm
     */
    Double calculateShippingDiscount(Long voucherId, Double shippingFee, Double orderValue);
    
    /**
     * Validate shipping voucher có thể sử dụng không
     * @param voucherId ID của voucher
     * @param orderValue Giá trị đơn hàng
     * @param shippingFee Phí ship
     * @return true nếu valid
     */
    boolean validateShippingVoucher(Long voucherId, Double orderValue, Double shippingFee);
}
