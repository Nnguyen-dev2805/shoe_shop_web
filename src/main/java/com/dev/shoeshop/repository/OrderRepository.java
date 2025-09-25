package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.OrderEntity;
import com.dev.shoeshop.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    Long countByStatus(ShipmentStatus status);

}
