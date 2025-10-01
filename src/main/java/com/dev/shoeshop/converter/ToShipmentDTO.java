package com.dev.shoeshop.converter;


import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Shipment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ToShipmentDTO {

    @Autowired
    private ModelMapper modelMapper;

    public ShipmentDTO toShipmentDTO(Shipment shipment) {
        // Map cơ bản
        ShipmentDTO dto = modelMapper.map(shipment, ShipmentDTO.class);

        // Custom mapping: shipperName
        if (shipment.getShipper() != null) {
            dto.setShipperName(shipment.getShipper().getFullname());
            dto.setShipperId(shipment.getShipper().getId());
        } else {
            dto.setShipperName("Unknown");
        }

        // Custom mapping: deliveryAddress (gộp từ addressEntity)
        if (shipment.getAddress() != null) {
            String address = String.join(", ",
                    shipment.getAddress().getAddress_line(),
                    shipment.getAddress().getDistrict(),
                    shipment.getAddress().getCity(),
                    shipment.getAddress().getCountry());
            dto.setDeliveryAddress(address);
        } else {
            dto.setDeliveryAddress("Unknown");
        }

        // Custom mapping: orderId, totalPrice, createdDate, orderStatus, payOption
        if (shipment.getOrder() != null) {
            dto.setOrderId(shipment.getOrder().getId());
            dto.setTotalPrice(shipment.getOrder().getTotalPrice());
            dto.setCreatedDate(shipment.getOrder().getCreatedDate());
            dto.setOrderStatus(shipment.getOrder().getStatus());
            if (shipment.getOrder().getPayOption() != null) {
                dto.setPayOption(shipment.getOrder().getPayOption().name());
            }
        }

        // Custom mapping: shipmentStatus
        dto.setShipmentStatus(shipment.getStatus());

        // Custom mapping: updatedDate
        dto.setUpdatedDate(shipment.getUpdatedDate());

        return dto;
    }

}
