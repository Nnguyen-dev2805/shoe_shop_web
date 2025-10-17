package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.flashsale.request.BulkAddItemsRequest;
import com.dev.shoeshop.dto.flashsale.response.FlashSaleResponse;
import com.dev.shoeshop.entity.FlashSale;
import com.dev.shoeshop.enums.FlashSaleStatus;
import com.dev.shoeshop.repository.FlashSaleRepository;
import com.dev.shoeshop.service.FlashSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller cho Flash Sale - Admin Side
 * 
 * Base URL: /admin/api/flash-sale
 * 
 * Security: Chỉ admin mới truy cập được
 * @PreAuthorize("hasRole('ADMIN')")
 * 
 * Endpoints:
 * - POST / - Tạo flash sale mới
 * - GET /list - Lấy danh sách tất cả flash sales
 * - GET /{id} - Lấy chi tiết flash sale
 * - PUT /{id} - Cập nhật flash sale
 * - DELETE /{id} - Xóa flash sale (soft delete)
 * - POST /{id}/bulk-add - Thêm nhiều sản phẩm vào flash sale
 * - PUT /{id}/status - Cập nhật status flash sale
 */
@RestController
@RequestMapping("/admin/api/flash-sale")
@RequiredArgsConstructor
@Slf4j
//@PreAuthorize("hasRole('ADMIN')") // Chỉ admin mới access
public class AdminFlashSaleController {
    
    private final FlashSaleService flashSaleService;
    private final FlashSaleRepository flashSaleRepo;
    private final com.dev.shoeshop.repository.FlashSaleItemRepository flashSaleItemRepo;
    
