package com.dev.shoeshop.controller.user.api;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * API Controller for User Profile Management
 * RESTful API + Ajax + jQuery
 * Location: controller/user/api/ (User Module)
 */
@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
@Slf4j
public class UserProfileApiController {

    private final UserRepository userRepository;

    /**
     * Update user profile information (fullname, phone)
     * POST /api/user/profile/update
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get logged-in user from session
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để cập nhật thông tin.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Get request data
            String fullname = request.get("fullname");
            String phone = request.get("phone");

            // Validate input
            if (fullname == null || fullname.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Họ tên không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }

            if (phone == null || phone.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Số điện thoại không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate phone format (10 digits, start with 0)
            if (!phone.matches("^0\\d{9}$")) {
                response.put("success", false);
                response.put("message", "Số điện thoại phải có 10 số và bắt đầu bằng 0.");
                return ResponseEntity.badRequest().body(response);
            }

            // Get user from database
            Users dbUser = userRepository.findByEmail(user.getEmail());
            if (dbUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy người dùng.");
                return ResponseEntity.badRequest().body(response);
            }

            // Update user information
            dbUser.setFullname(fullname.trim());
            dbUser.setPhone(phone.trim());
            userRepository.save(dbUser);
            
            // Update session
            session.setAttribute(Constant.SESSION_USER, dbUser);
            
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
            response.put("user", Map.of(
                "fullname", dbUser.getFullname(),
                "phone", dbUser.getPhone()
            ));
            
            log.info("Profile updated successfully for user: {}", user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error updating profile", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi cập nhật thông tin. Vui lòng thử lại.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get current user profile information
     * GET /api/user/profile
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfile(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Get fresh data from database
            Users dbUser = userRepository.findByEmail(user.getEmail());
            if (dbUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy người dùng.");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("user", Map.of(
                "email", dbUser.getEmail(),
                "fullname", dbUser.getFullname() != null ? dbUser.getFullname() : "",
                "phone", dbUser.getPhone() != null ? dbUser.getPhone() : ""
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting profile", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi tải thông tin.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
