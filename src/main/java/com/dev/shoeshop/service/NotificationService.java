package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.OrderNotificationDTO;

/**
 * Service để gửi real-time notifications qua WebSocket
 */
public interface NotificationService {
    
    /**
     * Gửi thông báo đơn hàng mới đến admin
     * @param notification thông tin đơn hàng
     */
    void sendNewOrderNotification(OrderNotificationDTO notification);
    
    /**
     * Gửi thông báo hủy đơn hàng đến admin
     * @param notification thông tin đơn hàng bị hủy
     */
    void sendOrderCancelledNotification(OrderNotificationDTO notification);
    
    /**
     * Gửi thông báo cập nhật đơn hàng đến admin
     * @param notification thông tin đơn hàng được cập nhật
     */
    void sendOrderUpdatedNotification(OrderNotificationDTO notification);
}
