package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.OrderStaticDTO;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

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
}
