package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderStaticDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.enums.ShipmentStatus;

import java.util.List;

public interface OrderService {
    public OrderStaticDTO getStatic();

    public List<OrderDTO> getOrderByStatus(ShipmentStatus status);

    public List<OrderDTO> getAllOrders();

    public Order findById(Long id);


}