    /**
     * API 0: LẤY DANH SÁCH FLASH SALES VỚI PAGINATION
     * 
     * Method: GET
     * URL: /admin/api/flash-sale?page=0&size=10&status=ACTIVE
     * Auth: Admin only
     * 
     * Query Params:
     * - page: Trang (0-indexed)
     * - size: Số items mỗi trang
     * - status: Filter theo status (optional)
     * 
     * Response:
     * {
     *   "content": [...],
     *   "totalPages": 5,
     *   "totalElements": 45,
     *   "number": 0,
     *   "size": 10
     * }
     * 
     * Dùng cho:
     * - Admin list page với pagination
     * - Filter theo status
     */
    @GetMapping
    public ResponseEntity<Page<FlashSale>> getFlashSalesWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) FlashSaleStatus status) {
        
        log.info("Admin API: Getting flash sales - page={}, size={}, status={}", page, size, status);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<FlashSale> flashSales;
        
        if (status != null) {
            // Filter by status
            flashSales = flashSaleRepo.findByStatusAndIsDeleteFalse(status, pageable);
        } else {
            // Get all
            flashSales = flashSaleRepo.findByIsDeleteFalse(pageable);
        }
        
        log.info("Found {} flash sales (page {}/{})", 
                 flashSales.getContent().size(), 
                 flashSales.getNumber() + 1, 
                 flashSales.getTotalPages());
        
        return ResponseEntity.ok(flashSales);
    }
    
    /**
     * API 1: TẠO FLASH SALE MỚI
     * 
     * Method: POST
     * URL: /admin/api/flash-sale
     * Auth: Admin only
     * 
     * Request Body:
     * {
     *   "name": "Flash Sale 12h Trưa",
     *   "description": "Giảm giá cực sốc giữa trưa",
     *   "startTime": "2024-10-20T12:00:00",
     *   "endTime": "2024-10-20T14:00:00",
     *   "bannerImage": "https://example.com/banner.jpg"
     * }
     * 
     * Response:
     * - 200 OK: { success: true, flashSaleId: 1 }
     * - 400 Bad Request: Validation errors
     * 
     * Dùng cho:
     * - Admin tạo flash sale mới
     * - Form "Tạo Flash Sale"
     * 
     * Frontend call:
     * $('#createFlashSaleForm').submit(function(e) {
     *   e.preventDefault();
     *   $.ajax({
     *     url: '/admin/api/flash-sale',
     *     method: 'POST',
     *     contentType: 'application/json',
     *     data: JSON.stringify({
     *       name: $('#name').val(),
     *       startTime: $('#startTime').val(),
     *       endTime: $('#endTime').val(),
     *       ...
     *     }),
     *     success: function(response) {
     *       alert('Tạo thành công!');
     *       window.location.href = '/admin/flash-sale/' + response.flashSaleId + '/add-items';
     *     }
     *   });
     * });
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createFlashSale(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String bannerImage) {
        
        log.info("Admin API: Creating flash sale - {}", name);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate thời gian
            if (startTime.isAfter(endTime)) {
                response.put("success", false);
                response.put("message", "Thời gian bắt đầu phải trước thời gian kết thúc!");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (startTime.isBefore(LocalDateTime.now())) {
                response.put("success", false);
                response.put("message", "Thời gian bắt đầu phải sau thời gian hiện tại!");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Tạo flash sale
            FlashSale flashSale = FlashSale.builder()
                .name(name)
                .description(description)
                .startTime(startTime)
                .endTime(endTime)
                .status(FlashSaleStatus.SCHEDULED) // Mặc định SCHEDULED
                .bannerImage(bannerImage)
                .totalItems(0)
                .totalSold(0)
                .isDelete(false)
                .createdDate(LocalDateTime.now())
                .build();
            
            flashSale = flashSaleRepo.save(flashSale);
            
            response.put("success", true);
            response.put("flashSaleId", flashSale.getId());
            response.put("message", "Tạo Flash Sale thành công!");
            
            log.info("Flash sale created: id={}", flashSale.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create flash sale: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * API 2: LẤY DANH SÁCH TẤT CẢ FLASH SALES
     * 
     * Method: GET
     * URL: /admin/api/flash-sale/list
     * Auth: Admin only
     * 
     * Response:
     * - 200 OK: List<FlashSale>
     * 
     * Dùng cho:
     * - Admin page: Danh sách tất cả flash sales
     * - Table hiển thị flash sales với status, dates, items
     * 
     * Frontend call:
     * $.get('/admin/api/flash-sale/list', function(data) {
     *   data.forEach(fs => {
     *     addRowToTable(fs);
     *   });
     * });
     */
    @GetMapping("/list")
    public ResponseEntity<List<FlashSale>> getAllFlashSales() {
        log.info("Admin API: Getting all flash sales");
        
        List<FlashSale> flashSales = flashSaleRepo.findByIsDeleteFalseOrderByCreatedDateDesc();
        
        log.info("Found {} flash sales", flashSales.size());
        return ResponseEntity.ok(flashSales);
    }
    
    /**
     * API 3: LẤY CHI TIẾT FLASH SALE
     * 
     * Method: GET
     * URL: /admin/api/flash-sale/{id}
     * Auth: Admin only
     * 
     * Response:
     * - 200 OK: FlashSaleResponse (bao gồm items)
     * - 404 Not Found: Flash sale không tồn tại
     * 
     * Dùng cho:
     * - Admin xem chi tiết flash sale
     * - Edit flash sale form
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFlashSaleById(@PathVariable Long id) {
        log.info("Admin API: Getting flash sale {}", id);
        
        FlashSale flashSale = flashSaleRepo.findById(id)
            .orElse(null);
        
        if (flashSale == null) {
            log.warn("Flash sale {} not found", id);
            return ResponseEntity.notFound().build();
        }
        
        // Lấy items của flash sale
        var items = flashSaleService.getFlashSaleItems(id);
        
        // Tạo response
        Map<String, Object> response = new HashMap<>();
        response.put("id", flashSale.getId());
        response.put("name", flashSale.getName());
        response.put("description", flashSale.getDescription());
        response.put("startTime", flashSale.getStartTime());
        response.put("endTime", flashSale.getEndTime());
        response.put("status", flashSale.getStatus().name());
        response.put("totalItems", flashSale.getTotalItems());
        response.put("totalSold", flashSale.getTotalSold());
        response.put("bannerImage", flashSale.getBannerImage());
        response.put("items", items);  // ✅ Thêm items
        
        // ✅ Tính số products (unique) thay vì số items
        long uniqueProducts = items.stream()
            .map(item -> item.getProductName())
            .distinct()
            .count();
        response.put("totalProducts", uniqueProducts);
        
        log.info("Flash sale {} loaded with {} items ({} unique products)", id, items.size(), uniqueProducts);
        return ResponseEntity.ok(response);
    }
    
    /**
     * API 3.1: LẤY DANH SÁCH ITEMS CỦA FLASH SALE
     * 
     * Method: GET
     * URL: /admin/api/flash-sale/{id}/items
     * Auth: Admin only
     * 
     * Response:
     * - 200 OK: List<FlashSaleItemResponse>
     * 
     * Dùng cho:
     * - Admin edit page - Hiển thị danh sách items
     * - Load items để xóa/sửa
     */
    @GetMapping("/{id}/items")
    public ResponseEntity<List<com.dev.shoeshop.dto.flashsale.response.FlashSaleItemResponse>> getFlashSaleItems(
            @PathVariable Long id) {
        
        log.info("Admin API: Getting items for flash sale {}", id);
        
        var items = flashSaleService.getFlashSaleItems(id);
        
        log.info("Found {} items for flash sale {}", items.size(), id);
        return ResponseEntity.ok(items);
    }
    
    /**
     * API 4: CẬP NHẬT FLASH SALE
     * 
     * Method: PUT
     * URL: /admin/api/flash-sale/{id}
     * Auth: Admin only
     * 
     * Request Params:
     * - name, description, startTime, endTime, bannerImage
     * 
     * Response:
     * - 200 OK: { success: true, message: "..." }
     * - 404 Not Found: Flash sale không tồn tại
     * 
     * Dùng cho:
     * - Admin sửa thông tin flash sale
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateFlashSale(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String bannerImage) {
        
        log.info("Admin API: Updating flash sale {}", id);
        
        Map<String, Object> response = new HashMap<>();
        
        return flashSaleRepo.findById(id)
            .map(flashSale -> {
                // Chỉ update những fields không null
                if (name != null) flashSale.setName(name);
                if (description != null) flashSale.setDescription(description);
                if (startTime != null) flashSale.setStartTime(startTime);
                if (endTime != null) flashSale.setEndTime(endTime);
                if (bannerImage != null) flashSale.setBannerImage(bannerImage);
                
                flashSaleRepo.save(flashSale);
                
                response.put("success", true);
                response.put("message", "Cập nhật thành công!");
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                response.put("success", false);
                response.put("message", "Flash sale không tồn tại!");
                return ResponseEntity.notFound().build();
            });
    }
    
    /**
     * API 5: XÓA FLASH SALE (Soft Delete)
     * 
     * Method: DELETE
     * URL: /admin/api/flash-sale/{id}
     * Auth: Admin only
     * 
     * Response:
     * - 200 OK: { success: true }
     * - 404 Not Found: Flash sale không tồn tại
     * 
     * Dùng cho:
     * - Admin xóa flash sale
     * - Soft delete: set isDelete = true
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFlashSale(@PathVariable Long id) {
        log.info("Admin API: Deleting flash sale {}", id);
        
        Map<String, Object> response = new HashMap<>();
        
        return flashSaleRepo.findById(id)
            .map(flashSale -> {
                flashSale.setIsDelete(true);
                flashSaleRepo.save(flashSale);
                
                response.put("success", true);
                response.put("message", "Xóa thành công!");
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                response.put("success", false);
                response.put("message", "Flash sale không tồn tại!");
                return ResponseEntity.notFound().build();
            });
    }
    
    /**
     * API 6: BULK ADD - THÊM NHIỀU SẢN PHẨM VÀO FLASH SALE
     * 
     * Method: POST
     * URL: /admin/api/flash-sale/{id}/bulk-add
     * Auth: Admin only
     * 
     * Request Body:
     * {
     *   "productDetailIds": [123, 124, 125, 126],
     *   "discountPercent": 50
     * }
     * 
     * Response:
     * - 200 OK: { success: true, addedCount: 4 }
     * - 400 Bad Request: Validation errors
     * 
     * Dùng cho:
     * - Admin thêm nhiều sản phẩm cùng lúc
     * - Page "Thêm sản phẩm vào Flash Sale"
     * - Checkbox list products → Click "THÊM TẤT CẢ"
     * 
     * Frontend call:
     * $('#bulkAddForm').submit(function(e) {
     *   e.preventDefault();
     *   
     *   // Lấy tất cả checkbox đã chọn
     *   var selectedIds = [];
     *   $('.product-checkbox:checked').each(function() {
     *     selectedIds.push($(this).data('product-detail-id'));
     *   });
     *   
     *   $.ajax({
     *     url: '/admin/api/flash-sale/1/bulk-add',
     *     method: 'POST',
     *     contentType: 'application/json',
     *     data: JSON.stringify({
     *       productDetailIds: selectedIds,
     *       discountPercent: $('#discountPercent').val()
     *     }),
     *     success: function(response) {
     *       alert('Đã thêm ' + response.addedCount + ' sản phẩm!');
     *       location.reload();
     *     }
     *   });
     * });
     */
    @PostMapping("/{id}/bulk-add")
    public ResponseEntity<Map<String, Object>> bulkAddItems(
            @PathVariable Long id,
            @Valid @RequestBody BulkAddItemsRequest request) {
        
        log.info("Admin API: Bulk adding {} items to flash sale {}", 
                 request.getProductDetailIds().size(), id);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            int addedCount = flashSaleService.bulkAddItems(id, request);
            
            response.put("success", true);
            response.put("addedCount", addedCount);
            response.put("message", "Đã thêm " + addedCount + " sản phẩm vào flash sale!");
            
            log.info("Bulk add successful: {} items added", addedCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Bulk add failed: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * API 7: CẬP NHẬT STATUS FLASH SALE
     * 
     * Method: PUT
     * URL: /admin/api/flash-sale/{id}/status
     * Auth: Admin only
     * 
     * Request Params:
     * - status: SCHEDULED, ACTIVE, ENDED, CANCELLED
     * 
     * Response:
     * - 200 OK: { success: true }
     * 
     * Dùng cho:
     * - Admin thay đổi status thủ công
     * - VD: Hủy flash sale → CANCELLED
     * - VD: Active sớm → ACTIVE
     * 
     * Frontend call:
     * $.ajax({
     *   url: '/admin/api/flash-sale/1/status?status=CANCELLED',
     *   method: 'PUT',
     *   success: function() {
     *     alert('Đã hủy flash sale!');
     *   }
     * });
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id,
            @RequestParam FlashSaleStatus status) {
        
        log.info("Admin API: Updating flash sale {} status to {}", id, status);
        
        Map<String, Object> response = new HashMap<>();
        
        return flashSaleRepo.findById(id)
            .map(flashSale -> {
                flashSale.setStatus(status);
                flashSaleRepo.save(flashSale);
                
                response.put("success", true);
                response.put("message", "Cập nhật status thành công!");
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                response.put("success", false);
                response.put("message", "Flash sale không tồn tại!");
                return ResponseEntity.notFound().build();
            });
    }
    
    /**
     * API 8: LẤY THỐNG KÊ FLASH SALE
     * 
     * Method: GET
     * URL: /admin/api/flash-sale/{id}/statistics
     * Auth: Admin only
     * 
     * Response:
     * {
     *   "totalItems": 50,
     *   "totalSold": 123,
     *   "totalRevenue": 123456789.0,
     *   "soldPercentage": 45.6,
     *   "topSellingItems": [...]
     * }
     * 
     * Dùng cho:
     * - Dashboard thống kê
     * - Báo cáo flash sale
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(@PathVariable Long id) {
        log.info("Admin API: Getting statistics for flash sale {}", id);
        
        // TODO: Implement statistics logic
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Feature coming soon");
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * API 9: THÊM PRODUCT VÀO FLASH SALE (TỰ ĐỘNG TẤT CẢ SIZES)
     * 
     * Method: POST
     * URL: /admin/api/flash-sale/{id}/add-product
     * Auth: Admin only
     * 
     * Request Params:
     * - productId: ID của product cần thêm
     * - discountPercent: % giảm giá (1-99)
     * 
     * Response:
     * {
     *   "success": true,
     *   "itemsCreated": 5,
     *   "message": "Đã thêm 5 sizes vào flash sale!"
     * }
     * 
     * Dùng cho:
     * - Admin chọn Product → Tự động thêm TẤT CẢ sizes vào flash sale
     * - Không cần chọn từng ProductDetail nữa
     * 
     * Example:
     * POST /admin/api/flash-sale/1/add-product?productId=5&discountPercent=50
     * → Tạo FlashSaleItem cho tất cả sizes của Nike Air Max (giảm 50%)
     */
    @PostMapping("/{id}/add-product")
    public ResponseEntity<Map<String, Object>> addProductToFlashSale(
            @PathVariable Long id,
            @RequestParam Long productId,
            @RequestParam Double discountPercent) {
        
        log.info("Admin API: Adding product {} to flash sale {} with {}% discount", 
                 productId, id, discountPercent);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            int itemsCreated = flashSaleService.addProductToFlashSale(id, productId, discountPercent);
            
            response.put("success", true);
            response.put("itemsCreated", itemsCreated);
            response.put("message", "Đã thêm " + itemsCreated + " sizes vào flash sale!");
            
            log.info("Successfully added product {}: {} items created", productId, itemsCreated);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to add product to flash sale: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * API 9: XÓA FLASH SALE ITEM
     * 
     * Method: DELETE
     * URL: /admin/api/flash-sale/item/{itemId}
     * Auth: Admin only
     * 
     * Response:
     * - 200 OK: { success: true, message: "Đã xóa item" }
     * - 404 Not Found: Item không tồn tại
     * 
     * Dùng cho:
     * - Admin xóa sản phẩm khỏi flash sale
     */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteFlashSaleItem(@PathVariable Long itemId) {
        log.info("Admin API: Deleting flash sale item {}", itemId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Tìm item để lấy flash sale id
            var itemEntity = flashSaleItemRepo.findById(itemId)
                .orElse(null);
            
            if (itemEntity == null) {
                response.put("success", false);
                response.put("message", "Item không tồn tại");
                return ResponseEntity.notFound().build();
            }
            
            // Lấy flash sale để update total_items
            var flashSale = itemEntity.getFlashSale();
            
            // Xóa item
            flashSaleItemRepo.deleteById(itemId);
            
            // Update total_items
            if (flashSale != null && flashSale.getTotalItems() > 0) {
                flashSale.setTotalItems(flashSale.getTotalItems() - 1);
                flashSaleRepo.save(flashSale);
            }
            
            response.put("success", true);
            response.put("message", "Đã xóa item khỏi flash sale");
            
            log.info("Successfully deleted flash sale item {}", itemId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to delete flash sale item: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
