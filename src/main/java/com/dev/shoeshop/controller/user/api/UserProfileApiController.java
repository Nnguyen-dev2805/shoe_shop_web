package com.dev.shoeshop.controller.user.api;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.CloudinaryService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * API Controller for User Profile Management
 * RESTful API + Ajax + jQuery
 * Location: controller/user/api/ (User Module)
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserProfileApiController {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Update user profile information (fullname, phone)
     * POST /api/user/profile/update
     */
    @PostMapping("/profile/update")
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

            // Validate phone format only if provided (not required)
            if (phone != null && !phone.trim().isEmpty() && !phone.matches("^0\\d{9}$")) {
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
            // Phone is optional, allow null or empty
            if (phone != null && !phone.trim().isEmpty()) {
                dbUser.setPhone(phone.trim());
            } else {
                dbUser.setPhone(null);
            }
            userRepository.save(dbUser);
            
            // Update session
            session.setAttribute(Constant.SESSION_USER, dbUser);
            
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
            
            Map<String, String> userData = new HashMap<>();
            userData.put("fullname", dbUser.getFullname());
            userData.put("phone", dbUser.getPhone() != null ? dbUser.getPhone() : "");
            response.put("user", userData);
            
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
    @GetMapping("/profile")
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
    
    /**
     * Get current user profile with avatar
     * GET /api/user/profile/avatar
     */
    @GetMapping("/profile/avatar")
    public ResponseEntity<Map<String, Object>> getProfileWithAvatar(HttpSession session) {
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

            Map<String, Object> userData = new HashMap<>();
            userData.put("email", dbUser.getEmail());
            userData.put("fullname", dbUser.getFullname() != null ? dbUser.getFullname() : "");
            userData.put("phone", dbUser.getPhone() != null ? dbUser.getPhone() : "");
            userData.put("profilePicture", dbUser.getProfilePicture() != null ? dbUser.getProfilePicture() : "");
            
            response.put("success", true);
            response.put("user", userData);
            
            log.info("🔍 Profile with avatar loaded for user: {}", user.getEmail());
            log.info("📸 Profile picture value: '{}'", dbUser.getProfilePicture());
            log.info("📸 Profile picture null? {}", dbUser.getProfilePicture() == null);
            log.info("📸 Profile picture empty? {}", dbUser.getProfilePicture() != null && dbUser.getProfilePicture().isEmpty());
            log.info("📦 Response data: {}", userData);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting profile with avatar", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi tải thông tin.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Upload avatar to Cloudinary
     * POST /api/user/profile/upload-avatar
     */
    @PostMapping("/profile/upload-avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn file ảnh.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "File phải là ảnh (jpg, png, gif).");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "Kích thước ảnh tối đa 5MB.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Upload to Cloudinary
            String cloudinaryUrl = cloudinaryService.uploadImage(file, CloudinaryService.AVATAR_FOLDER);
            
            // Update user profile picture in database
            Users dbUser = userRepository.findByEmail(user.getEmail());
            if (dbUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy người dùng.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Delete old avatar from Cloudinary if exists (not Google URL)
            if (dbUser.getProfilePicture() != null && 
                !dbUser.getProfilePicture().isEmpty() && 
                dbUser.getProfilePicture().contains("cloudinary.com")) {
                try {
                    cloudinaryService.deleteImage(dbUser.getProfilePicture());
                    log.info("🗑️ Deleted old avatar from Cloudinary");
                } catch (Exception e) {
                    log.warn("Failed to delete old avatar from Cloudinary", e);
                }
            }
            
            // Update profile picture URL
            dbUser.setProfilePicture(cloudinaryUrl);
            userRepository.save(dbUser);
            
            // Update session
            session.setAttribute(Constant.SESSION_USER, dbUser);
            
            response.put("success", true);
            response.put("message", "Cập nhật ảnh đại diện thành công!");
            response.put("avatarUrl", cloudinaryUrl);
            
            log.info("✅ Avatar uploaded to Cloudinary for user: {}, URL: {}", user.getEmail(), cloudinaryUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error uploading avatar to Cloudinary", e);
            response.put("success", false);
            response.put("message", "Có lỗi khi upload ảnh. Vui lòng thử lại.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get user DeeG Xu balance
     * GET /api/user/coins/balance
     */
    @GetMapping("/coins/balance")
    public ResponseEntity<Map<String, Object>> getCoinsBalance(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Get fresh data from database to ensure up-to-date coins
            Users dbUser = userRepository.findByEmail(user.getEmail());
            if (dbUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy người dùng.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Long coins = dbUser.getCoins() != null ? dbUser.getCoins() : 0L;
            
            response.put("success", true);
            response.put("coins", coins);
            response.put("coinsFormatted", String.format("%,d xu", coins));
            
            log.info("🪙 Coins balance loaded for user {}: {} xu", user.getEmail(), coins);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error getting coins balance", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi tải số xu.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
