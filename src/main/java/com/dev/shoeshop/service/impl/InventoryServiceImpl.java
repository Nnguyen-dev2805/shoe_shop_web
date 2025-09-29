package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.inventory.InventoryRequest;
import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.repository.InventoryRepository;
import com.dev.shoeshop.repository.ProductDetailRepository;
import com.dev.shoeshop.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    private final ProductDetailRepository productDetailRepository;

    @Override
    public void addInventory(InventoryRequest request) {
        Inventory inventory = new Inventory();
        ProductDetail detail = productDetailRepository.findById(request.getProductDetailId()).get();
        inventory.setQuantity(request.getQuantity());
        inventory.setProductDetail(detail);
        inventory.setCreatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);
    }
}
