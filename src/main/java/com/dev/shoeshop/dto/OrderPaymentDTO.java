package com.dev.shoeshop.dto;


import lombok.Data;

@Data
public class OrderPaymentDTO {
    private double subtotal;
    private double discount;
    private double totalpay;
}
