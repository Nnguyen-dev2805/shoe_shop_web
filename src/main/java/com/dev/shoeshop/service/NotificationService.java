package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.InventoryUpdateDTO;
import com.dev.shoeshop.dto.OrderNotificationDTO;
import com.dev.shoeshop.dto.SoldQuantityUpdateDTO;

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
    
    /**
     * Gửi cập nhật inventory realtime (sau khi trigger chạy)
     * @param inventoryUpdate thông tin inventory đã thay đổi
     */
    void sendInventoryUpdate(InventoryUpdateDTO inventoryUpdate);
    
    /**
     * Gửi cập nhật sold_quantity realtime (sau khi trigger chạy)
     * @param soldQuantityUpdate thông tin sold_quantity đã thay đổi
     */
    void sendSoldQuantityUpdate(SoldQuantityUpdateDTO soldQuantityUpdate);
}
