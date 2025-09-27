package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment,Long> {
    public Shipment findShipmentById(int id);
    public Shipment findByOrderId(Long orderId);
}
