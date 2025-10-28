package com.dev.shoeshop.dto;

import com.dev.shoeshop.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDTO {
    private Long id;
    private Long orderId;
    private Double totalPrice;
    private Date createdDate;
    private ShipmentStatus orderStatus;
    private String payOption;
    private String deliveryAddress; // gộp từ addressEntity
    private String shipperName;
    private Long shipperId; // Fixed: lowercase 's'
    private String shipperPhone;  // Thêm phone
    private String shipperEmail;  // Thêm email
    private String shipperAddress; // Thêm address
    private Date updatedDate;
    private String shipmentStatus;
    private String note; // Ghi chú từ shipper
}
