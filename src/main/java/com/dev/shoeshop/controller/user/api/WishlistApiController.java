package com.dev.shoeshop.controller.user.api;

import com.dev.shoeshop.dto.WishlistResponseDTO;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.WishlistService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller cho Wishlist
 * Quản lý các thao tác với danh sách yêu thích
 * RESTful API + Ajax + jQuery
 * Location: controller/user/api/ (User Module)
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Slf4j
public class WishlistApiController {

    private final WishlistService wishlistService;

    /**
     * Toggle wishlist - Thêm hoặc xóa product khỏi wishlist
     * POST /api/wishlist/toggle
     * 
     * @param productId ID của product
     * @param session HTTP Session
     * @return ResponseEntity<WishlistResponseDTO>
     */
    @PostMapping("/toggle")
    public ResponseEntity<WishlistResponseDTO> toggleWishlist(
            @RequestParam Long productId,
            HttpSession session) {
        
        log.info("=== API: Toggle wishlist for productId: {} ===", productId);
        
        try {
            // Lấy user từ session
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                log.warn("User not logged in");
                WishlistResponseDTO response = WishlistResponseDTO.builder()
                        .success(false)
                        .message("Bạn cần đăng nhập để sử dụng chức năng này")
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            log.info("User {} toggling wishlist for product {}", user.getId(), productId);
            WishlistResponseDTO response = wishlistService.toggleWishlist(user.getId(), productId);
            
            log.info("Toggle result - success: {}, isInWishlist: {}, count: {}", 
                    response.isSuccess(), response.getIsInWishlist(), response.getCount());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in toggleWishlist API: ", e);
            WishlistResponseDTO response = WishlistResponseDTO.builder()
                    .success(false)
                    .message("Có lỗi xảy ra: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Thêm product vào wishlist
     * POST /api/wishlist/add
     * 
     * @param productId ID của product
     * @param session HTTP Session
     * @return ResponseEntity<WishlistResponseDTO>
     */
    @PostMapping("/add")
    public ResponseEntity<WishlistResponseDTO> addToWishlist(
            @RequestParam Long productId,
            HttpSession session) {
        
        log.info("=== API: Add to wishlist - productId: {} ===", productId);
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                log.warn("User not logged in");
                WishlistResponseDTO response = WishlistResponseDTO.builder()
                        .success(false)
                        .message("Bạn cần đăng nhập để sử dụng chức năng này")
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            log.info("User {} adding product {} to wishlist", user.getId(), productId);
            WishlistResponseDTO response = wishlistService.addToWishlist(user.getId(), productId);
            
            log.info("Add result - success: {}, count: {}", response.isSuccess(), response.getCount());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in addToWishlist API: ", e);
            WishlistResponseDTO response = WishlistResponseDTO.builder()
                    .success(false)
                    .message("Có lỗi xảy ra: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Xóa product khỏi wishlist
     * DELETE /api/wishlist/remove
     * 
     * @param productId ID của product
     * @param session HTTP Session
     * @return ResponseEntity<WishlistResponseDTO>
     */
    @DeleteMapping("/remove")
    public ResponseEntity<WishlistResponseDTO> removeFromWishlist(
            @RequestParam Long productId,
            HttpSession session) {
        
        log.info("=== API: Remove from wishlist - productId: {} ===", productId);
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                log.warn("User not logged in");
                WishlistResponseDTO response = WishlistResponseDTO.builder()
                        .success(false)
                        .message("Bạn cần đăng nhập để sử dụng chức năng này")
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            log.info("User {} removing product {} from wishlist", user.getId(), productId);
            WishlistResponseDTO response = wishlistService.removeFromWishlist(user.getId(), productId);
            
            log.info("Remove result - success: {}, count: {}", response.isSuccess(), response.getCount());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in removeFromWishlist API: ", e);
            WishlistResponseDTO response = WishlistResponseDTO.builder()
                    .success(false)
                    .message("Có lỗi xảy ra: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Lấy danh sách wishlist của user
     * GET /api/wishlist
     * 
     * @param session HTTP Session
     * @return ResponseEntity<Map>
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getWishlist(HttpSession session) {
        log.info("=== API: Get wishlist ===");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                log.warn("User not logged in");
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để xem danh sách yêu thích");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            log.info("Getting wishlist for user {}", user.getId());
            List<Map<String, Object>> wishlist = wishlistService.getUserWishlist(user.getId());
            Long count = wishlistService.countWishlistItems(user.getId());
            
            response.put("success", true);
            response.put("wishlist", wishlist);
            response.put("count", count);
            
            log.info("Wishlist retrieved - {} items", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in getWishlist API: ", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi tải wishlist");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Đếm số lượng items trong wishlist
     * GET /api/wishlist/count
     * 
     * @param session HTTP Session
     * @return ResponseEntity<Map>
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> countWishlist(HttpSession session) {
        log.debug("=== API: Count wishlist ===");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", true);
                response.put("count", 0L);
                return ResponseEntity.ok(response);
            }
            
            Long count = wishlistService.countWishlistItems(user.getId());
            
            response.put("success", true);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in countWishlist API: ", e);
            response.put("success", false);
            response.put("count", 0L);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Kiểm tra product có trong wishlist không
     * GET /api/wishlist/check/{productId}
     * 
     * @param productId ID của product
     * @param session HTTP Session
     * @return ResponseEntity<Map>
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Object>> checkWishlist(
            @PathVariable Long productId,
            HttpSession session) {
        
        log.debug("=== API: Check wishlist for productId: {} ===", productId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", true);
                response.put("isInWishlist", false);
                return ResponseEntity.ok(response);
            }
            
            boolean isInWishlist = wishlistService.isInWishlist(user.getId(), productId);
            
            response.put("success", true);
            response.put("isInWishlist", isInWishlist);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in checkWishlist API: ", e);
            response.put("success", false);
            response.put("isInWishlist", false);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Lấy danh sách product IDs trong wishlist
     * GET /api/wishlist/product-ids
     * 
     * @param session HTTP Session
     * @return ResponseEntity<Map>
     */
    @GetMapping("/product-ids")
    public ResponseEntity<Map<String, Object>> getWishlistProductIds(HttpSession session) {
        log.debug("=== API: Get wishlist product IDs ===");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", true);
                response.put("productIds", List.of());
                return ResponseEntity.ok(response);
            }
            
            List<Long> productIds = wishlistService.getWishlistProductIds(user.getId());
            
            response.put("success", true);
            response.put("productIds", productIds);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in getWishlistProductIds API: ", e);
            response.put("success", false);
            response.put("productIds", List.of());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Xóa tất cả items trong wishlist
     * DELETE /api/wishlist/clear
     * 
     * @param session HTTP Session
     * @return ResponseEntity<WishlistResponseDTO>
     */
    @DeleteMapping("/clear")
    public ResponseEntity<WishlistResponseDTO> clearWishlist(HttpSession session) {
        log.info("=== API: Clear wishlist ===");
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                log.warn("User not logged in");
                WishlistResponseDTO response = WishlistResponseDTO.builder()
                        .success(false)
                        .message("Bạn cần đăng nhập để sử dụng chức năng này")
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            log.info("User {} clearing wishlist", user.getId());
            WishlistResponseDTO response = wishlistService.clearWishlist(user.getId());
            
            log.info("Clear result - success: {}", response.isSuccess());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in clearWishlist API: ", e);
            WishlistResponseDTO response = WishlistResponseDTO.builder()
                    .success(false)
                    .message("Có lỗi xảy ra: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
