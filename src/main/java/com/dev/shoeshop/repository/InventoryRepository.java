package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * InventoryRepository - Quản lý tồn kho hiện tại
 * Mỗi ProductDetail chỉ có 1 record Inventory (OneToOne)
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    /**
     * Find inventory by ProductDetail ID
     */
    Optional<Inventory> findByProductDetailId(Long productDetailId);
    
    /**
     * Find inventory by ProductDetail entity
     */
    Optional<Inventory> findByProductDetail(ProductDetail productDetail);
    
    /**
     * Check if inventory exists for ProductDetail
     */
    boolean existsByProductDetailId(Long productDetailId);
    
    /**
     * Get all out of stock inventories
     */
    @Query("SELECT i FROM Inventory i WHERE i.remainingQuantity = 0")
    List<Inventory> findOutOfStockInventories();
    
    /**
     * Count active inventories (có hàng)
     */
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.remainingQuantity > 0")
    Long countActiveInventories();
    
    /**
     * Get total remaining quantity across all inventories
     */
    @Query("SELECT COALESCE(SUM(i.remainingQuantity), 0) FROM Inventory i")
    Integer getTotalRemainingQuantity();
    
    /**
     * Get total quantity (all imported)
     */
    @Query("SELECT COALESCE(SUM(i.totalQuantity), 0) FROM Inventory i")
    Integer getTotalQuantity();
}
