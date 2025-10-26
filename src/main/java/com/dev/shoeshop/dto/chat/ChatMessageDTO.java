package com.dev.shoeshop.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho tin nhắn chat
 * Dùng cho cả gửi và nhận messages qua WebSocket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String senderType; // USER, MANAGER
    private String content;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    /**
     * Constructor cho tin nhắn gửi đi (chưa có ID)
     */
    public ChatMessageDTO(Long conversationId, Long senderId, String senderType, String content) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderType = senderType;
        this.content = content;
        this.isRead = false;
        this.sentAt = LocalDateTime.now();
    }
}
