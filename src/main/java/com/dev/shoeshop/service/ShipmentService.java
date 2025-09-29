package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Shipment;

public interface ShipmentService {
    public ShipmentDTO findShipmentByOrderId(Long id);

    void insertShipment(long orderid, long userid);
}
