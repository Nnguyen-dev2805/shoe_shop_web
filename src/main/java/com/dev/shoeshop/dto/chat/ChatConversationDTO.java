package com.dev.shoeshop.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO cho conversation
 * Dùng để hiển thị danh sách conversations cho manager
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatConversationDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String status;
    private Integer unreadCount;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private ChatMessageDTO lastMessage;
    
    @Builder.Default
    private List<ChatMessageDTO> messages = new ArrayList<>();

    /**
     * Kiểm tra có tin nhắn chưa đọc không
     */
    public boolean hasUnreadMessages() {
        return unreadCount != null && unreadCount > 0;
    }
}
