package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.inventory.InventoryRequest;
import com.dev.shoeshop.dto.inventory.InventoryResponse;
import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.repository.InventoryRepository;
import com.dev.shoeshop.repository.ProductDetailRepository;
import com.dev.shoeshop.service.InventoryService;
import lombok.RequiredArgsConstructor;
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
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    private final ProductDetailRepository productDetailRepository;

    @Override
    @Transactional
    public void addInventory(InventoryRequest request) {
        List<ProductDetail> details = productDetailRepository.findByProductId(request.getProductId());

        for (Map.Entry<Integer, Integer> entry : request.getSizes().entrySet()) {
            Integer size = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity != null && quantity > 0) {
                ProductDetail detail = details.stream()
                        .filter(d -> Objects.equals(d.getSize(), size))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy product_detail cho size " + size));

                // Tìm inventory record hiện có
                Inventory inventory = inventoryRepository.findByProductDetail(detail);
                
                if (inventory != null) {
                    // Nếu đã tồn tại, cộng thêm số lượng
                    inventory.setQuantity(inventory.getQuantity() + quantity);
                } else {
                    // Nếu chưa tồn tại, tạo mới
                    inventory = new Inventory();
                    inventory.setProductDetail(detail);
                    inventory.setQuantity(quantity);
                }

                inventoryRepository.save(inventory);
            }
        }
    }
    
    @Override
    public Page<InventoryResponse> getAllInventory(Pageable pageable, String search, Long warehouseId, Integer productSize) {
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
                            .productDetailId(pd.getId())  // ✅ THÊM
                            .productId(pd.getProduct().getId())  // ✅ THÊM
                            .productName(pd.getProduct().getTitle())
                            .productImage(pd.getProduct().getImage())
                            .size(pd.getSize())
                            .warehouseName("Kho mặc định")
                            .warehouseId(null)
                            .quantity(inv.getQuantity())
                            // ✅ NEW - Cost tracking fields
                            .costPrice(inv.getCostPrice())
                            .soldQuantity(inv.getSoldQuantity())
                            .initialQuantity(inv.getInitialQuantity())
                            .note(inv.getNote())
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
    
    @Override
    @Transactional
    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho với ID: " + id));
        
        inventoryRepository.delete(inventory);
    }
}
