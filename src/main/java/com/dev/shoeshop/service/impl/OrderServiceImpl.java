package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.converter.OrderDTOConverter;
import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderStaticDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDTOConverter orderDTOConverter;

    @Override
    public OrderStaticDTO getStatic() {
        OrderStaticDTO orderStaticDto = new OrderStaticDTO();
        orderStaticDto.setShipping(orderRepository.countByStatus(ShipmentStatus.SHIPPED));
        orderStaticDto.setCancel(orderRepository.countByStatus(ShipmentStatus.CANCEL));
        orderStaticDto.setInStock(orderRepository.countByStatus(ShipmentStatus.IN_STOCK));
        orderStaticDto.setPreturn(orderRepository.countByStatus(ShipmentStatus.RETURN));
        orderStaticDto.setDelivered(orderRepository.countByStatus(ShipmentStatus.DELIVERED));

        return orderStaticDto;
    }

    @Override
    public List<OrderDTO> getOrderByStatus(ShipmentStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(orderDTOConverter::toOrderDTO)
                .toList();
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderDTOConverter::toOrderDTO)
                .toList();

    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("can not find order"));
    }
}
