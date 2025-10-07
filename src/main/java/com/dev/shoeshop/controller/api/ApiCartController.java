package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.AddressDTO;
import com.dev.shoeshop.dto.CartDTO;
import com.dev.shoeshop.dto.OrderResultDTO;
import com.dev.shoeshop.dto.discount.DiscountResponse;
import com.dev.shoeshop.dto.shippingcompany.ShippingCompanyResponse;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.*;
import com.dev.shoeshop.utils.Constant;
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
    
    /**
     * Get current cart data
     * GET /api/cart/current
     */
    @GetMapping("/cart/current")
    public ResponseEntity<?> getCurrentCart(HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                System.out.println("User not authenticated in session");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            System.out.println("User found in session: " + user.getId() + " - " + user.getEmail());
            
            // Get current cart for user
            CartDTO cart = orderService.getCartByUserId(user.getId());
            System.out.println("Cart loaded successfully for user: " + user.getId());
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
            System.out.println("Available discounts: " + discounts);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", discounts);
            System.out.println("HOangha");
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
            System.out.println("HOangha");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error loading shipping companies: " + e.getMessage()));
        }
    }
    
    /**
     * Get user addresses
     * GET /api/user/addresses
     */
    @GetMapping("/user/addresses")
    public ResponseEntity<?> getUserAddresses(HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            List<AddressDTO> addresses = userService.getUserAddresses(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", addresses);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error loading addresses: " + e.getMessage()));
        }
    }
    
    /**
     * Update item quantity
     * PUT /api/cart/update-quantity/{id}
     */
    @PutMapping("/cart/update-quantity/{id}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, 
                                          @RequestBody Map<String, Long> request,
                                          HttpSession session) {
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            Long quantity = request.get("quantity");
            if (quantity == null || quantity < 1) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid quantity"));
            }
            
            orderDetailService.updateQuantity(id, quantity, user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Quantity updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error updating quantity: " + e.getMessage()));
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
            Long cartId = Long.valueOf(request.get("cartId").toString());
            Double finalTotalPrice = Double.valueOf(request.get("finalTotalPrice").toString());
            String payOption = (String) request.get("payOption");
            Long addressId = Long.valueOf(request.get("addressId").toString());
            Long shippingCompanyId = null; // Shipping company removed from cart
            Long discountId = request.get("discountId") != null ? 
                Long.valueOf(request.get("discountId").toString()) : null;
            
            // Validate required fields
            if (cartId == null || addressId == null || payOption == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Missing required fields"));
            }
            
            // Process the order
            OrderResultDTO orderResult = orderService.processCheckout(
                cartId, user.getId(), addressId, finalTotalPrice, 
                payOption, shippingCompanyId, discountId
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order processed successfully");
            response.put("orderId", orderResult.getOrderId());
            
            // Handle payment redirection for VNPay
            if ("VNPAY".equals(payOption) && orderResult.getPaymentUrl() != null) {
                response.put("redirectUrl", orderResult.getPaymentUrl());
            }
            
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
            
            // Extract request data
            Long productDetailId = Long.valueOf(request.get("productDetailId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            Double pricePerUnit = Double.valueOf(request.get("pricePerUnit").toString());
            
            System.out.println("=== Add to cart request ===");
            System.out.println("User ID: " + user.getId());
            System.out.println("Product Detail ID: " + productDetailId);
            System.out.println("Quantity: " + quantity);
            System.out.println("Price Per Unit: " + pricePerUnit);
            
            // Call service to add to cart
            orderService.addToCart(user.getId(), productDetailId, quantity, pricePerUnit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm sản phẩm vào giỏ hàng");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error adding to cart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error adding to cart: " + e.getMessage()));
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
