package com.dev.shoeshop.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho notification khi có tin nhắn mới
 * Gửi qua WebSocket cho user hoặc manager
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatNotificationDTO {
    private String type; // NEW_MESSAGE, MESSAGE_READ, USER_TYPING
    private Long conversationId;
    private ChatMessageDTO message;
    private String content;
    private LocalDateTime timestamp;

    /**
     * Tạo notification cho tin nhắn mới
     */
    public static ChatNotificationDTO newMessage(ChatMessageDTO message) {
        return ChatNotificationDTO.builder()
                .type("NEW_MESSAGE")
                .conversationId(message.getConversationId())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Tạo notification cho đánh dấu đã đọc
     */
    public static ChatNotificationDTO messageRead(Long conversationId) {
        return ChatNotificationDTO.builder()
                .type("MESSAGE_READ")
                .conversationId(conversationId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Tạo notification cho user đang gõ
     */
    public static ChatNotificationDTO userTyping(Long conversationId, String content) {
        return ChatNotificationDTO.builder()
                .type("USER_TYPING")
                .conversationId(conversationId)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
