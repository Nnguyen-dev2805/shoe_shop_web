package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.RegisterRequestDTO;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
@Slf4j
public class RegistrationApiController {

    private final UserService userService;

    /**
     * API gửi mã xác nhận đến email
     */
    @PostMapping("/send-verification-code")
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

            // Kiểm tra email đã tồn tại chưa
            Users existingUser = userService.findUserByEmail(email);
            if (existingUser != null) {
                response.put("success", false);
                response.put("message", "Email đã được đăng ký. Vui lòng sử dụng email khác.");
                return ResponseEntity.badRequest().body(response);
            }

            // Gửi mã xác nhận (sử dụng lại hệ thống reset password)
            String verificationCode = userService.generateVerificationCode();
            userService.sendRegistrationVerificationCode(email, verificationCode);
            
            // Lưu email vào session
            session.setAttribute("registerEmail", email);
            session.setAttribute("isRegisterCodeSent", true);
            
            response.put("success", true);
            response.put("message", "Mã xác nhận đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.");
            
            log.info("Registration verification code sent to email: {}", email);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error sending registration verification code", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi gửi mã xác nhận. Vui lòng thử lại.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * API đăng ký tài khoản mới
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> registerUser(
            @Valid @RequestBody RegisterRequestDTO registerRequest,
            BindingResult bindingResult,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra validation errors
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getAllErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.joining(", "));
                
                response.put("success", false);
                response.put("message", errorMessage);
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra email trong session
            String sessionEmail = (String) session.getAttribute("registerEmail");
            Boolean isCodeSent = (Boolean) session.getAttribute("isRegisterCodeSent");
            
            if (sessionEmail == null || isCodeSent == null || !isCodeSent) {
                response.put("success", false);
                response.put("message", "Vui lòng gửi mã xác nhận trước khi đăng ký.");
                return ResponseEntity.badRequest().body(response);
            }

            if (!sessionEmail.equals(registerRequest.getEmail())) {
                response.put("success", false);
                response.put("message", "Email không khớp với email đã gửi mã xác nhận.");
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra mật khẩu khớp
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                response.put("success", false);
                response.put("message", "Mật khẩu xác nhận không khớp.");
                return ResponseEntity.badRequest().body(response);
            }

            // Verify mã xác nhận
            boolean isCodeValid = userService.verifyResetCode(
                    registerRequest.getEmail(), 
                    registerRequest.getVerificationCode()
            );
            
            if (!isCodeValid) {
                response.put("success", false);
                response.put("message", "Mã xác nhận không hợp lệ hoặc đã hết hạn.");
                return ResponseEntity.badRequest().body(response);
            }

            // Đăng ký user
            userService.registerNewUser(
                    registerRequest.getEmail(),
                    registerRequest.getFullname(),
                    registerRequest.getPassword()
            );
            
            // Xóa session
            session.removeAttribute("registerEmail");
            session.removeAttribute("isRegisterCodeSent");
            
            response.put("success", true);
            response.put("message", "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
            response.put("redirectUrl", "/login");
            
            log.info("User registered successfully: {}", registerRequest.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error registering user", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
