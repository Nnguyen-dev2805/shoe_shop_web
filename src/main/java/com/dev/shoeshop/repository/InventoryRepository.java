package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    
    /**
     * Tính tổng số lượng tồn kho của một ProductDetail từ tất cả các bản ghi Inventory
     * @param productDetail ProductDetail entity
     * @return Tổng số lượng tồn kho
     */
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.productDetail = :productDetail")
    int getTotalQuantityByProductDetail(@Param("productDetail") ProductDetail productDetail);
    
    /**
     * Tìm Inventory theo ProductDetail
     * @param productDetail ProductDetail entity
     * @return Inventory nếu tìm thấy
     */
    Inventory findByProductDetail(ProductDetail productDetail);
    
    // ===== NEW QUERIES FOR COST PRICE & PROFIT TRACKING =====
    
    /**
     * Lấy danh sách inventory theo FIFO (First In First Out)
     * Ưu tiên lô hàng cũ nhất (import_date nhỏ nhất) và có quantity > 0
     */
    @Query("SELECT i FROM Inventory i " +
           "WHERE i.productDetail.id = :productDetailId " +
           "AND i.quantity > 0 " +
           "ORDER BY i.importDate ASC, i.id ASC")
    List<Inventory> findAvailableInventoriesFIFO(@Param("productDetailId") Long productDetailId);
    
    /**
     * Lấy tất cả lô hàng của 1 ProductDetail (để xem báo cáo)
     * Sorted by import date descending (mới nhất trước)
     */
    @Query("SELECT i FROM Inventory i " +
           "WHERE i.productDetail.id = :productDetailId " +
           "ORDER BY i.importDate DESC")
    List<Inventory> findAllByProductDetailId(@Param("productDetailId") Long productDetailId);
    
    /**
     * Lấy các lô đang active (còn hàng hoặc đã có bán)
     */
    @Query("SELECT i FROM Inventory i " +
           "WHERE i.productDetail.id = :productDetailId " +
           "AND (i.quantity > 0 OR i.soldQuantity > 0) " +
           "ORDER BY i.importDate DESC")
    List<Inventory> findActiveInventoriesByProductDetailId(@Param("productDetailId") Long productDetailId);
    
    /**
     * Tính average cost price của ProductDetail (từ các lô còn hàng)
     */
    @Query("SELECT AVG(i.costPrice) FROM Inventory i " +
           "WHERE i.productDetail.id = :productDetailId " +
           "AND i.quantity > 0")
    Double getAverageCostPriceByProductDetailId(@Param("productDetailId") Long productDetailId);
    
    /**
     * Tính weighted average cost (theo số lượng còn lại)
     */
    @Query("SELECT COALESCE(SUM(i.costPrice * i.quantity) / SUM(i.quantity), 0) " +
           "FROM Inventory i " +
           "WHERE i.productDetail.id = :productDetailId " +
           "AND i.quantity > 0")
    Double getWeightedAverageCostPrice(@Param("productDetailId") Long productDetailId);
}
