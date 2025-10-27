package com.dev.shoeshop.service;

import com.dev.shoeshop.entity.ReturnRequest;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ReturnReason;
import com.dev.shoeshop.enums.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ReturnRequestService {
    
    /**
     * Tạo yêu cầu trả hàng mới
     */
    ReturnRequest createReturnRequest(Long orderId, Long userId, ReturnReason reason, 
                                     String description, String images);
    
    /**
     * Approve return request (Admin)
     */
    ReturnRequest approveReturnRequest(Long returnId, String adminNote);
    
    /**
     * Reject return request (Admin)
     */
    ReturnRequest rejectReturnRequest(Long returnId, String adminNote);
    
    /**
     * Cập nhật status: Shop đã nhận hàng
     */
    ReturnRequest markAsReceived(Long returnId, String adminNote);
    
    /**
     * Hoàn xu cho khách hàng
     */
    ReturnRequest processRefund(Long returnId, BigDecimal refundAmount);
    
    /**
     * User cập nhật tracking code khi gửi hàng
     */
    ReturnRequest updateTrackingCode(Long returnId, String trackingCode);
    
    /**
     * Lấy return request theo ID
     */
    ReturnRequest getReturnRequestById(Long id);
    
    /**
     * Lấy tất cả return requests của user
     */
    List<ReturnRequest> getUserReturnRequests(Users user);
    
    /**
     * Lấy return requests theo order
     */
    List<ReturnRequest> getReturnRequestsByOrder(Long orderId);
    
    /**
     * Lấy tất cả return requests (Admin - phân trang)
     */
    Page<ReturnRequest> getAllReturnRequests(Pageable pageable);
    
    /**
     * Lấy return requests theo status (Admin - phân trang)
     */
    Page<ReturnRequest> getReturnRequestsByStatus(ReturnStatus status, Pageable pageable);
    
    /**
     * Đếm số lượng return request pending
     */
    long countPendingRequests();
    
    /**
     * Kiểm tra order có thể tạo return request không
     */
    boolean canCreateReturnRequest(Long orderId, Long userId);
    
    /**
     * Hủy return request (chỉ khi PENDING)
     */
    void cancelReturnRequest(Long returnId, Long userId);
}
