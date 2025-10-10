package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;

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

}
