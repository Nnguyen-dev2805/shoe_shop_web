package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.FlashSale;
import com.dev.shoeshop.enums.FlashSaleStatus;
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
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    // Tìm flash sale đang ACTIVE
    // Dùng cho việc Homepage hiển thị flash sale đang diễn ra
    @Query("SELECT fs FROM FlashSale fs " +
           "WHERE fs.status = :status " +
           "AND fs.isDelete = false " +
           "AND :now BETWEEN fs.startTime AND fs.endTime")
    Optional<FlashSale> findActiveFlashSale(
        @Param("status") FlashSaleStatus status,
        @Param("now") LocalDateTime now
    );

    // Tìm flash sale sắp diễn ra - UPCOMING
    // lấy flash sale gần nhất theo thời gian
    // Dùng hiển thị countdown "Sắp bắt đầu"
    @Query("SELECT fs FROM FlashSale fs " +
           "WHERE fs.status = :status " +
           "AND fs.isDelete = false " +
           "AND fs.startTime > :now " +
           "ORDER BY fs.startTime ASC")
    Optional<FlashSale> findUpcomingFlashSale(
        @Param("status") FlashSaleStatus status,
        @Param("now") LocalDateTime now
    );

    // Tìm tất cả flash sales cần ACTIVATE (đến giờ rồi)
    // Dùng cho Scheduler tự động activate
    List<FlashSale> findByStatusAndStartTimeBeforeAndIsDeleteFalse(
        FlashSaleStatus status, 
        LocalDateTime time
    );

    // Tìm tất cả flash sales cần END
    // Scheduler tự động end
    List<FlashSale> findByStatusAndEndTimeBeforeAndIsDeleteFalse(
        FlashSaleStatus status, 
        LocalDateTime time
    );

    // Lấy tất cả flash sales chưa bị xóa
    // Dùng cho Admin
    List<FlashSale> findByIsDeleteFalseOrderByCreatedDateDesc();
    
    // ========== PAGINATION METHODS FOR ADMIN ==========
    
    /**
     * Lấy tất cả flash sales với pagination
     * Dùng cho Admin list page
     */
    Page<FlashSale> findByIsDeleteFalse(Pageable pageable);
    
    /**
     * Lấy flash sales theo status với pagination
     * Dùng cho Admin filter
     */
    Page<FlashSale> findByStatusAndIsDeleteFalse(FlashSaleStatus status, Pageable pageable);
}
