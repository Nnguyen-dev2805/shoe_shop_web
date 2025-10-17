package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Shipment;
import com.dev.shoeshop.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment,Long> {
    public Shipment findShipmentById(int id);
    public Shipment findByOrderId(Long orderId);
    
    /**
     * Tìm tất cả shipments của shipper cụ thể
     */
    List<Shipment> findByShipperId(Long shipperId);
    
    /**
     * Tìm shipments của shipper theo status
     */
    @Query("SELECT s FROM Shipment s WHERE s.shipper.id = :shipperId AND s.order.status = :status")
    List<Shipment> findByShipperIdAndOrderStatus(@Param("shipperId") Long shipperId, 
                                                  @Param("status") ShipmentStatus status);
    
    /**
     * Lấy các orders của shipper với pagination
     */
    @Query(value = "SELECT s.order FROM Shipment s WHERE s.shipper.id = :shipperId ORDER BY s.order.createdDate DESC",
           countQuery = "SELECT COUNT(s) FROM Shipment s WHERE s.shipper.id = :shipperId")
    Page<Order> findOrdersByShipperId(@Param("shipperId") Long shipperId, Pageable pageable);
    
    /**
     * Lấy các orders của shipper theo status với pagination
     */
    @Query(value = "SELECT s.order FROM Shipment s WHERE s.shipper.id = :shipperId AND s.order.status = :status ORDER BY s.order.createdDate DESC",
           countQuery = "SELECT COUNT(s) FROM Shipment s WHERE s.shipper.id = :shipperId AND s.order.status = :status")
    Page<Order> findOrdersByShipperIdAndStatus(@Param("shipperId") Long shipperId, 
                                                @Param("status") ShipmentStatus status, 
                                                Pageable pageable);
    
    /**
     * Đếm số orders của shipper theo status
     */
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.shipper.id = :shipperId AND s.order.status = :status")
    Long countByShipperIdAndOrderStatus(@Param("shipperId") Long shipperId, 
                                         @Param("status") ShipmentStatus status);
}
