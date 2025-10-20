package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.OrderDetail;
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

}
