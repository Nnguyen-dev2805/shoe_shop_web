package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.chat.ChatConversationDTO;
import com.dev.shoeshop.dto.chat.ChatMessageDTO;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller cho chat
 * Xử lý lịch sử tin nhắn và quản lý conversations
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatApiController {

    private final ChatService chatService;

    /**
     * Lấy hoặc tạo conversation cho user hiện tại
     * User endpoint: GET /api/chat/conversation
     */
    @GetMapping("/conversation")
    public ResponseEntity<Map<String, Object>> getUserConversation(
            @AuthenticationPrincipal Users currentUser,
            jakarta.servlet.http.HttpSession session) {
        try {
            // Try to get user from AuthenticationPrincipal first
            Users user = currentUser;
            
            // Fallback to session if AuthenticationPrincipal is null
            if (user == null) {
                user = (Users) session.getAttribute("user");
                log.info("User from session: {}", user != null ? user.getId() : "null");
            } else {
                log.info("User from AuthenticationPrincipal: {}", user.getId());
            }
            
            if (user == null) {
                log.warn("No authenticated user found");
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Unauthorized - Please login"
                ));
            }

            log.info("Getting conversation for user: {}", user.getId());
            ChatConversationDTO conversation = chatService.getOrCreateConversation(user.getId());
            log.info("Conversation retrieved: {}", conversation.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", conversation
            ));

        } catch (Exception e) {
            log.error("Error getting user conversation: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to get conversation: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy lịch sử tin nhắn của conversation
     * GET /api/chat/messages/{conversationId}
     */
    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<Map<String, Object>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ChatMessageDTO> messages = chatService.getMessageHistoryPaged(conversationId, pageable);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", messages.getContent(),
                    "totalPages", messages.getTotalPages(),
                    "totalElements", messages.getTotalElements(),
                    "currentPage", messages.getNumber()
            ));

        } catch (Exception e) {
            log.error("Error getting messages: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to get messages: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy tất cả tin nhắn của conversation (không phân trang)
     * GET /api/chat/messages/{conversationId}/all
     */
    @GetMapping("/messages/{conversationId}/all")
    public ResponseEntity<Map<String, Object>> getAllMessages(@PathVariable Long conversationId) {
        try {
            List<ChatMessageDTO> messages = chatService.getMessageHistory(conversationId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", messages
            ));

        } catch (Exception e) {
            log.error("Error getting all messages: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to get messages: " + e.getMessage()
            ));
        }
    }

    // ============= MANAGER ENDPOINTS =============

    /**
     * Lấy tất cả conversations cho manager
     * Manager endpoint: GET /api/chat/manager/conversations
     */
    @GetMapping("/manager/conversations")
    public ResponseEntity<Map<String, Object>> getAllConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean unreadOnly) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ChatConversationDTO> conversations;

            if (Boolean.TRUE.equals(unreadOnly)) {
                conversations = chatService.getUnreadConversations(pageable);
            } else {
                conversations = chatService.getAllConversations(pageable);
            }

            Long unreadCount = chatService.countUnreadConversations();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", conversations.getContent());
            response.put("totalPages", conversations.getTotalPages());
            response.put("totalElements", conversations.getTotalElements());
            response.put("currentPage", conversations.getNumber());
            response.put("unreadCount", unreadCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting conversations: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to get conversations: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy chi tiết conversation theo ID
     * Manager endpoint: GET /api/chat/manager/conversation/{id}
     */
    @GetMapping("/manager/conversation/{id}")
    public ResponseEntity<Map<String, Object>> getConversationById(@PathVariable Long id) {
        try {
            ChatConversationDTO conversation = chatService.getConversationById(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", conversation
            ));

        } catch (Exception e) {
            log.error("Error getting conversation: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to get conversation: " + e.getMessage()
            ));
        }
    }

    /**
     * Đếm số conversation chưa đọc
     * Manager endpoint: GET /api/chat/manager/unread-count
     */
    @GetMapping("/manager/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        try {
            Long unreadCount = chatService.countUnreadConversations();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", unreadCount
            ));

        } catch (Exception e) {
            log.error("Error getting unread count: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to get unread count: " + e.getMessage()
            ));
        }
    }

    /**
     * Đóng conversation
     * Manager endpoint: PUT /api/chat/manager/conversation/{id}/close
     */
    @PutMapping("/manager/conversation/{id}/close")
    public ResponseEntity<Map<String, Object>> closeConversation(@PathVariable Long id) {
        try {
            chatService.closeConversation(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Conversation closed successfully"
            ));

        } catch (Exception e) {
            log.error("Error closing conversation: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to close conversation: " + e.getMessage()
            ));
        }
    }

    /**
     * Mở lại conversation
     * Manager endpoint: PUT /api/chat/manager/conversation/{id}/reopen
     */
    @PutMapping("/manager/conversation/{id}/reopen")
    public ResponseEntity<Map<String, Object>> reopenConversation(@PathVariable Long id) {
        try {
            chatService.reopenConversation(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Conversation reopened successfully"
            ));

        } catch (Exception e) {
            log.error("Error reopening conversation: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to reopen conversation: " + e.getMessage()
            ));
        }
    }

    /**
     * Tìm kiếm tin nhắn trong conversation
     * GET /api/chat/search/{conversationId}
     */
    @GetMapping("/search/{conversationId}")
    public ResponseEntity<Map<String, Object>> searchMessages(
            @PathVariable Long conversationId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ChatMessageDTO> messages = chatService.searchMessages(conversationId, keyword, pageable);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", messages.getContent(),
                    "totalPages", messages.getTotalPages(),
                    "totalElements", messages.getTotalElements(),
                    "currentPage", messages.getNumber()
            ));

        } catch (Exception e) {
            log.error("Error searching messages: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to search messages: " + e.getMessage()
            ));
        }
    }
}
