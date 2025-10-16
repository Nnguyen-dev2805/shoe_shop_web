package com.dev.shoeshop.controller.user.api;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.UserService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * API Controller for User Password Management
 * RESTful API + Ajax + jQuery
 * Location: controller/user/api/ (User Module)
 */
@RestController
@RequestMapping("/api/user/password")
@RequiredArgsConstructor
@Slf4j
public class UserPasswordApiController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Change password for logged-in user (NO email required)
     * POST /api/user/password/change
     */
    @PostMapping("/change")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get logged-in user from session
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để thay đổi mật khẩu.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Get request data
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            String confirmPassword = request.get("confirmPassword");

            // Validate input
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Mật khẩu hiện tại không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Mật khẩu mới không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }

            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "Mật khẩu xác nhận không khớp.");
                return ResponseEntity.badRequest().body(response);
            }

            if (newPassword.length() < 6) {
                response.put("success", false);
                response.put("message", "Mật khẩu mới phải có ít nhất 6 ký tự.");
                return ResponseEntity.badRequest().body(response);
            }

            // Verify current password
            Users dbUser = userService.findUserByEmail(user.getEmail());
            if (dbUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy người dùng.");
                return ResponseEntity.badRequest().body(response);
            }

            if (!passwordEncoder.matches(currentPassword, dbUser.getPassword())) {
                response.put("success", false);
                response.put("message", "Mật khẩu hiện tại không đúng.");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if new password is same as current
            if (passwordEncoder.matches(newPassword, dbUser.getPassword())) {
                response.put("success", false);
                response.put("message", "Mật khẩu mới trùng với mật khẩu hiện tại.");
                return ResponseEntity.badRequest().body(response);
            }

            // Change password
            userService.changePassword(user.getEmail(), newPassword);
            
            response.put("success", true);
            response.put("message", "Đổi mật khẩu thành công!");
            
            log.info("Password changed successfully for user: {}", user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error changing password", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi đổi mật khẩu. Vui lòng thử lại.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
