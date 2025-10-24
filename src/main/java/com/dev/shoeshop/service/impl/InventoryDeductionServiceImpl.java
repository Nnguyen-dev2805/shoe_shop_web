package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.repository.InventoryRepository;
import com.dev.shoeshop.service.InventoryDeductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation của InventoryDeductionService
 * Xử lý logic FIFO để trừ hàng và tính lợi nhuận
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryDeductionServiceImpl implements InventoryDeductionService {
    
    private final InventoryRepository inventoryRepository;
    
    /**
     * Trừ hàng từ inventory theo FIFO và tính profit
     */
    @Override
    @Transactional
    public void deductInventoryAndCalculateProfit(OrderDetail orderDetail) {
        Long productDetailId = orderDetail.getProduct().getId();
        int quantityToDeduct = orderDetail.getQuantity();
        
        log.info("=== FIFO Deduction Started ===");
        log.info("ProductDetail ID: {}, Quantity to deduct: {}", productDetailId, quantityToDeduct);
        
        // ✅ Lấy danh sách inventory theo FIFO (lô cũ nhất trước)
        List<Inventory> inventories = inventoryRepository.findAvailableInventoriesFIFO(productDetailId);
        
        if (inventories.isEmpty()) {
            throw new RuntimeException("Sản phẩm đã hết hàng!");
        }
        
        // ✅ Tính tổng số lượng có sẵn
        int totalAvailable = inventories.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();
        
        log.info("Total available inventory: {}", totalAvailable);
        
        if (totalAvailable < quantityToDeduct) {
            throw new RuntimeException("Không đủ hàng! Chỉ còn " + totalAvailable + " sản phẩm.");
        }
        
        // ✅ Trừ dần từng lô theo FIFO và track cost
        int remaining = quantityToDeduct;
        double totalCost = 0.0; // Tổng giá nhập cho số lượng này
        
        for (Inventory inventory : inventories) {
            if (remaining <= 0) break;
            
            int currentQuantity = inventory.getQuantity();
            int toDeduct = Math.min(remaining, currentQuantity);
            
            // Trừ số lượng và tăng soldQuantity
            inventory.setQuantity(currentQuantity - toDeduct);
            inventory.setSoldQuantity(inventory.getSoldQuantity() + toDeduct);
            inventoryRepository.save(inventory);
            
            // Tích lũy cost
            double costPrice = inventory.getCostPrice() != null ? inventory.getCostPrice() : 0.0;
            totalCost += costPrice * toDeduct;
            remaining -= toDeduct;
            
            log.info("✅ Deducted {} from Inventory ID {} (Cost: {}đ) | Remaining: {}/{}", 
                    toDeduct, inventory.getId(), costPrice, inventory.getQuantity(), inventory.getInitialQuantity());
        }
        
        // ✅ Tính average cost price cho order detail này
        double averageCostPrice = totalCost / quantityToDeduct;
        
        // ✅ Tính profit
        double sellingPrice = orderDetail.getPrice();
        double profit = (sellingPrice - averageCostPrice) * quantityToDeduct;
        
        // ✅ Lưu vào OrderDetail
        orderDetail.setCostPriceAtSale(averageCostPrice);
        orderDetail.setProfit(profit);
        
        log.info("💰 Cost Price: {}đ | Selling Price: {}đ | Profit: {}đ (Margin: {:.2f}%)", 
                averageCostPrice, sellingPrice, profit, orderDetail.getProfitMargin());
        log.info("=== FIFO Deduction Completed ===");
    }
    
    /**
     * Hoàn trả hàng vào inventory khi hủy đơn
     * TODO: Implement logic hoàn trả (optional - nếu cần)
     */
    @Override
    @Transactional
    public void returnInventory(OrderDetail orderDetail) {
        // Logic hoàn trả hàng khi hủy đơn
        // Có thể implement sau nếu cần
        log.warn("returnInventory not implemented yet for OrderDetail ID: {}", orderDetail.getId());
    }
}
