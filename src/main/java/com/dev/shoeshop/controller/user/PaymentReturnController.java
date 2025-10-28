package com.dev.shoeshop.controller.user;

import com.dev.shoeshop.dto.OrderResultDTO;
import com.dev.shoeshop.dto.PendingPaymentDTO;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.PendingPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

/**
 * Controller to handle PayOS payment return
 * Creates Order when user returns from PayOS payment page
 */
@Controller
@RequiredArgsConstructor
public class PaymentReturnController {
    
    private final PendingPaymentService pendingPaymentService;
    private final OrderService orderService;
    
    /**
     * Handle return from PayOS payment
     * This is called when user completes payment and is redirected back
     */
    @GetMapping("/payment/return")
    public String handlePaymentReturn(
            @RequestParam(required = false) Long orderCode,
            @RequestParam(required = false) String status,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("=== PAYMENT RETURN HANDLER ===");
        System.out.println("Order Code: " + orderCode);
        System.out.println("Status: " + status);
        
        try {
            // Check if payment was successful
            if (orderCode == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin thanh toán");
                return "redirect:/cart/view";
            }
            
            // Retrieve pending payment data
            PendingPaymentDTO pendingPayment = pendingPaymentService.getPendingPayment(orderCode);
            
            if (pendingPayment == null) {
                System.err.println("⚠️ No pending payment found for order code: " + orderCode);
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đơn hàng");
                return "redirect:/cart/view";
            }
            
            System.out.println("✅ Found pending payment for user: " + pendingPayment.getUserId());
            
            // Create Order now
            OrderResultDTO orderResult = orderService.processCheckout(
                pendingPayment.getCartId(),
                pendingPayment.getUserId(),
                pendingPayment.getAddressId(),
                pendingPayment.getFinalTotalPrice(),
                "PAYOS",
                pendingPayment.getShippingCompanyId(),
                pendingPayment.getOrderDiscountId(),
                pendingPayment.getShippingDiscountId(),
                pendingPayment.getFlashSaleId(),
                pendingPayment.getSelectedItemIds(),
                pendingPayment.getItemQuantities(),
                pendingPayment.getSubtotal(),
                pendingPayment.getShippingFee(),
                pendingPayment.getOrderDiscountAmount(),
                pendingPayment.getShippingDiscountAmount(),
                pendingPayment.getPointsRedeemed(), // 🪙 Add points
                pendingPayment.getCoinsUsed() // 🪙 Add coins
            );
            
            System.out.println("✅ Order created successfully with ID: " + orderResult.getOrderId());
            
            // Update PayOS payment info
            orderService.updatePayOSPaymentInfo(
                orderResult.getOrderId(),
                orderCode,
                "PAID",
                new Date()
            );
            
            System.out.println("✅ Order payment status updated to PAID");
            
            // Remove pending payment
            pendingPaymentService.removePendingPayment(orderCode);
            
            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công! Đơn hàng #" + orderResult.getOrderId() + " đã được tạo.");
            return "redirect:/user/order/view";
            
        } catch (Exception e) {
            System.err.println("❌ Failed to create order: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo đơn hàng: " + e.getMessage());
            return "redirect:/cart/view";
        }
    }
}
