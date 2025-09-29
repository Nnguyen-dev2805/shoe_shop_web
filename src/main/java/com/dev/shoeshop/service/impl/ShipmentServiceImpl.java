package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.converter.toShipmentDTO;
import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Shipment;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.repository.ShipmentRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private toShipmentDTO  toShipmentDTO;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;


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

        order.setStatus(ShipmentStatus.SHIPPED);

        orderRepository.save(order);
    }
}
