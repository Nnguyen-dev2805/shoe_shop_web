package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Long> {
    
    /**
     * Lấy tất cả shipping rates đang active, sắp xếp theo min distance
     */
    List<ShippingRate> findByIsActiveTrueOrderByMinDistanceKmAsc();
    
    /**
     * Tìm shipping rate phù hợp với khoảng cách
     */
    @Query("SELECT sr FROM ShippingRate sr WHERE sr.isActive = true " +
           "AND :distance >= sr.minDistanceKm AND :distance <= sr.maxDistanceKm")
    Optional<ShippingRate> findByDistanceRange(@Param("distance") BigDecimal distance);
    
    /**
     * Lấy tất cả rates (cho admin)
     */
    List<ShippingRate> findAllByOrderByMinDistanceKmAsc();
}
