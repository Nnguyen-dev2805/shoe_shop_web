package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.discount.DiscountResponse;
import com.dev.shoeshop.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API Controller cho voucher system (Order & Shipping vouchers)
 */
@Slf4j
@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Tag(name = "Vouchers", description = "Voucher & Discount management APIs - Get order and shipping vouchers")
public class VoucherController {
    
    private final DiscountService discountService;
    
    /**
     * Get all active order vouchers (giảm giá đơn hàng)
     * GET /api/vouchers/order
     */
    @Operation(
        summary = "Get Order Vouchers",
        description = "Retrieve all active order discount vouchers"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved order vouchers",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"success\": true, \"count\": 5, \"data\": []}")
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @GetMapping("/order")
    public ResponseEntity<Map<String, Object>> getOrderVouchers() {
        log.info("API: Getting active order vouchers");
        
        try {
            List<DiscountResponse> vouchers = discountService.getActiveOrderVouchers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", vouchers);
            response.put("count", vouchers.size());
            response.put("message", "Lấy voucher đơn hàng thành công");
            
            log.info("✅ Found {} order vouchers", vouchers.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error getting order vouchers: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy voucher: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get all active shipping vouchers (giảm phí vận chuyển)
     * GET /api/vouchers/shipping
     */
    @Operation(
        summary = "Get Shipping Vouchers",
        description = "Retrieve all active shipping fee discount vouchers"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved shipping vouchers",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"success\": true, \"count\": 3, \"data\": []}")
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @GetMapping("/shipping")
    public ResponseEntity<Map<String, Object>> getShippingVouchers() {
        log.info("API: Getting active shipping vouchers");
        
        try {
            List<DiscountResponse> vouchers = discountService.getActiveShippingVouchers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", vouchers);
            response.put("count", vouchers.size());
            response.put("message", "Lấy voucher ship thành công");
            
            log.info("✅ Found {} shipping vouchers", vouchers.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error getting shipping vouchers: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy voucher ship: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Calculate shipping discount
     * POST /api/vouchers/shipping/calculate
     * 
     * Request body:
     * {
     *   "voucherId": 1,
     *   "shippingFee": 35000,
     *   "orderValue": 1700000
     * }
     */
    @PostMapping("/shipping/calculate")
    public ResponseEntity<Map<String, Object>> calculateShippingDiscount(
            @RequestBody Map<String, Object> request) {
        
        try {
            Long voucherId = Long.valueOf(request.get("voucherId").toString());
            Double shippingFee = Double.valueOf(request.get("shippingFee").toString());
            Double orderValue = Double.valueOf(request.get("orderValue").toString());
            
            log.info("API: Calculating shipping discount - Voucher: {}, Fee: {}, Order: {}", 
                    voucherId, shippingFee, orderValue);
            
            // Validate voucher
            boolean isValid = discountService.validateShippingVoucher(voucherId, orderValue, shippingFee);
            if (!isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Voucher không hợp lệ hoặc không đủ điều kiện sử dụng");
                
                return ResponseEntity.badRequest().body(response);
            }
            
            // Calculate discount
            Double discountAmount = discountService.calculateShippingDiscount(voucherId, shippingFee, orderValue);
            Double finalShippingFee = shippingFee - discountAmount;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("discountAmount", discountAmount);
            response.put("originalShippingFee", shippingFee);
            response.put("finalShippingFee", finalShippingFee);
            response.put("message", "Tính giảm phí ship thành công");
            
            log.info("✅ Calculated discount: {} (Original: {}, Final: {})", 
                    discountAmount, shippingFee, finalShippingFee);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error calculating shipping discount: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi tính giảm phí ship: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Validate shipping voucher
     * POST /api/vouchers/shipping/validate
     * 
     * Request body:
     * {
     *   "voucherId": 1,
     *   "orderValue": 1700000,
     *   "shippingFee": 35000
     * }
     */
    @PostMapping("/shipping/validate")
    public ResponseEntity<Map<String, Object>> validateShippingVoucher(
            @RequestBody Map<String, Object> request) {
        
        try {
            Long voucherId = Long.valueOf(request.get("voucherId").toString());
            Double orderValue = Double.valueOf(request.get("orderValue").toString());
            Double shippingFee = Double.valueOf(request.get("shippingFee").toString());
            
            log.info("API: Validating shipping voucher - Voucher: {}, Order: {}, Fee: {}", 
                    voucherId, orderValue, shippingFee);
            
            boolean isValid = discountService.validateShippingVoucher(voucherId, orderValue, shippingFee);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("valid", isValid);
            response.put("message", isValid ? "Voucher hợp lệ" : "Voucher không hợp lệ");
            
            log.info("✅ Voucher validation result: {}", isValid);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error validating shipping voucher: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi validate voucher: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
