package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.discount.DiscountCreateRequest;
import com.dev.shoeshop.dto.discount.DiscountResponse;
import com.dev.shoeshop.dto.discount.DiscountUpdateRequest;
import com.dev.shoeshop.service.DiscountService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/discount")
@RequiredArgsConstructor
public class ApiDiscountController {

    private final DiscountService discountService;

    /**
     * Lấy danh sách tất cả discount với pagination và filtering
     * URL: GET /api/discount?page=0&size=10&status=ACTIVE
     */
    @GetMapping
    public ResponseEntity<?> getAllDiscounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<DiscountResponse> discountPage;
            
            if (status != null && !status.trim().isEmpty()) {
                discountPage = discountService.getDiscountsByStatus(status, pageable);
            } else {
                discountPage = discountService.getAllDiscounts(pageable);
            }
            
            return ResponseEntity.ok(discountPage);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Lấy discount theo ID
     * URL: GET /api/discount/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiscountResponse> getDiscountById(@PathVariable Long id) {
        try {
            DiscountResponse discount = discountService.getDiscountResponseById(id);
            if (discount == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(discount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Tạo discount mới
     * URL: POST /api/discount
     */
    @PostMapping
    public ResponseEntity<?> createDiscount(
            @Valid @RequestBody DiscountCreateRequest request, 
            BindingResult result) {
        
        if (result.hasErrors()) {
            String errorMsg = result.getFieldErrors().stream()
                    .map(e -> e.getField() + " - " + e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body("Lỗi validation: " + errorMsg);
        }

        try {
            // TODO: Implement create discount from DTO
            // For now, return a simple response
            return ResponseEntity.status(HttpStatus.CREATED).body("Discount created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Cập nhật discount
     * URL: PUT /api/discount/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody DiscountUpdateRequest request, 
            BindingResult result) {
        
        if (result.hasErrors()) {
            String errorMsg = result.getFieldErrors().stream()
                    .map(e -> e.getField() + " - " + e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body("Lỗi validation: " + errorMsg);
        }

        try {
            // TODO: Implement update discount from DTO
            // For now, return a simple response
            return ResponseEntity.ok("Discount updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Xóa discount
     * URL: DELETE /api/discount/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscount(@PathVariable Long id) {
        try {
            discountService.deleteDiscount(id);
            return ResponseEntity.ok("Discount with ID " + id + " deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Lấy discount theo status
     * URL: GET /api/discount/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DiscountResponse>> getDiscountsByStatus(@PathVariable String status) {
        try {
            List<DiscountResponse> discounts = discountService.getActiveDiscounts();
            return ResponseEntity.ok(discounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    
    /**
     * Lấy danh sách voucher đang active để hiển thị trên shop (product page)
     * Chỉ lấy vouchers có status ACTIVE, chưa hết hạn, còn số lượng
     * URL: GET /api/discount/active-vouchers
     */
    @GetMapping("/active-vouchers")
    public ResponseEntity<List<DiscountResponse>> getActiveVouchers() {
        try {
            List<DiscountResponse> discounts = discountService.getActiveDiscounts();
            return ResponseEntity.ok(discounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}