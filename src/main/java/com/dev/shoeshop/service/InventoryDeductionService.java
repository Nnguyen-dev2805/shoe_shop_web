package com.dev.shoeshop.service;

import com.dev.shoeshop.entity.OrderDetail;

/**
 * Service để xử lý trừ hàng từ inventory theo FIFO và tính toán lợi nhuận
 */
public interface InventoryDeductionService {
    
    /**
     * Trừ hàng từ inventory theo FIFO (First In First Out)
     * và tính toán cost price + profit cho OrderDetail
     * 
     * @param orderDetail OrderDetail cần trừ hàng
     * @throws RuntimeException nếu không đủ hàng
     */
    void deductInventoryAndCalculateProfit(OrderDetail orderDetail);
    
    /**
     * Hoàn trả hàng vào inventory khi hủy đơn
     * 
     * @param orderDetail OrderDetail cần hoàn trả
     */
    void returnInventory(OrderDetail orderDetail);
}
