package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.inventory.InventoryRequest;
import com.dev.shoeshop.dto.inventory.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {
    void addInventory(InventoryRequest request);
    
    /**
     * Get all inventory with pagination and filters
     * 
     * @param pageable Pagination info
     * @param search Search by product name
     * @param warehouseId Filter by warehouse
     * @param productSize Filter by product size
     * @return Page of InventoryResponse
     */
    Page<InventoryResponse> getAllInventory(Pageable pageable, String search, Long warehouseId, Integer productSize);
    
    /**
     * Delete inventory by ID
     * 
     * @param id Inventory ID
     */
    void deleteInventory(Long id);
}
