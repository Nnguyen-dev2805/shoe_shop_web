package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/password-reset")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetApiController {

    private final UserService userService;

    @PostMapping("/send-code")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }

            userService.sendPasswordResetCode(email);
            
            // Store email in session
            session.setAttribute("resetEmail", email);
            session.setAttribute("isEmailSent", true);
            
            response.put("success", true);
            response.put("message", "Mã xác nhận đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.");
            
            log.info("Password reset code sent to email: {}", email);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error sending password reset code", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String enteredCode = request.get("code");
            String email = (String) session.getAttribute("resetEmail");
            
            if (email == null) {
                response.put("success", false);
                response.put("message", "Phiên làm việc đã hết hạn. Vui lòng thử lại.");
                return ResponseEntity.badRequest().body(response);
            }

            if (enteredCode == null || enteredCode.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Mã xác nhận không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }

            boolean isValid = userService.verifyResetCode(email, enteredCode);
            
            if (isValid) {
                session.setAttribute("isCodeVerified", true);
                response.put("success", true);
                response.put("message", "Mã xác nhận hợp lệ. Vui lòng nhập mật khẩu mới.");
                
                log.info("Password reset code verified successfully for email: {}", email);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Mã xác nhận không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Error verifying password reset code", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra. Vui lòng thử lại.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String newPassword = request.get("newPassword");
            String confirmPassword = request.get("confirmPassword");
            String email = (String) session.getAttribute("resetEmail");
            Boolean isCodeVerified = (Boolean) session.getAttribute("isCodeVerified");
            
            if (email == null || isCodeVerified == null || !isCodeVerified) {
                response.put("success", false);
                response.put("message", "Phiên làm việc không hợp lệ. Vui lòng thử lại.");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate input
            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Mật khẩu mới không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }

            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "Mật khẩu xác nhận không khớp. Vui lòng thử lại.");
                return ResponseEntity.badRequest().body(response);
            }

            if (newPassword.length() < 6) {
                response.put("success", false);
                response.put("message", "Mật khẩu phải có ít nhất 6 ký tự.");
                return ResponseEntity.badRequest().body(response);
            }

            userService.resetPassword(email, newPassword);
            
            // Clear session
            session.removeAttribute("resetEmail");
            session.removeAttribute("isEmailSent");
            session.removeAttribute("isCodeVerified");
            
            response.put("success", true);
            response.put("message", "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập với mật khẩu mới.");
            
            log.info("Password reset successfully for email: {}", email);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error resetting password", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/session-status")
    public ResponseEntity<Map<String, Object>> getSessionStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        String email = (String) session.getAttribute("resetEmail");
        Boolean isEmailSent = (Boolean) session.getAttribute("isEmailSent");
        Boolean isCodeVerified = (Boolean) session.getAttribute("isCodeVerified");
        
        response.put("hasEmail", email != null);
        response.put("isEmailSent", isEmailSent != null && isEmailSent);
        response.put("isCodeVerified", isCodeVerified != null && isCodeVerified);
        
        return ResponseEntity.ok(response);
    }
}
