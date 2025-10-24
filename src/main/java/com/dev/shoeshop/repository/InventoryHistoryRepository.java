package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.InventoryHistory;
import com.dev.shoeshop.entity.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
    
    /**
     * Find all import history by product detail ID
     */
    List<InventoryHistory> findByProductDetailIdOrderByImportDateDesc(Long productDetailId);
    
    /**
     * Find import history by product detail ID with pagination
     */
    Page<InventoryHistory> findByProductDetailIdOrderByImportDateDesc(Long productDetailId, Pageable pageable);
    
    /**
     * ⭐ Find all import history by ProductDetail entity
     * Dùng cho getAverageCostPrice()
     */
    List<InventoryHistory> findByProductDetail(ProductDetail productDetail);
    
    /**
     * Find by date range
     */
    List<InventoryHistory> findByImportDateBetweenOrderByImportDateDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    /**
     * Find imports by product detail and date range
     */
    @Query("SELECT ih FROM InventoryHistory ih WHERE ih.productDetail.id = :productDetailId " +
           "AND ih.importDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ih.importDate DESC")
    List<InventoryHistory> findImportsByProductAndDateRange(
        @Param("productDetailId") Long productDetailId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Calculate total imported quantity by product detail
     */
    @Query("SELECT COALESCE(SUM(ih.quantity), 0) FROM InventoryHistory ih " +
           "WHERE ih.productDetail.id = :productDetailId")
    Integer getTotalImportedQuantity(@Param("productDetailId") Long productDetailId);
    
    /**
     * Get recent imports (global)
     */
    Page<InventoryHistory> findAllByOrderByImportDateDesc(Pageable pageable);
    
    /**
     * Count imports by product detail
     */
    Long countByProductDetailId(Long productDetailId);
    
    /**
     * Get latest import for product detail
     */
    InventoryHistory findFirstByProductDetailIdOrderByImportDateDesc(Long productDetailId);
}
