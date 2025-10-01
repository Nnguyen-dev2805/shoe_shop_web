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
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    private final ProductDetailRepository productDetailRepository;

    @Override
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

                Inventory inventory = new Inventory();
                inventory.setProductDetail(detail);
                inventory.setQuantity(quantity);
                inventory.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());

                inventoryRepository.save(inventory);
            }
        }
    }
}
