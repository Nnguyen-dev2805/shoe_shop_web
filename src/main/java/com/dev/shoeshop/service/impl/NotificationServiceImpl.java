package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.OrderNotificationDTO;
import com.dev.shoeshop.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service implementation để gửi thông báo real-time qua WebSocket
 * Sử dụng SimpMessagingTemplate để broadcast messages đến clients
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi thông báo đơn hàng mới đến admin
     */
    @Override
    public void sendNewOrderNotification(OrderNotificationDTO notification) {
        System.out.println("=== Sending NEW ORDER notification via WebSocket ===");
        System.out.println("Order ID: " + notification.getOrderId());
        System.out.println("Customer: " + notification.getCustomerName());
        System.out.println("Total: " + notification.getTotalPrice());
        
        // Set notification type and message
        notification.setNotificationType("NEW_ORDER");
        notification.setMessage("🛍️ Đơn hàng mới từ " + notification.getCustomerName());
        notification.setTimestamp(System.currentTimeMillis());
        
        // Send to topic /topic/admin/orders
        // Admin clients subscribe to this topic
        messagingTemplate.convertAndSend("/topic/admin/orders", notification);
        
        System.out.println("✅ Notification sent to /topic/admin/orders");
    }

    /**
     * Gửi thông báo hủy đơn hàng đến admin
     */
    @Override
    public void sendOrderCancelledNotification(OrderNotificationDTO notification) {
        System.out.println("=== Sending ORDER CANCELLED notification via WebSocket ===");
        
        notification.setNotificationType("ORDER_CANCELLED");
        notification.setMessage("❌ Đơn hàng #" + notification.getOrderId() + " đã bị hủy");
        notification.setTimestamp(System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/admin/orders", notification);
        
        System.out.println("✅ Cancellation notification sent");
    }

    /**
     * Gửi thông báo cập nhật đơn hàng đến admin
     */
    @Override
    public void sendOrderUpdatedNotification(OrderNotificationDTO notification) {
        System.out.println("=== Sending ORDER UPDATED notification via WebSocket ===");
        
        notification.setNotificationType("ORDER_UPDATED");
        notification.setMessage("🔄 Đơn hàng #" + notification.getOrderId() + " đã được cập nhật");
        notification.setTimestamp(System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/admin/orders", notification);
        
        System.out.println("✅ Update notification sent");
    }
}
