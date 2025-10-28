package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.flashsale.request.PurchaseFlashSaleRequest;
import com.dev.shoeshop.dto.flashsale.response.FlashSaleItemResponse;
import com.dev.shoeshop.dto.flashsale.response.FlashSaleResponse;
import com.dev.shoeshop.dto.flashsale.response.StockResponse;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.FlashSaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller cho Flash Sale - User Side
 * 
 * Base URL: /api/flash-sale
 * 
 * Endpoints:
 * - GET /active - Lấy flash sale đang diễn ra
 * - GET /upcoming - Lấy flash sale sắp diễn ra
 * - GET /{id} - Lấy chi tiết flash sale
 * - GET /{id}/items - Lấy danh sách sản phẩm
 * - GET /item/{itemId}/stock - Lấy stock real-time (AJAX polling)
 * - POST /purchase - Mua flash sale item
 */
@Tag(name = "Flash Sale", description = "Flash sale management APIs for time-limited special offers")
@RestController
@RequestMapping("/api/flash-sale")
@RequiredArgsConstructor
@Slf4j
public class FlashSaleApiController {
    
    private final FlashSaleService flashSaleService;
    
    /**
     * API 1: Lấy flash sale đang ACTIVE (đang diễn ra)
     * 
     * Method: GET
     * URL: /api/flash-sale/active
     * Auth: Public (không cần login)
     * 
     * Response:
     * - 200 OK: Trả về FlashSaleResponse (bao gồm items)
     * - 204 No Content: Không có flash sale đang diễn ra
     * 
     * Dùng cho:
     * - Homepage: Hiển thị banner flash sale
     * - Countdown timer
     * - Danh sách sản phẩm flash sale
     * 
     * Frontend call:
     * $.ajax({
     *   url: '/api/flash-sale/active',
     *   success: function(data) {
     *     if (data) {
     *       displayFlashSale(data);
     *       startCountdown(data.endTime);
     *     }
     *   }
     * });
     */
    @Operation(
        summary = "Get active flash sale",
        description = "Retrieves the currently active flash sale with all items and time information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active flash sale found", 
            content = @Content(schema = @Schema(implementation = FlashSaleResponse.class))),
        @ApiResponse(responseCode = "204", description = "No active flash sale")
    })
    @GetMapping("/active")
    public ResponseEntity<FlashSaleResponse> getActiveFlashSale() {
        log.info("API: Getting active flash sale");
        
        FlashSaleResponse flashSale = flashSaleService.getActiveFlashSale();
        
        if (flashSale == null) {
            log.info("No active flash sale found");
            return ResponseEntity.noContent().build();
        }
        
        log.info("Found active flash sale: {}", flashSale.getName());
        return ResponseEntity.ok(flashSale);
    }
    
    /**
     * API 2: Lấy flash sale sắp diễn ra (upcoming)
     * 
     * Method: GET
     * URL: /api/flash-sale/upcoming
     * Auth: Public
     * 
     * Response:
     * - 200 OK: Trả về FlashSaleResponse
     * - 204 No Content: Không có flash sale sắp diễn ra
     * 
     * Dùng cho:
     * - Homepage: Hiển thị "Sắp bắt đầu"
     * - Countdown đếm ngược đến startTime
     * - Preview sản phẩm sắp sale
     * 
     * Frontend call:
     * $.ajax({
     *   url: '/api/flash-sale/upcoming',
     *   success: function(data) {
     *     if (data) {
     *       displayUpcoming(data);
     *       startCountdownToStart(data.startTime);
     *     }
     *   }
     * });
     */
    @Operation(
        summary = "Get upcoming flash sale",
        description = "Retrieves the next scheduled flash sale that hasn't started yet"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upcoming flash sale found",
            content = @Content(schema = @Schema(implementation = FlashSaleResponse.class))),
        @ApiResponse(responseCode = "204", description = "No upcoming flash sale")
    })
    @GetMapping("/upcoming")
    public ResponseEntity<FlashSaleResponse> getUpcomingFlashSale() {
        log.info("API: Getting upcoming flash sale");
        
        FlashSaleResponse flashSale = flashSaleService.getUpcomingFlashSale();
        
        if (flashSale == null) {
            log.info("No upcoming flash sale found");
            return ResponseEntity.noContent().build();
        }
        
        log.info("Found upcoming flash sale: {}", flashSale.getName());
        return ResponseEntity.ok(flashSale);
    }
    
    /**
     * API 3: Lấy danh sách items của flash sale
     * 
     * Method: GET
     * URL: /api/flash-sale/{flashSaleId}/items
     * Auth: Public
     * 
     * Params:
     * - flashSaleId: ID của flash sale
     * 
     * Response:
     * - 200 OK: List<FlashSaleItemResponse>
     * 
     * Dùng cho:
     * - Load thêm sản phẩm (pagination)
     * - Filter sản phẩm flash sale
     * 
     * Frontend call:
     * $.ajax({
     *   url: '/api/flash-sale/1/items',
     *   success: function(items) {
     *     items.forEach(item => renderProduct(item));
     *   }
     * });
     */
    @Operation(
        summary = "Get flash sale items",
        description = "Retrieves all products included in a specific flash sale"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved flash sale items")
    })
    @GetMapping("/{flashSaleId}/items")
    public ResponseEntity<List<FlashSaleItemResponse>> getFlashSaleItems(
            @Parameter(description = "Flash Sale ID", required = true, example = "1") 
            @PathVariable Long flashSaleId) {
        
        log.info("API: Getting items for flash sale {}", flashSaleId);
        
        List<FlashSaleItemResponse> items = flashSaleService.getFlashSaleItems(flashSaleId);
        
        log.info("Found {} items for flash sale {}", items.size(), flashSaleId);
        return ResponseEntity.ok(items);
    }
    
    /**
     * API 4: Lấy stock real-time của một flash sale item
     * 
     * Method: GET
     * URL: /api/flash-sale/item/{itemId}/stock
     * Auth: Public
     * 
     * Params:
     * - itemId: ID của flash sale item
     * 
     * Response:
     * - 200 OK: StockResponse { stock, sold, remaining, soldPercentage }
     * 
     * Dùng cho:
     * - AJAX POLLING: Gọi mỗi 3-5 giây để update stock real-time
     * - Progress bar hiển thị % đã bán
     * - Enable/disable nút "MUA NGAY"
     * 
     * Frontend call (POLLING):
     * setInterval(function() {
     *   $.ajax({
     *     url: '/api/flash-sale/item/123/stock',
     *     success: function(data) {
     *       updateProgressBar(data.soldPercentage);
     *       updateStockText(data.remaining);
     *       if (data.remaining === 0) {
     *         disableBuyButton();
     *       }
     *     }
     *   });
     * }, 3000); // 3 giây
     */
    @Operation(
        summary = "Get real-time stock info",
        description = "Returns current stock status for a flash sale item (for AJAX polling every 3-5 seconds)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock info retrieved",
            content = @Content(schema = @Schema(implementation = StockResponse.class)))
    })
    @GetMapping("/item/{itemId}/stock")
    public ResponseEntity<StockResponse> getStockInfo(
            @Parameter(description = "Flash Sale Item ID", required = true, example = "123") 
            @PathVariable Long itemId) {
        log.debug("API: Getting stock info for item {}", itemId);
        
        StockResponse stock = flashSaleService.getStockInfo(itemId);
        
        return ResponseEntity.ok(stock);
    }
    
    /**
     * API 5: MUA SẢN PHẨM FLASH SALE - API QUAN TRỌNG NHẤT
     * 
     * Method: POST
     * URL: /api/flash-sale/purchase
     * Auth: Required (phải login)
     * 
     * Request Body:
     * {
     *   "flashSaleItemId": 123,
     *   "quantity": 2
     * }
     * 
     * Response:
     * - 200 OK: { success: true, orderId: 456, message: "..." }
     * - 400 Bad Request: { success: false, message: "Hết hàng!" }
     * - 401 Unauthorized: Chưa login
     * 
     * Dùng cho:
     * - User click nút "MUA NGAY"
     * - Xử lý purchase flow
     * - Redirect đến checkout page
     * 
     * Frontend call:
     * $('#buyButton').click(function() {
     *   $.ajax({
     *     url: '/api/flash-sale/purchase',
     *     method: 'POST',
     *     contentType: 'application/json',
     *     data: JSON.stringify({
     *       flashSaleItemId: itemId,
     *       quantity: $('#quantity').val()
     *     }),
     *     success: function(response) {
     *       if (response.success) {
     *         window.location.href = '/checkout?orderId=' + response.orderId;
     *       }
     *     },
     *     error: function(xhr) {
     *       alert(xhr.responseJSON.message);
     *     }
     *   });
     * });
     * 
     * Security:
     * - Dùng @AuthenticationPrincipal để lấy user đang login
     * - Validate user không null
     * - FlashSaleService sẽ check stock, timeout, inventory
     */
    @Operation(
        summary = "Purchase flash sale item",
        description = "Process flash sale purchase. Validates stock, creates order. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Purchase successful"),
        @ApiResponse(responseCode = "400", description = "Out of stock or invalid request"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchaseFlashSaleItem(
            @Valid @RequestBody PurchaseFlashSaleRequest request,
            @AuthenticationPrincipal Users user) {
        
        log.info("API: User {} purchasing flash sale item {}, quantity: {}", 
                 user != null ? user.getId() : "unknown", 
                 request.getFlashSaleItemId(), 
                 request.getQuantity());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate user đã login
            if (user == null) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để mua hàng!");
                return ResponseEntity.status(401).body(response);
            }
            
            // Gọi service mua hàng
            Order order = flashSaleService.purchaseFlashSaleItem(request, user.getId());
            
            // Success response
            response.put("success", true);
            response.put("orderId", order.getId());
            response.put("message", "Mua hàng thành công! Đang chuyển đến trang thanh toán...");
            response.put("totalPrice", order.getTotalPrice());
            response.put("discountAmount", order.getDiscountAmount());
            
            log.info("Purchase successful: orderId={}, userId={}", order.getId(), user.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Purchase failed: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * API 6: Kiểm tra flash sale có đang active không
     * 
     * Method: GET
     * URL: /api/flash-sale/status
     * Auth: Public
     * 
     * Response:
     * - 200 OK: { hasActive: true/false, hasUpcoming: true/false }
     * 
     * Dùng cho:
     * - Quick check có flash sale không
     * - Header notification badge
     * 
     * Frontend call:
     * $.get('/api/flash-sale/status', function(data) {
     *   if (data.hasActive) {
     *     showFlashSaleBadge();
     *   }
     * });
     */
    @Operation(
        summary = "Check flash sale status",
        description = "Quick check to see if there are any active or upcoming flash sales"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    })
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getFlashSaleStatus() {
        log.debug("API: Checking flash sale status");
        
        Map<String, Boolean> status = new HashMap<>();
        
        FlashSaleResponse active = flashSaleService.getActiveFlashSale();
        FlashSaleResponse upcoming = flashSaleService.getUpcomingFlashSale();
        
        status.put("hasActive", active != null);
        status.put("hasUpcoming", upcoming != null);
        
        return ResponseEntity.ok(status);
    }
}
