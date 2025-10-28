package com.dev.shoeshop.controller.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.dev.shoeshop.controller.payment.type.ApiResponse;
import com.dev.shoeshop.dto.OrderResultDTO;
import com.dev.shoeshop.dto.PendingPaymentDTO;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.PendingPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.payos.PayOS;
import vn.payos.model.webhooks.*;

import java.util.Date;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
  private final PayOS payOS;
  private final PendingPaymentService pendingPaymentService;
  private final OrderService orderService;

  /**
   * PayOS Webhook Handler
   * Called by PayOS when payment status changes
   * Creates Order ONLY after payment is confirmed
   */
  @PostMapping(path = "/payos_transfer_handler")
  public ApiResponse<WebhookData> payosTransferHandler(@RequestBody Object body)
      throws JsonProcessingException, IllegalArgumentException {
    try {
      System.out.println("=== PAYOS WEBHOOK RECEIVED ===");
      
      // 1. Verify webhook signature
      WebhookData webhookData = payOS.webhooks().verify(body);
      System.out.println("✅ Webhook signature verified");
      System.out.println("Webhook data: " + webhookData);
      
      // 2. Check payment status
      String code = webhookData.getCode();
      if ("00".equals(code)) { // "00" = Payment successful
        System.out.println("✅ Payment confirmed by PayOS");
        
        // 3. Get order code from webhook
        // Parse orderCode from webhook - PayOS SDK may have different structure
        Long payosOrderCode = null;
        try {
          // WebhookData typically has: code, desc, data, signature
          // The data field contains: orderCode, amount, description, etc.
          // We'll parse it from the toString or use reflection
          String webhookStr = webhookData.toString();
          System.out.println("Webhook toString: " + webhookStr);
          
          // Try to extract orderCode from string representation
          // Format might be: "orderCode=123456789"
          if (webhookStr.contains("orderCode=")) {
            int startIndex = webhookStr.indexOf("orderCode=") + 10;
            int endIndex = webhookStr.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = webhookStr.indexOf(")", startIndex);
            if (endIndex == -1) endIndex = webhookStr.indexOf("}", startIndex);
            
            String orderCodeStr = webhookStr.substring(startIndex, endIndex).trim();
            payosOrderCode = Long.parseLong(orderCodeStr);
            System.out.println("Extracted orderCode from string: " + payosOrderCode);
          }
        } catch (Exception e) {
          System.err.println("⚠️ Could not extract orderCode from webhook: " + e.getMessage());
          e.printStackTrace();
        }
        
        if (payosOrderCode == null) {
          System.err.println("❌ OrderCode is null in webhook data");
          System.err.println("Full webhook object: " + webhookData);
          return ApiResponse.error("OrderCode not found in webhook");
        }
        
        System.out.println("PayOS Order Code: " + payosOrderCode);
        
        // 4. Retrieve pending payment data
        PendingPaymentDTO pendingPayment = pendingPaymentService.getPendingPayment(payosOrderCode);
        
        if (pendingPayment == null) {
          System.err.println("⚠️ No pending payment found for order code: " + payosOrderCode);
          return ApiResponse.error("Pending payment not found");
        }
        
        System.out.println("✅ Found pending payment for user: " + pendingPayment.getUserId());
        
        // 5. Create Order now that payment is confirmed
        try {
          OrderResultDTO orderResult = orderService.processCheckout(
              pendingPayment.getCartId(),
              pendingPayment.getUserId(),
              pendingPayment.getAddressId(),
              pendingPayment.getFinalTotalPrice(),
              "PAYOS", // Payment option
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
          
          // 6. Update order with PayOS tracking info
          orderService.updatePayOSPaymentInfo(
              orderResult.getOrderId(),
              payosOrderCode,
              "PAID",
              new Date()
          );
          
          System.out.println("✅ Order payment status updated to PAID");
          
          // 7. Remove pending payment
          pendingPaymentService.removePendingPayment(payosOrderCode);
          
          return ApiResponse.success("Order created successfully", webhookData);
          
        } catch (Exception e) {
          System.err.println("❌ Failed to create order: " + e.getMessage());
          e.printStackTrace();
          return ApiResponse.error("Failed to create order: " + e.getMessage());
        }
        
      } else {
        System.out.println("⚠️ Payment not successful. Code: " + code);
        return ApiResponse.success("Webhook received but payment not successful", webhookData);
      }
      
    } catch (Exception e) {
      System.err.println("❌ Webhook processing error: " + e.getMessage());
      e.printStackTrace();
      return ApiResponse.error(e.getMessage());
    }
  }
}
