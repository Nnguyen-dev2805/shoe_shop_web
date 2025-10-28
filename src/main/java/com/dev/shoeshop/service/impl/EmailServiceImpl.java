package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.ReturnRequest;
import com.dev.shoeshop.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendPasswordResetEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject("Mã xác nhận đặt lại mật khẩu - DeeG Shoe Shop");
            
            // Render HTML template với Thymeleaf
            Context context = new Context();
            context.setVariable("verificationCode", verificationCode);
            String htmlContent = templateEngine.process("email/password-reset", context);
            helper.setText(htmlContent, true);
            
            // Set sender with proper encoding handling
            helper.setFrom("noreply@deegshoeshop.com", "DeeG Shoe Shop");

            // Attach logo
            try {
                ClassPathResource logoResource = new ClassPathResource("static/img/logo-1.png");
                if (logoResource.exists()) {
                    helper.addInline("logo", logoResource);
                    log.info("Logo attached successfully to email");
                } else {
                    log.warn("Logo file not found at: static/img/logo-1.png");
                }
            } catch (Exception logoException) {
                log.warn("Failed to attach logo to email: {}", logoException.getMessage());
            }

            mailSender.send(mimeMessage);
            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }

    @Override
    public void sendRegistrationVerificationEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject("Mã xác nhận đăng ký tài khoản - DeeG Shoe Shop");
            
            // Render HTML template với Thymeleaf
            Context context = new Context();
            context.setVariable("verificationCode", verificationCode);
            String htmlContent = templateEngine.process("email/registration-verification", context);
            helper.setText(htmlContent, true);
            
            // Set sender with proper encoding handling
            helper.setFrom("noreply@deegshoeshop.com", "DeeG Shoe Shop");

            // Attach logo
            try {
                ClassPathResource logoResource = new ClassPathResource("static/img/logo-1.png");
                if (logoResource.exists()) {
                    helper.addInline("logo", logoResource);
                    log.info("Logo attached successfully to registration email");
                } else {
                    log.warn("Logo file not found at: static/img/logo-1.png");
                }
            } catch (Exception logoException) {
                log.warn("Failed to attach logo to registration email: {}", logoException.getMessage());
                // Continue sending email without logo
            }

            mailSender.send(mimeMessage);
            log.info("Registration verification email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send registration verification email to: {}", toEmail, e);
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }

    @Override
    @Async
    public void sendOrderShippedEmail(Order order) {
        try {
            log.info("🚀 Preparing to send order shipped email to: {}", order.getUser().getEmail());
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("📦 Đơn hàng #" + order.getId() + " đang được giao đến bạn!");
            
            // Render HTML template với Thymeleaf
            Context context = new Context();
            context.setVariable("order", order);
            context.setVariable("customerName", order.getUser().getFullname());
            context.setVariable("orderId", order.getId());
            context.setVariable("orderDate", order.getCreatedDate());
            context.setVariable("totalPrice", formatCurrency(order.getTotalPrice()));
            context.setVariable("paymentMethod", order.getPayOption().toString());
            context.setVariable("deliveryAddress", buildAddressString(order.getAddress()));
            
            // Calculate total items
            int totalItems = 0;
            if (order.getOrderDetailSet() != null) {
                for (OrderDetail detail : order.getOrderDetailSet()) {
                    totalItems += detail.getQuantity();
                }
            }
            context.setVariable("totalItems", totalItems);
            
            String htmlContent = templateEngine.process("email/order-shipped", context);
            helper.setText(htmlContent, true);
            
            // Set sender
            helper.setFrom("noreply@deegshoeshop.com", "DeeG Shoe Shop");
            
            // Attach logo
            try {
                ClassPathResource logoResource = new ClassPathResource("static/img/logo-1.png");
                if (logoResource.exists()) {
                    helper.addInline("logo", logoResource);
                    log.info("Logo attached successfully to order shipped email");
                }
            } catch (Exception logoException) {
                log.warn("Failed to attach logo: {}", logoException.getMessage());
            }
            
            mailSender.send(mimeMessage);
            log.info("✅ Order shipped email sent successfully to: {}", order.getUser().getEmail());
            
        } catch (Exception e) {
            log.error("❌ Failed to send order shipped email for order #{}: {}", order.getId(), e.getMessage(), e);
            // Don't throw exception - email failure shouldn't break order processing
        }
    }
    
    /**
     * Format currency to VND format
     */
    private String formatCurrency(Double amount) {
        if (amount == null) return "0₫";
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "₫";
    }
    
    /**
     * Build address string from Address entity
     */
    private String buildAddressString(com.dev.shoeshop.entity.Address address) {
        if (address == null) return "N/A";
        
        StringBuilder sb = new StringBuilder();
        if (address.getAddress_line() != null) {
            sb.append(address.getAddress_line());
        }
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(address.getCity());
        }
        if (address.getCountry() != null && !address.getCountry().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(address.getCountry());
        }
        
        return sb.length() > 0 ? sb.toString() : "N/A";
    }
    
    @Override
    @Async
    public void sendReturnApprovedEmail(ReturnRequest returnRequest) {
        try {
            log.info("📧 Preparing to send return approved email for return request #{}", returnRequest.getId());
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(returnRequest.getUser().getEmail());
            helper.setSubject("✅ Yêu cầu trả hàng #" + returnRequest.getId() + " đã được chấp nhận");
            
            // Get product name
            String productName = "Sản phẩm";
            if (returnRequest.getOrder() != null && 
                returnRequest.getOrder().getOrderDetailSet() != null && 
                !returnRequest.getOrder().getOrderDetailSet().isEmpty()) {
                OrderDetail firstDetail = returnRequest.getOrder().getOrderDetailSet().iterator().next();
                if (firstDetail != null && firstDetail.getProduct() != null && 
                    firstDetail.getProduct().getProduct() != null) {
                    Product product = firstDetail.getProduct().getProduct();
                    productName = product.getTitle() != null ? product.getTitle() : "Sản phẩm";
                }
            }
            
            // Map return reason to Vietnamese
            String returnReason = mapReturnReasonToVietnamese(returnRequest.getReason().name());
            
            // Build user address
            String userAddress = returnRequest.getUser().getEmail(); // Fallback
            if (returnRequest.getOrder() != null && returnRequest.getOrder().getAddress() != null) {
                userAddress = buildAddressString(returnRequest.getOrder().getAddress());
            }
            
            // Render HTML template với Thymeleaf
            Context context = new Context();
            context.setVariable("userName", returnRequest.getUser().getFullname());
            context.setVariable("returnRequestId", returnRequest.getId());
            context.setVariable("orderId", returnRequest.getOrder().getId());
            context.setVariable("productName", productName);
            context.setVariable("refundAmount", returnRequest.getOrder().getTotalPrice());
            context.setVariable("returnReason", returnReason);
            context.setVariable("userPhone", returnRequest.getUser().getPhone());
            context.setVariable("userAddress", userAddress);
            context.setVariable("trackingUrl", "http://localhost:8081/user/order-view");
            
            String htmlContent = templateEngine.process("email/return-approved", context);
            helper.setText(htmlContent, true);
            
            // Set sender
            helper.setFrom("noreply@deegshoeshop.com", "DeeG Shoe Shop");
            
            // Attach logo
            try {
                ClassPathResource logoResource = new ClassPathResource("static/img/logo-1.png");
                if (logoResource.exists()) {
                    helper.addInline("logo", logoResource);
                }
            } catch (Exception logoException) {
                log.warn("Failed to attach logo: {}", logoException.getMessage());
            }
            
            mailSender.send(mimeMessage);
            log.info("✅ Return approved email sent successfully to: {}", returnRequest.getUser().getEmail());
            
        } catch (Exception e) {
            log.error("❌ Failed to send return approved email for return request #{}: {}", 
                      returnRequest.getId(), e.getMessage(), e);
            // Don't throw exception - email failure shouldn't break approval process
        }
    }
    
    /**
     * Map return reason enum to Vietnamese
     */
    private String mapReturnReasonToVietnamese(String reason) {
        return switch (reason) {
            case "WRONG_SIZE" -> "Size không đúng";
            case "DAMAGED" -> "Hàng bị hỏng";
            case "WRONG_PRODUCT" -> "Sai sản phẩm";
            case "NOT_AS_DESCRIBED" -> "Không giống mô tả";
            case "QUALITY_ISSUE" -> "Vấn đề chất lượng";
            case "CHANGE_MIND" -> "Đổi ý";
            case "OTHER" -> "Lý do khác";
            default -> reason;
        };
    }
    
    @Override
    @Async
    public void sendRefundCompletedEmail(ReturnRequest returnRequest) {
        try {
            log.info("💰 Preparing to send refund completed email for return request #{}", returnRequest.getId());
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(returnRequest.getUser().getEmail());
            helper.setSubject("💰 Hoàn xu thành công cho đơn trả hàng #" + returnRequest.getId());
            
            // Get product name
            String productName = "Sản phẩm";
            if (returnRequest.getOrder() != null && 
                returnRequest.getOrder().getOrderDetailSet() != null && 
                !returnRequest.getOrder().getOrderDetailSet().isEmpty()) {
                OrderDetail firstDetail = returnRequest.getOrder().getOrderDetailSet().iterator().next();
                if (firstDetail != null && firstDetail.getProduct() != null && 
                    firstDetail.getProduct().getProduct() != null) {
                    Product product = firstDetail.getProduct().getProduct();
                    productName = product.getTitle() != null ? product.getTitle() : "Sản phẩm";
                }
            }
            
            // Get user's new balance
            Long newBalance = returnRequest.getUser().getCoins() != null ? returnRequest.getUser().getCoins() : 0L;
            
            // Render HTML template với Thymeleaf
            Context context = new Context();
            context.setVariable("userName", returnRequest.getUser().getFullname());
            context.setVariable("returnRequestId", returnRequest.getId());
            context.setVariable("orderId", returnRequest.getOrder().getId());
            context.setVariable("productName", productName);
            context.setVariable("refundAmount", formatCurrency(returnRequest.getRefundAmount().doubleValue()));
            context.setVariable("refundCoins", returnRequest.getRefundAmount().longValue());
            context.setVariable("newBalance", newBalance);
            context.setVariable("newBalanceFormatted", formatCurrency(newBalance.doubleValue()));
            context.setVariable("completedDate", returnRequest.getCompletedDate());
            context.setVariable("accountUrl", "http://localhost:8081/user/my_account");
            
            String htmlContent = templateEngine.process("email/refund-completed", context);
            helper.setText(htmlContent, true);
            
            // Set sender
            helper.setFrom("noreply@deegshoeshop.com", "DeeG Shoe Shop");
            
            // Attach logo
            try {
                ClassPathResource logoResource = new ClassPathResource("static/img/logo-1.png");
                if (logoResource.exists()) {
                    helper.addInline("logo", logoResource);
                }
            } catch (Exception logoException) {
                log.warn("Failed to attach logo: {}", logoException.getMessage());
            }
            
            mailSender.send(mimeMessage);
            log.info("✅ Refund completed email sent successfully to: {}", returnRequest.getUser().getEmail());
            
        } catch (Exception e) {
            log.error("❌ Failed to send refund completed email for return request #{}: {}", 
                      returnRequest.getId(), e.getMessage(), e);
            // Don't throw exception - email failure shouldn't break refund process
        }
    }
    
    @Override
    @Async
    public void sendReturnRejectedEmail(ReturnRequest returnRequest) {
        try {
            log.info("❌ Preparing to send return rejected email for return request #{}", returnRequest.getId());
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(returnRequest.getUser().getEmail());
            helper.setSubject("❌ Yêu cầu trả hàng #" + returnRequest.getId() + " đã bị từ chối");
            
            // Get product name
            String productName = "Sản phẩm";
            if (returnRequest.getOrder() != null && 
                returnRequest.getOrder().getOrderDetailSet() != null && 
                !returnRequest.getOrder().getOrderDetailSet().isEmpty()) {
                OrderDetail firstDetail = returnRequest.getOrder().getOrderDetailSet().iterator().next();
                if (firstDetail != null && firstDetail.getProduct() != null && 
                    firstDetail.getProduct().getProduct() != null) {
                    Product product = firstDetail.getProduct().getProduct();
                    productName = product.getTitle() != null ? product.getTitle() : "Sản phẩm";
                }
            }
            
            // Map return reason to Vietnamese
            String returnReason = mapReturnReasonToVietnamese(returnRequest.getReason().name());
            
            // Render HTML template với Thymeleaf
            Context context = new Context();
            context.setVariable("userName", returnRequest.getUser().getFullname());
            context.setVariable("returnRequestId", returnRequest.getId());
            context.setVariable("orderId", returnRequest.getOrder().getId());
            context.setVariable("productName", productName);
            context.setVariable("returnReason", returnReason);
            context.setVariable("adminNote", returnRequest.getAdminNote() != null ? returnRequest.getAdminNote() : "Không có ghi chú");
            context.setVariable("supportEmail", "support@deegshoeshop.com");
            context.setVariable("supportPhone", "1900-xxxx");
            context.setVariable("contactUrl", "http://localhost:8081/user/order-view");
            
            String htmlContent = templateEngine.process("email/return-rejected", context);
            helper.setText(htmlContent, true);
            
            // Set sender
            helper.setFrom("noreply@deegshoeshop.com", "DeeG Shoe Shop");
            
            // Attach logo
            try {
                ClassPathResource logoResource = new ClassPathResource("static/img/logo-1.png");
                if (logoResource.exists()) {
                    helper.addInline("logo", logoResource);
                }
            } catch (Exception logoException) {
                log.warn("Failed to attach logo: {}", logoException.getMessage());
            }
            
            mailSender.send(mimeMessage);
            log.info("✅ Return rejected email sent successfully to: {}", returnRequest.getUser().getEmail());
            
        } catch (Exception e) {
            log.error("❌ Failed to send return rejected email for return request #{}: {}", 
                      returnRequest.getId(), e.getMessage(), e);
            // Don't throw exception - email failure shouldn't break rejection process
        }
    }

}
