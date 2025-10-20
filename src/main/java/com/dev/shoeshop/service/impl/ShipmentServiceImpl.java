package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.converter.ToShipmentDTO;
import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Shipment;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.repository.ShipmentRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private ToShipmentDTO toShipmentDTO;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private OrderService orderService;


    @Override
    public ShipmentDTO findShipmentByOrderId(Long id) {
        System.out.println("hoangha");
        Shipment shipment= shipmentRepository.findByOrderId(id);
        ShipmentDTO  shipmentDTO = new ShipmentDTO();
        if (shipment!=null){
              shipmentDTO = toShipmentDTO.toShipmentDTO(shipment);
        }

        return shipmentDTO;
    }

    @Override
    public void insertShipment(long orderid, long userid) {
        Shipment shipment = new Shipment();

        Order order = orderRepository.findOrderById(orderid);
        Users user = userRepository.findById(userid).orElse(null);

        shipment.setOrder(order);
        shipment.setShipper(user);
        shipment.setAddress(order.getAddress());
        shipment.setUpdatedDate(new Date());
        shipment.setStatus(ShipmentStatus.SHIPPED.toString());
        shipmentRepository.save(shipment);

        // ✅ Gọi OrderService.updateOrderStatus() để trigger email notification
        orderService.updateOrderStatus(orderid, ShipmentStatus.SHIPPED);
        System.out.println("📧 Called orderService.updateOrderStatus() to trigger email");
    }
    
    @Override
    public void updateShipmentDate(Long orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId);
        if (shipment != null) {
            shipment.setUpdatedDate(new Date());
            shipmentRepository.save(shipment);
        }
    }
    
    @Override
    public void updateShipmentStatusAndDate(Long orderId, ShipmentStatus newStatus) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId);
        if (shipment != null) {
            // Update cả status và ngày
            shipment.setStatus(newStatus.toString());
            shipment.setUpdatedDate(new Date());
            shipmentRepository.save(shipment);
        }
    }
    
    @Override
    public void saveNote(Long shipmentId, String note) {
        Shipment shipment = shipmentRepository.findById(shipmentId).orElse(null);
        if (shipment != null) {
            // Lưu note vào field status tạm thời (hoặc cần thêm field note vào entity Shipment)
            // TODO: Thêm field 'note' vào entity Shipment nếu cần lưu ghi chú
            System.out.println("📝 Saving note for shipment #" + shipmentId + ": " + note);
            // shipment.setNote(note); // Uncomment when note field is added
            shipmentRepository.save(shipment);
        }
    }
    
    // ========== Methods cho Controller (MVC Pattern) ==========
    
    @Override
    public Page<Order> getOrdersByShipperId(Long shipperId, Pageable pageable) {
        return shipmentRepository.findOrdersByShipperId(shipperId, pageable);
    }
    
    @Override
    public Page<Order> getOrdersByShipperIdAndStatus(Long shipperId, ShipmentStatus status, Pageable pageable) {
        return shipmentRepository.findOrdersByShipperIdAndStatus(shipperId, status, pageable);
    }
    
    @Override
    public Long countOrdersByShipperIdAndStatus(Long shipperId, ShipmentStatus status) {
        return shipmentRepository.countByShipperIdAndOrderStatus(shipperId, status);
    }
}
