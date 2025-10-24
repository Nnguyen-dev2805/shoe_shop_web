package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.InventoryHistory;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.repository.InventoryHistoryRepository;
import com.dev.shoeshop.repository.InventoryRepository;
import com.dev.shoeshop.service.InventoryHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryHistoryServiceImpl implements InventoryHistoryService {
    
    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final InventoryRepository inventoryRepository;
    
    @Override
    @Transactional
    public InventoryHistory recordImport(
            ProductDetail productDetail,
            Integer quantity,
            Double costPrice,
            String note) {
        
        log.info("Recording import: ProductDetail={}, Quantity={}, CostPrice={}", 
                 productDetail.getId(), quantity, costPrice);
        
        // 1. Get or create Inventory
        Inventory inventory = inventoryRepository.findByProductDetailId(productDetail.getId())
                .orElseGet(() -> {
                    Inventory newInv = Inventory.builder()
                            .productDetail(productDetail)
                            .remainingQuantity(0)
                            .totalQuantity(0)
                            .build();
                    return inventoryRepository.save(newInv);
                });
        
        // 2. Update inventory stock
        inventory.addStock(quantity);
        inventoryRepository.save(inventory);
        
        // 3. Create history record
        InventoryHistory history = InventoryHistory.builder()
                .productDetail(productDetail)
                .importDate(LocalDateTime.now())
                .quantity(quantity)
                .costPrice(costPrice)  // ⭐ Lưu giá nhập
                .note(note)
                .build();
        
        history = inventoryHistoryRepository.save(history);
        
        log.info("✅ Import recorded: HistoryId={}, NewBalance={}, TotalCost={}", 
                 history.getId(), inventory.getRemainingQuantity(), history.getTotalCost());
        
        return history;
    }
    
    @Override
    public Double getAverageCostPrice(ProductDetail productDetail) {
        List<InventoryHistory> histories = inventoryHistoryRepository
                .findByProductDetail(productDetail);
        
        if (histories.isEmpty()) {
            return null;
        }
        
        // Tính giá vốn trung bình: Tổng (giá × số lượng) / Tổng số lượng
        double totalCost = histories.stream()
                .filter(h -> h.getCostPrice() != null)
                .mapToDouble(InventoryHistory::getTotalCost)
                .sum();
        
        int totalQuantity = histories.stream()
                .filter(h -> h.getCostPrice() != null)
                .mapToInt(InventoryHistory::getQuantity)
                .sum();
        
        if (totalQuantity == 0) {
            return null;
        }
        
        return totalCost / totalQuantity;
    }
    
    
    @Override
    public List<InventoryHistory> getImportHistoryByProductDetail(Long productDetailId) {
        return inventoryHistoryRepository.findByProductDetailIdOrderByImportDateDesc(productDetailId);
    }
    
    @Override
    public Page<InventoryHistory> getImportHistoryByProductDetail(Long productDetailId, Pageable pageable) {
        return inventoryHistoryRepository.findByProductDetailIdOrderByImportDateDesc(productDetailId, pageable);
    }
    
    @Override
    public List<InventoryHistory> getImportHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return inventoryHistoryRepository.findByImportDateBetweenOrderByImportDateDesc(startDate, endDate);
    }
    
    @Override
    public Page<InventoryHistory> getRecentImports(Pageable pageable) {
        return inventoryHistoryRepository.findAllByOrderByImportDateDesc(pageable);
    }
    
    @Override
    public Integer getTotalImportedQuantity(Long productDetailId) {
        return inventoryHistoryRepository.getTotalImportedQuantity(productDetailId);
    }
    
    @Override
    public Long getImportCount(Long productDetailId) {
        return inventoryHistoryRepository.countByProductDetailId(productDetailId);
    }
}
