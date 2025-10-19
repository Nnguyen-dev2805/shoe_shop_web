package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.DiscountUsed;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountUsedRepository extends JpaRepository<DiscountUsed, Long> {
    
    /**
     * Kiểm tra user đã dùng discount này chưa
     */
    boolean existsByUserAndDiscountAndIsActiveTrue(Users user, Discount discount);
    
    /**
     * Đếm số lần user đã dùng discount
     */
    long countByUserAndDiscount(Users user, Discount discount);
    
    /**
     * Lấy tất cả discount mà user đã dùng
     */
    List<DiscountUsed> findByUserAndIsActiveTrue(Users user);
    
    /**
     * Lấy tất cả lượt sử dụng của một discount
     */
    List<DiscountUsed> findByDiscountAndIsActiveTrue(Discount discount);
    
    /**
     * Tìm DiscountUsed theo user và discount
     */
    Optional<DiscountUsed> findByUserAndDiscountAndIsActiveTrue(Users user, Discount discount);
    
    /**
     * Đếm số lượt đã dùng của một discount
     */
    @Query("SELECT COUNT(du) FROM DiscountUsed du WHERE du.discount = :discount AND du.isActive = true")
    long countUsedByDiscount(@Param("discount") Discount discount);
    
    /**
     * Tìm DiscountUsed theo orderId
     */
    List<DiscountUsed> findByOrderId(Long orderId);
}
