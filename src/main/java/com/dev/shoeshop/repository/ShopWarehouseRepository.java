package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.ShopWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopWarehouseRepository extends JpaRepository<ShopWarehouse, Long> {
    
    // Tìm kho mặc định
    Optional<ShopWarehouse> findByIsDefaultTrue();
    
    // Tìm tất cả kho đang hoạt động
    List<ShopWarehouse> findByIsActiveTrue();
    
    // Tìm kho theo thành phố
    Optional<ShopWarehouse> findByCityAndIsActiveTrue(String city);
    
    // Check xem có kho mặc định chưa
    boolean existsByIsDefaultTrue();
}
