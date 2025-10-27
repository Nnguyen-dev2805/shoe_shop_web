package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.ReturnRequest;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    
    /**
     * Tìm return request theo order ID
     */
    @Query("SELECT r FROM ReturnRequest r WHERE r.order.id = :orderId")
    List<ReturnRequest> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * Tìm return request theo user
     */
    @Query("SELECT r FROM ReturnRequest r WHERE r.user = :user ORDER BY r.createdDate DESC")
    List<ReturnRequest> findByUser(@Param("user") Users user);
    
    /**
     * Tìm return request theo user và status
     */
    @Query("SELECT r FROM ReturnRequest r WHERE r.user = :user AND r.status = :status ORDER BY r.createdDate DESC")
    List<ReturnRequest> findByUserAndStatus(@Param("user") Users user, @Param("status") ReturnStatus status);
    
    /**
     * Tìm return request theo status (phân trang)
     */
    Page<ReturnRequest> findByStatus(ReturnStatus status, Pageable pageable);
    
    /**
     * Tìm tất cả return requests (phân trang - cho admin)
     */
    @Query("SELECT r FROM ReturnRequest r ORDER BY r.createdDate DESC")
    Page<ReturnRequest> findAllOrderByCreatedDateDesc(Pageable pageable);
    
    /**
     * Đếm số lượng return request theo status
     */
    long countByStatus(ReturnStatus status);
    
    /**
     * Đếm pending return requests
     */
    @Query("SELECT COUNT(r) FROM ReturnRequest r WHERE r.status = 'PENDING'")
    long countPendingRequests();
    
    /**
     * Tìm return request cần xử lý (PENDING, quá 24h)
     */
    @Query("SELECT r FROM ReturnRequest r WHERE r.status = 'PENDING' AND r.createdDate < :before")
    List<ReturnRequest> findPendingBefore(@Param("before") LocalDateTime before);
    
    /**
     * Kiểm tra order đã có return request chưa
     */
    @Query("SELECT COUNT(r) > 0 FROM ReturnRequest r WHERE r.order.id = :orderId")
    boolean existsByOrderId(@Param("orderId") Long orderId);
    
    /**
     * Tìm return request theo order và user
     */
    @Query("SELECT r FROM ReturnRequest r WHERE r.order.id = :orderId AND r.user.id = :userId")
    Optional<ReturnRequest> findByOrderIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);
}
