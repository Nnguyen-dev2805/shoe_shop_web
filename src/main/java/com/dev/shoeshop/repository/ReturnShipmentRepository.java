package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.ReturnShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnShipmentRepository extends JpaRepository<ReturnShipment, Long> {
    
    /**
     * Tìm return shipment theo return request ID
     */
    Optional<ReturnShipment> findByReturnRequestId(Long returnRequestId);
    
    /**
     * Tìm tất cả return shipments của shipper
     */
    List<ReturnShipment> findByShipperId(Long shipperId);
    
    /**
     * Tìm return shipments của shipper theo status
     */
    @Query("SELECT rs FROM ReturnShipment rs WHERE rs.shipper.id = :shipperId AND rs.status = :status")
    List<ReturnShipment> findByShipperIdAndStatus(@Param("shipperId") Long shipperId, 
                                                   @Param("status") String status);
    
    /**
     * Đếm số return shipments của shipper theo status
     */
    @Query("SELECT COUNT(rs) FROM ReturnShipment rs WHERE rs.shipper.id = :shipperId AND rs.status = :status")
    Long countByShipperIdAndStatus(@Param("shipperId") Long shipperId, 
                                    @Param("status") String status);
}
