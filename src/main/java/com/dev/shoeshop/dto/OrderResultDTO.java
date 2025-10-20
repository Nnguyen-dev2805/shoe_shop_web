package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResultDTO {
    private Long orderId;
    private String paymentUrl; // For VNPay redirection
    private Long payosOrderCode; // For PayOS payment verification
    private String status;
    private String message;
}
