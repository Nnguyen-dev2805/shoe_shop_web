package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Shipment;
import com.dev.shoeshop.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShipmentService {
    public ShipmentDTO findShipmentByOrderId(Long id);

    void insertShipment(long orderid, long userid);
    
    /**
     * Cập nhật ngày update của shipment
     */
    void updateShipmentDate(Long orderId);
    
    /**
     * Cập nhật cả status và ngày update của shipment
     */
    void updateShipmentStatusAndDate(Long orderId, ShipmentStatus newStatus);
    
    /**
     * Lưu ghi chú của shipper
     */
    void saveNote(Long shipmentId, String note);
    
    // ========== Methods cho Controller (MVC Pattern) ==========
    
    /**
     * Lấy tất cả orders của shipper với pagination
     */
    Page<Order> getOrdersByShipperId(Long shipperId, Pageable pageable);
    
    /**
     * Lấy orders của shipper theo status với pagination
     */
    Page<Order> getOrdersByShipperIdAndStatus(Long shipperId, ShipmentStatus status, Pageable pageable);
    
    /**
     * Đếm số orders của shipper theo status
     */
    Long countOrdersByShipperIdAndStatus(Long shipperId, ShipmentStatus status);
}
