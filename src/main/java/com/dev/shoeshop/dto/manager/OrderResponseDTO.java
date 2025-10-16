package com.dev.shoeshop.dto.manager;

import com.dev.shoeshop.enums.PayOption;
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
public class OrderResponseDTO {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Double totalPrice;
    private Date createdDate;
    private ShipmentStatus status;
    private PayOption payOption;
    private String deliveryAddress;
    private int itemCount;
}
