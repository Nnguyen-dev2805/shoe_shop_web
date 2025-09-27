package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    
    /**
     * Tìm discount theo status
     */
    List<Discount> findByStatus(String status);
    
    /**
     * Tìm discount theo status với pagination
     */
    Page<Discount> findByStatus(String status, Pageable pageable);
    
    /**
     * Tìm discount chưa bị xóa (soft delete)
     */
    List<Discount> findByIsDeleteFalse();
    
    /**
     * Tìm discount chưa bị xóa với pagination
     */
    Page<Discount> findByIsDeleteFalse(Pageable pageable);
    
    /**
     * Tìm discount theo status và chưa bị xóa
     */
    List<Discount> findByStatusAndIsDeleteFalse(String status);
    
    /**
     * Tìm discount theo status và chưa bị xóa với pagination
     */
    Page<Discount> findByStatusAndIsDeleteFalse(String status, Pageable pageable);
    
    /**
     * Tìm discount đang active (đang trong thời gian hiệu lực)
     */
    @Query("SELECT d FROM Discount d WHERE d.status = 'ACTIVE' AND d.isDelete = false AND :currentDate BETWEEN d.startDate AND d.endDate")
    List<Discount> findActiveDiscounts(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Tìm discount sắp bắt đầu
     */
    @Query("SELECT d FROM Discount d WHERE d.status = 'COMING' AND d.isDelete = false AND d.startDate > :currentDate")
    List<Discount> findComingDiscounts(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Tìm discount đã hết hạn
     */
    @Query("SELECT d FROM Discount d WHERE d.isDelete = false AND d.endDate < :currentDate")
    List<Discount> findExpiredDiscounts(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Tìm discount theo tên (tìm kiếm không phân biệt hoa thường)
     */
    @Query("SELECT d FROM Discount d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND d.isDelete = false")
    List<Discount> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Tìm discount có thể sử dụng (active, còn số lượng, chưa hết hạn)
     */
    @Query("SELECT d FROM Discount d WHERE d.status = 'ACTIVE' AND d.isDelete = false AND d.quantity > 0 AND :currentDate BETWEEN d.startDate AND d.endDate")
    List<Discount> findUsableDiscounts(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Đếm số discount theo status
     */
    long countByStatusAndIsDeleteFalse(String status);
    
    /**
     * Đếm tổng số discount chưa bị xóa
     */
    long countByIsDeleteFalse();
    
    /**
     * Kiểm tra tên discount đã tồn tại chưa (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Kiểm tra tên discount đã tồn tại cho discount khác chưa (case-insensitive)
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
