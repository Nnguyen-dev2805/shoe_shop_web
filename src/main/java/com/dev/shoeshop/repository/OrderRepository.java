package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface OrderRepository extends JpaRepository<Order,Long> {

    Long countByStatus(ShipmentStatus status);

    public List<Order> findByStatus(ShipmentStatus status);

    public Order findOrderById(Long id);



}
