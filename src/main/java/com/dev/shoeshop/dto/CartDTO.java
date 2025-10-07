package com.dev.shoeshop.dto;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private Double totalPrice;
    private Date createdDate;
    private Set<CartDetailDTO> cartDetails; // Changed from orderDetailSet for consistency
}
