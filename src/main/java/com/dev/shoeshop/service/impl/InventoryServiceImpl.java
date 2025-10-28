package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.inventory.InventoryRequest;
import com.dev.shoeshop.dto.inventory.InventoryResponse;
import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.repository.InventoryRepository;
import com.dev.shoeshop.repository.ProductDetailRepository;
import com.dev.shoeshop.service.InventoryHistoryService;
import com.dev.shoeshop.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductDetailRepository productDetailRepository;
    private final InventoryHistoryService inventoryHistoryService;

    /**
     * 🗑️ CACHE EVICT: Clear inventory cache when adding new inventory
     */
    @Override
    @Transactional
    @CacheEvict(value = "inventory", allEntries = true)
    public void addInventory(InventoryRequest request) {
        log.info("➕ Adding new inventory, clearing inventory cache");
        List<ProductDetail> details = productDetailRepository.findByProductId(request.getProductId());

        for (Map.Entry<Integer, Integer> entry : request.getSizes().entrySet()) {
            Integer size = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity != null && quantity > 0) {
                ProductDetail detail = details.stream()
                        .filter(d -> Objects.equals(d.getSize(), size))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy product_detail cho size " + size));

                // ✅ GỌI recordImport thay vì update trực tiếp
                // recordImport sẽ:
                // 1. Tự động tạo/update Inventory
                // 2. Tạo InventoryHistory với costPrice
                String note = request.getNote() != null ? request.getNote() : "Nhập hàng qua admin";
                inventoryHistoryService.recordImport(
                    detail,
                    quantity,
                    request.getCostPrice(),  // ⭐ Giá nhập
                    note
                );
            }
        }
    }
    
    /**
     * ⚡ CACHED: Get all inventory with pagination and filters
     */
    @Override
    @Cacheable(value = "inventory", 
               key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + (#search != null ? #search : 'none') + ':' + (#warehouseId != null ? #warehouseId : 'none') + ':' + (#productSize != null ? #productSize : 'none')",
               unless = "#result == null")
    public Page<InventoryResponse> getAllInventory(Pageable pageable, String search, Long warehouseId, Integer productSize) {
        log.info("📦 Loading inventory (page: {}, search: {}, warehouse: {}, size: {})", 
                 pageable.getPageNumber(), search, warehouseId, productSize);
        
        // Get all inventory
        List<Inventory> allInventories = inventoryRepository.findAll();
        
        // Convert to response and apply filters
        List<InventoryResponse> responses = allInventories.stream()
                .map(inv -> {
                    ProductDetail pd = inv.getProductDetail();
                    if (pd == null || pd.getProduct() == null) {
                        return null;
                    }
                    
                    return InventoryResponse.builder()
                            .id(inv.getId())
                            .productDetailId(pd.getId())
                            .productId(pd.getProduct().getId())
                            .productName(pd.getProduct().getTitle())
                            .productImage(pd.getProduct().getImage())
                            .size(pd.getSize())
                            .warehouseName("Kho mặc định")
                            .warehouseId(null)
                            .quantity(inv.getRemainingQuantity())  // ✅ UPDATED
                            .soldQuantity(inv.getSoldQuantity())   // ✅ Calculated method
                            .totalQuantity(inv.getTotalQuantity()) // ✅ NEW
                            .build();
                })
                .filter(Objects::nonNull)
                // Apply search filter
                .filter(inv -> {
                    if (search == null || search.trim().isEmpty()) {
                        return true;
                    }
                    return inv.getProductName().toLowerCase().contains(search.toLowerCase());
                })
                // Apply size filter
                .filter(inv -> {
                    if (productSize == null) {
                        return true;
                    }
                    return productSize.equals(inv.getSize());
                })
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        
        List<InventoryResponse> pageContent = start >= responses.size() ? 
                List.of() : responses.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, responses.size());
    }
    
    /**
     * 🗑️ CACHE EVICT: Clear inventory cache when deleting
     */
    @Override
    @Transactional
    @CacheEvict(value = "inventory", allEntries = true)
    public void deleteInventory(Long id) {
        log.info("🗑️ Deleting inventory {}, clearing inventory cache", id);
        
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho với ID: " + id));
        
        inventoryRepository.delete(inventory);
    }
}
