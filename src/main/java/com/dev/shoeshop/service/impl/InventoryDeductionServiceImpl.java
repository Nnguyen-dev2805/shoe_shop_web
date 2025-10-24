package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.repository.InventoryRepository;
import com.dev.shoeshop.service.InventoryDeductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation của InventoryDeductionService
 * Trừ hàng từ inventory khi order được tạo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryDeductionServiceImpl implements InventoryDeductionService {
    
    private final InventoryRepository inventoryRepository;
    
    /**
     * Trừ hàng từ inventory
     */
    @Override
    @Transactional
    public void deductInventoryAndCalculateProfit(OrderDetail orderDetail) {
        Long productDetailId = orderDetail.getProduct().getId();
        int quantityToDeduct = orderDetail.getQuantity();
        
        log.info("=== Inventory Deduction Started ===");
        log.info("ProductDetail ID: {}, Quantity to deduct: {}", productDetailId, quantityToDeduct);
        
        // Tìm inventory
        Inventory inventory = inventoryRepository.findByProductDetailId(productDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy inventory cho sản phẩm!"));
        
        // Check stock availability
        if (inventory.getRemainingQuantity() < quantityToDeduct) {
            throw new RuntimeException("Không đủ hàng! Chỉ còn " + inventory.getRemainingQuantity() + " sản phẩm.");
        }
        
        log.info("Current stock: {}, Deducting: {}", inventory.getRemainingQuantity(), quantityToDeduct);
        
        // Trừ số lượng
        inventory.removeStock(quantityToDeduct);
        inventoryRepository.save(inventory);
        
        log.info("✅ Stock deducted. New remaining: {}/{}", 
                inventory.getRemainingQuantity(), inventory.getTotalQuantity());
        log.info("=== Inventory Deduction Completed ===");
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
