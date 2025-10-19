package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.InventoryUpdateDTO;
import com.dev.shoeshop.dto.OrderNotificationDTO;
import com.dev.shoeshop.dto.SoldQuantityUpdateDTO;
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
    
    /**
     * Gửi cập nhật inventory realtime
     * Được gọi SAU KHI trigger đã chạy và database đã cập nhật
     */
    @Override
    public void sendInventoryUpdate(InventoryUpdateDTO inventoryUpdate) {
        System.out.println("\n========================================");
        System.out.println("🔥 BROADCASTING INVENTORY UPDATE via WebSocket");
        System.out.println("========================================");
        System.out.println("📦 Product Detail ID: " + inventoryUpdate.getProductDetailId());
        System.out.println("📦 Product ID: " + inventoryUpdate.getProductId());
        System.out.println("📦 Product Title: " + inventoryUpdate.getProductTitle());
        System.out.println("📦 Size: " + inventoryUpdate.getSize());
        System.out.println("📦 New Quantity: " + inventoryUpdate.getNewQuantity());
        System.out.println("📦 Update Type: " + inventoryUpdate.getUpdateType());
        System.out.println("📦 Order ID: " + inventoryUpdate.getOrderId());
        
        // Set timestamp if not set
        if (inventoryUpdate.getTimestamp() == null) {
            inventoryUpdate.setTimestamp(System.currentTimeMillis());
        }
        
        System.out.println("📡 Sending to topic: /topic/inventory");
        
        // Broadcast to /topic/inventory
        // All admin pages (inventory-list, dashboard) subscribe to this
        messagingTemplate.convertAndSend("/topic/inventory", inventoryUpdate);
        
        System.out.println("✅ INVENTORY UPDATE SENT SUCCESSFULLY!");
        System.out.println("========================================\n");
    }
    
    /**
     * Gửi cập nhật sold_quantity realtime
     * Được gọi SAU KHI trigger đã chạy và database đã cập nhật
     */
    @Override
    public void sendSoldQuantityUpdate(SoldQuantityUpdateDTO soldQuantityUpdate) {
        System.out.println("=== Broadcasting SOLD QUANTITY UPDATE via WebSocket ===");
        System.out.println("Product ID: " + soldQuantityUpdate.getProductId());
        System.out.println("Sold Quantity: " + soldQuantityUpdate.getSoldQuantity());
        System.out.println("Update Type: " + soldQuantityUpdate.getUpdateType());
        
        // Set timestamp if not set
        if (soldQuantityUpdate.getTimestamp() == null) {
            soldQuantityUpdate.setTimestamp(System.currentTimeMillis());
        }
        
        // Broadcast to /topic/sold-quantity
        // Product list page, dashboard subscribe to this
        messagingTemplate.convertAndSend("/topic/sold-quantity", soldQuantityUpdate);
        
        System.out.println("✅ Sold quantity update broadcasted to /topic/sold-quantity");
    }
}
