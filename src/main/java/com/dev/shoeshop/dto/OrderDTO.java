package com.dev.shoeshop.dto;


import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.PayOption;
import com.dev.shoeshop.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;

    private String userName;

    private Double totalPrice;

    private Date createdDate;

    private ShipmentStatus status;

    private PayOption payOption;

    private UserDTO user;
    
    // Danh sách chi tiết đơn hàng
    private List<OrderDetailDTO> orderDetails;
    public OrderDTO convertToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .userName(order.getUser().getFullname())
                .totalPrice(order.getTotalPrice())
                .createdDate(order.getCreatedDate())
                .status(order.getStatus())
                .payOption(order.getPayOption())
                .user(UserDTO.builder()
                        .id(order.getUser().getId())
                        .fullname(order.getUser().getFullname())
                        .email(order.getUser().getEmail())
                        .address("1 VVN") // đoạn này chưa fix
//                        .address(order.getUser().getAddress())
                        .phone(order.getUser().getPhone())
                        .build()
                )
                .build();
    }

}
