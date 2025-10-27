package com.dev.shoeshop.controller;

import com.dev.shoeshop.dto.gemini.GeminiChatRequest;
import com.dev.shoeshop.dto.gemini.GeminiChatResponse;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.GeminiService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
@Slf4j
public class GeminiController {
    private final GeminiService geminiService;
    private final UserRepository userRepository;
    
    /**
     * Chat endpoint - SECURE: Lấy userId từ authenticated user
     * 
     * Security:
     * - Nếu user đã đăng nhập → Lấy userId từ Security Context/Session
     * - Không tin tưởng userId từ client request (SECURITY FIX)
     * - Guest user (chưa đăng nhập) → userId = null, không xem được đơn hàng
     */
    @PostMapping("/chat")
    public ResponseEntity<GeminiChatResponse> chat(
            @RequestBody GeminiChatRequest request,
            Authentication authentication,
            HttpSession session) {
        try {
            // Validate message
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(GeminiChatResponse.error("Message không được để trống"));
            }
            
            // 🔒 SECURITY: Lấy userId từ authenticated user (KHÔNG tin tưởng client)
            Long authenticatedUserId = getAuthenticatedUserId(authentication, session);
            
            if (authenticatedUserId != null) {
                log.info("Authenticated user chat: userId={}, message={}", 
                        authenticatedUserId, request.getMessage());
            } else {
                log.info("Guest user chat: message={}", request.getMessage());
            }
            
            // Gọi service với userId đã verify
            String response = geminiService.askGemini(request.getMessage(), authenticatedUserId);
            return ResponseEntity.ok(GeminiChatResponse.success(response));
            
        } catch (Exception e) {
            log.error("Error in chat endpoint: ", e);
            return ResponseEntity.internalServerError()
                    .body(GeminiChatResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 🔒 SECURITY METHOD: Lấy userId từ authenticated user
     * Ưu tiên: Spring Security Authentication → Session → null (guest)
     */
    private Long getAuthenticatedUserId(Authentication authentication, HttpSession session) {
        // Method 1: Lấy từ Spring Security Authentication
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            try {
                String email = authentication.getName();
                Users user = userRepository.findByEmail(email);
                if (user != null) {
                    log.debug("User authenticated via Spring Security: {}", user.getId());
                    return user.getId();
                }
            } catch (Exception e) {
                log.warn("Failed to get user from Authentication: {}", e.getMessage());
            }
        }
        
        // Method 2: Fallback to Session (for traditional login)
        try {
            Users sessionUser = (Users) session.getAttribute(Constant.SESSION_USER);
            if (sessionUser != null) {
                log.debug("User authenticated via Session: {}", sessionUser.getId());
                return sessionUser.getId();
            }
        } catch (Exception e) {
            log.warn("Failed to get user from Session: {}", e.getMessage());
        }
        
        // Method 3: Guest user (not authenticated)
        log.debug("No authenticated user found - Guest mode");
        return null;
    }
    
    /**
     * Backward compatibility: Endpoint cũ vẫn hoạt động (không secure)
     * @deprecated Sử dụng /chat endpoint thay thế
     */
    @PostMapping("/ask")
    @Deprecated
    public String askGeminiAPI(@RequestBody String prompt) {
        log.warn("Using deprecated /ask endpoint without security");
        return geminiService.askGemini(prompt);
    }
}