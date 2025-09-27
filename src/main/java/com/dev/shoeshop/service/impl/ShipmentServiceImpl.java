package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.converter.toShipmentDTO;
import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Shipment;
import com.dev.shoeshop.repository.ShipmentRepository;
import com.dev.shoeshop.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private toShipmentDTO  toShipmentDTO;

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
}
