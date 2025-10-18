package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.inventory.InventoryRequest;
import com.dev.shoeshop.dto.inventory.InventoryResponse;
import com.dev.shoeshop.dto.pagination.PaginationResponse;
import com.dev.shoeshop.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class ApiInventoryController {

    private final InventoryService inventoryService;

    /**
     * TEST ENDPOINT - Verify controller is loaded
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API Inventory Controller is working!");
    }

    /**
     * GET ALL INVENTORY - With Pagination
     * 
     * Method: GET
     * URL: /api/inventory
     * 
     * @param page Page number (0-indexed)
     * @param size Items per page
     * @param search Search term for product name
     * @param warehouseId Filter by warehouse
     * @param size Filter by product size
     * @return PaginationResponse<InventoryResponse>
     */
    @GetMapping
    public ResponseEntity<PaginationResponse<InventoryResponse>> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(name = "productSize", required = false) Integer productSize) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<InventoryResponse> inventoryPage = inventoryService.getAllInventory(
                    pageable, search, warehouseId, productSize);
            
            PaginationResponse<InventoryResponse> response = new PaginationResponse<>(
                    inventoryPage.getContent(),
                    inventoryPage.getTotalPages(),
                    inventoryPage.getTotalElements(),
                    inventoryPage.getNumber() + 1,
                    inventoryPage.getSize()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> addInventory(@Valid @RequestBody InventoryRequest request, BindingResult result){
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Lỗi validation: ");
            for (var error : result.getFieldErrors()) {
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMsg.toString());
        }


        try {
            inventoryService.addInventory(request);
            return ResponseEntity.ok("Thêm tồn kho thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * DELETE INVENTORY
     * 
     * Method: DELETE
     * URL: /api/inventory/{id}
     * 
     * @param id Inventory ID
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long id) {
        try {
            inventoryService.deleteInventory(id);
            return ResponseEntity.ok("Xóa tồn kho thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi: " + e.getMessage());
        }
    }

}
