package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.AddressDTO;
import com.dev.shoeshop.dto.CartDTO;
import com.dev.shoeshop.dto.OrderResultDTO;
import com.dev.shoeshop.dto.PendingPaymentDTO;
import com.dev.shoeshop.dto.discount.DiscountResponse;
import com.dev.shoeshop.dto.flashsale.response.CartItemFlashSaleInfo;
import com.dev.shoeshop.dto.shippingcompany.ShippingCompanyResponse;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.*;
import com.dev.shoeshop.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API Cart Controller - Handles RESTful API endpoints for cart operations
 * Provides JSON responses for AJAX calls from frontend
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiCartController {
    
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final DiscountService discountService;
    private final ShippingCompanyService shippingCompanyService;
    private final UserService userService;
    private final FlashSaleService flashSaleService;
    private final PendingPaymentService pendingPaymentService;
    
    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;
    
    @Autowired
    private PayOS payOS;

    /**
     * Get cart item count for current user
     * GET /api/cart/count
     */
    @GetMapping("/cart/count")
    public ResponseEntity<?> getCartCount(HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            
            // Nếu user chưa đăng nhập, trả về count = 0
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("count", 0);
                response.put("authenticated", false);
                return ResponseEntity.ok(response);
            }
            
            // Lấy giỏ hàng của user
            CartDTO cart = orderService.getCartByUserId(user.getId());
            int itemCount = 0;
            
            if (cart != null && cart.getCartDetails() != null) {
                itemCount = cart.getCartDetails().size();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", itemCount);
            response.put("authenticated", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error loading cart count: " + e.getMessage()));
        }
    }
    
    /**
     * Get current cart data
     * GET /api/cart/current
     */
    @GetMapping("/cart/current")
    public ResponseEntity<?> getCurrentCart(HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            // Get current cart for user
            CartDTO cart = orderService.getCartByUserId(user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", cart);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error loading cart: " + e.getMessage()));
        }
    }
    
    /**
     * Get available discounts
     * GET /api/discounts/available
     */
    @GetMapping("/discounts/available")
    public ResponseEntity<?> getAvailableDiscounts(HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            List<DiscountResponse> discounts = discountService.getAvailableDiscounts();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", discounts);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error loading discounts: " + e.getMessage()));
        }
    }
    
    /**
     * Get shipping companies
     * GET /api/shipping/companies
     */
    @GetMapping("/shipping/companies")
    public ResponseEntity<?> getShippingCompanies() {
        try {
            List<ShippingCompanyResponse> companies = shippingCompanyService.getAllActiveCompanies();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", companies);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error loading shipping companies: " + e.getMessage()));
        }
    }


    /**
     * Update item quantity
     * PUT /api/cart/update-quantity/{id}
     */
    @PutMapping("/cart/update-quantity/{id}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, 
                                          @RequestBody Map<String, Object> request,
                                          HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            Object quantityObj = request.get("quantity");
            if (quantityObj == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Quantity is required"));
            }
            
            // Convert to Long (handle both Integer and Long)
            Long quantity = Long.valueOf(quantityObj.toString());
            if (quantity < 1) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Quantity must be at least 1"));
            }
            
            orderDetailService.updateQuantity(id, quantity, user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Quantity updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Return clean message without prefix
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Edit cart item
     * PUT /api/cart/edit/{id}
     */
    @PutMapping("/cart/edit/{id}")
    public ResponseEntity<?> editCartItem(@PathVariable Long id,
                                        @RequestBody Map<String, Object> request,
                                        HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            Long quantity = Long.valueOf(request.get("quantity").toString());
            if (quantity == null || quantity < 1) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid quantity"));
            }
            
            orderDetailService.editCartItem(id, quantity, user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cart item updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error editing cart item: " + e.getMessage()));
        }
    }
    
    /**
     * Remove cart item
     * DELETE /api/cart/delete/{id}
     */
    @DeleteMapping("/cart/delete/{id}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long id, HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            orderDetailService.removeCartItem(id, user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item removed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error removing item: " + e.getMessage()));
        }
    }
    
    /**
     * Verify PayOS payment status
     * GET /api/order/verify-payos-payment?orderCode=xxx
     */
    @GetMapping("/order/verify-payos-payment")
    public ResponseEntity<?> verifyPayOSPayment(@RequestParam Long orderCode) {
        try {
            // Get payment info from PayOS
            var paymentInfo = payOS.paymentRequests().get(orderCode);

            Map<String, Object> response = new HashMap<>();

            if ("PAID".equals(paymentInfo.getStatus())) {
                response.put("success", true);
                response.put("status", "PAID");
                response.put("message", "Thanh toán thành công");
            } else if ("PENDING".equals(paymentInfo.getStatus())) {
                response.put("success", false);
                response.put("status", "PENDING");
                response.put("message", "Hệ thống chưa nhận được thanh toán của bạn. Vui lòng kiểm tra lại hoặc thử lại sau vài phút.");
            } else if ("CANCELLED".equals(paymentInfo.getStatus())) {
                response.put("success", false);
                response.put("status", "CANCELLED");
                response.put("message", "Giao dịch đã bị hủy");
            } else {
                response.put("success", false);
                response.put("status", paymentInfo.getStatus());
                response.put("message", "Trạng thái thanh toán: " + paymentInfo.getStatus());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("status", "ERROR");
            response.put("message", "Không thể xác minh trạng thái thanh toán. Vui lòng thử lại sau.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Process checkout
     * POST /api/order/pay
     */
    @PostMapping("/order/pay")
    public ResponseEntity<?> processCheckout(@RequestBody Map<String, Object> request,
                                           HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            // Extract request data
            Long cartId = request.get("cartId") != null && !request.get("cartId").toString().isEmpty() ? 
                Long.valueOf(request.get("cartId").toString()) : null;
            Double finalTotalPrice = Double.valueOf(request.get("finalTotalPrice").toString());
            String payOption = (String) request.get("payOption");
            Long addressId = Long.valueOf(request.get("addressId").toString());
            Long shippingCompanyId = request.get("shippingCompanyId") != null ? 
                Long.valueOf(request.get("shippingCompanyId").toString()) : null;
            
            // ✅ UPDATED: Get both order and shipping voucher IDs
            Long orderDiscountId = request.get("orderDiscountId") != null && !request.get("orderDiscountId").toString().isEmpty() ? 
                Long.valueOf(request.get("orderDiscountId").toString()) : null;
            Long shippingDiscountId = request.get("shippingDiscountId") != null && !request.get("shippingDiscountId").toString().isEmpty() ? 
                Long.valueOf(request.get("shippingDiscountId").toString()) : null;
            
            // 🔥 Get flash sale ID
            Long flashSaleId = request.get("flashSaleId") != null && !request.get("flashSaleId").toString().isEmpty() ?
                Long.valueOf(request.get("flashSaleId").toString()) : null;

            // ✅ Get pricing details from frontend
            Double subtotal = request.get("subtotal") != null ? Double.valueOf(request.get("subtotal").toString()) : null;
            Double shippingFee = request.get("shippingFee") != null ? Double.valueOf(request.get("shippingFee").toString()) : null;
            Double orderDiscountAmount = request.get("orderDiscountAmount") != null ? Double.valueOf(request.get("orderDiscountAmount").toString()) : 0.0;
            Double shippingDiscountAmount = request.get("shippingDiscountAmount") != null ? Double.valueOf(request.get("shippingDiscountAmount").toString()) : 0.0;
            
            // 🪙 Get loyalty points redeemed
            Integer pointsRedeemed = request.get("pointsRedeemed") != null && !request.get("pointsRedeemed").toString().isEmpty() ? 
                Integer.valueOf(request.get("pointsRedeemed").toString()) : null;
            
            // 🪙 Get DeeG Xu used
            Long coinsUsed = request.get("coinsUsed") != null && !request.get("coinsUsed").toString().isEmpty() ? 
                Long.valueOf(request.get("coinsUsed").toString()) : null;

            // Get selected item IDs (only items user selected to purchase)
            @SuppressWarnings("unchecked")
            java.util.List<Integer> selectedItemIds = request.get("selectedItemIds") != null ? 
                (java.util.List<Integer>) request.get("selectedItemIds") : null;
            
            // Get selected items with quantities (for Buy Now mode)
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> selectedItemsData = request.get("selectedItemsData") != null ? 
                (java.util.List<Map<String, Object>>) request.get("selectedItemsData") : null;
            
            // Convert to Map<ProductDetailId, Quantity> for easier access
            Map<Integer, Integer> itemQuantities = new HashMap<>();
            if (selectedItemsData != null) {
                for (Map<String, Object> item : selectedItemsData) {
                    Integer id = ((Number) item.get("id")).intValue();
                    Integer quantity = ((Number) item.get("quantity")).intValue();
                    itemQuantities.put(id, quantity);
                }
            }
            
            // Validate required fields (cartId can be null for Buy Now mode)
            if (addressId == null || payOption == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Missing required fields"));
            }
            
            // Check if this is payment confirmation (user already paid via PayOS)
            Boolean isPaymentConfirmation = request.get("isPaymentConfirmation") != null ?
                Boolean.valueOf(request.get("isPaymentConfirmation").toString()) : false;

            // ========== PAYOS PAYMENT: Redirect to PayOS, then create order after confirmation ==========
            if ("PAYOS".equals(payOption) && !isPaymentConfirmation) {
                try {
                    // Generate PayOS order code
                    long payosOrderCode = System.currentTimeMillis() / 1000;

                    // ✅ Calculate total quantity from selected items
                    int totalQuantity = itemQuantities.values().stream()
                        .mapToInt(Integer::intValue)
                        .sum();
                    
                    // ✅ Simple product name with quantity
                    String productName = String.format("Đơn hàng từ DeeG Shop x %d", totalQuantity);
                    
                    // ✅ Keep description short (max 25 chars)
                    String description = String.format("DH #%d", payosOrderCode); // Only 12-15 chars
                    
                    PaymentLinkItem item = PaymentLinkItem.builder()
                        .name(productName) // ✅ Detailed info here
                        .quantity(totalQuantity) // ✅ Use real quantity
                        .price(finalTotalPrice.longValue())
                        .build();

                    CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                        .orderCode(payosOrderCode)
                        .description(description)
                        .amount(finalTotalPrice.longValue())
                        .item(item)
                        .returnUrl(baseUrl + "/payment/return?orderCode=" + payosOrderCode)
                        .cancelUrl(baseUrl + "/cart/view")
                        .build();

                    CreatePaymentLinkResponse payosResponse = payOS.paymentRequests().create(paymentData);
                    
                    // 💾 Store pending payment data for later use
                    PendingPaymentDTO pendingPayment = PendingPaymentDTO.builder()
                        .payosOrderCode(payosOrderCode)
                        .userId(user.getId())
                        .cartId(cartId)
                        .addressId(addressId)
                        .shippingCompanyId(shippingCompanyId)
                        .orderDiscountId(orderDiscountId)
                        .shippingDiscountId(shippingDiscountId)
                        .flashSaleId(flashSaleId)
                        .finalTotalPrice(finalTotalPrice)
                        .subtotal(subtotal)
                        .shippingFee(shippingFee)
                        .orderDiscountAmount(orderDiscountAmount)
                        .shippingDiscountAmount(shippingDiscountAmount)
                        .pointsRedeemed(pointsRedeemed) // 🪙 Add points
                        .coinsUsed(coinsUsed) // 🪙 Add coins
                        .selectedItemIds(selectedItemIds)
                        .itemQuantities(itemQuantities)
                        .build();
                    
                    pendingPaymentService.storePendingPayment(payosOrderCode, pendingPayment);

                    // Get checkout URL or QR code
                    String paymentUrl = payosResponse.getCheckoutUrl();
                    if (paymentUrl == null && payosResponse.getQrCode() != null) {
                        paymentUrl = payosResponse.getQrCode();
                    }

                    OrderResultDTO orderResult = OrderResultDTO.builder()
                        .orderId(null)
                        .status("REDIRECT_TO_PAYMENT")
                        .message("Redirecting to PayOS payment page")
                        .paymentUrl(paymentUrl)
                        .payosOrderCode(payosOrderCode)  // Return orderCode to frontend
                        .build();

                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Payment link created successfully");
                    response.put("data", orderResult);

                    return ResponseEntity.ok(response);

                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Failed to create PayOS payment link: " + e.getMessage()));
                }
            }

            // ========== COD PAYMENT: Create order immediately ==========
            // Process the order with all pricing information
            OrderResultDTO orderResult = orderService.processCheckout(
                cartId, user.getId(), addressId, finalTotalPrice, 
                payOption, shippingCompanyId, orderDiscountId, shippingDiscountId, 
                flashSaleId, selectedItemIds, itemQuantities,
                subtotal, shippingFee, orderDiscountAmount, shippingDiscountAmount,
                pointsRedeemed, coinsUsed
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order processed successfully");
            response.put("data", orderResult);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error processing checkout: " + e.getMessage()));
        }
    }
    
    /**
     * Add product to cart
     * POST /api/cart/add
     */
    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request,
                                      HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            // Validate and extract request data with null checks
            if (request.get("productDetailId") == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Product detail ID is required"));
            }
            if (request.get("quantity") == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Quantity is required"));
            }
            if (request.get("pricePerUnit") == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Price per unit is required"));
            }
            
            Long productDetailId = Long.valueOf(request.get("productDetailId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            Double pricePerUnit = Double.valueOf(request.get("pricePerUnit").toString());
            
            // Call service to add to cart
            orderService.addToCart(user.getId(), productDetailId, quantity, pricePerUnit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm sản phẩm vào giỏ hàng");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error adding to cart: " + e.getMessage()));
        }
    }
    
    /**
     * Check Flash Sale for cart items
     * POST /api/cart/check-flash-sale
     * Body: {"productDetailIds": [1, 2, 3]}
     */
    @PostMapping("/cart/check-flash-sale")
    public ResponseEntity<?> checkFlashSaleForCartItems(@RequestBody Map<String, Object> request) {
        try {
            // Get productDetailIds from request
            @SuppressWarnings("unchecked")
            List<?> rawIds = (List<?>) request.get("productDetailIds");
            
            if (rawIds == null || rawIds.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Product detail IDs are required"));
            }
            
            // Convert to List<Long> (JSON numbers are parsed as Integer)
            List<Long> productDetailIds = new java.util.ArrayList<>();
            for (Object id : rawIds) {
                if (id instanceof Number) {
                    productDetailIds.add(((Number) id).longValue());
                }
            }
            
            // Get flash sale info for each product detail
            List<CartItemFlashSaleInfo> flashSaleInfoList = flashSaleService
                    .getFlashSaleInfoForCartItems(productDetailIds);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", flashSaleInfoList);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error checking flash sale: " + e.getMessage()));
        }
    }
    
    /**
     * Helper method to create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
