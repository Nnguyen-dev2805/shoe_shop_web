package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    
    /**
     * Tính tổng số lượng tồn kho của một ProductDetail từ tất cả các bản ghi Inventory
     * @param productDetail ProductDetail entity
     * @return Tổng số lượng tồn kho
     */
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.productDetail = :productDetail")
    int getTotalQuantityByProductDetail(@Param("productDetail") ProductDetail productDetail);
}
