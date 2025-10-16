package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO for sending order notifications to admin via WebSocket
 * Chứa thông tin đơn hàng để hiển thị trong notification popup
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDTO {
    
    // Order information
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private Double totalPrice;
    private String payOption;
    private Integer itemCount;
    private Date createdDate;
    
    // Notification metadata
    private String message;
    private String notificationType; // NEW_ORDER, ORDER_CANCELLED, ORDER_UPDATED
    private Long timestamp;
    
    // Optional: Address info
    private String deliveryAddress;
}
