package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ tin nhắn trong conversation
 * Tin nhắn có thể được gửi bởi user hoặc manager (shop staff)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_messages")
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ChatConversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Users sender;

    @Column(name = "sender_type", nullable = false, length = 20)
    private String senderType; // USER, MANAGER

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "sent_at", nullable = false)
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * Đánh dấu tin nhắn đã đọc
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Kiểm tra tin nhắn có phải từ user không
     */
    public boolean isFromUser() {
        return "USER".equals(this.senderType);
    }

    /**
     * Kiểm tra tin nhắn có phải từ manager không
     */
    public boolean isFromManager() {
        return "MANAGER".equals(this.senderType);
    }

    /**
     * Lấy tên người gửi để hiển thị
     * - Nếu là user: hiển thị tên user
     * - Nếu là manager: hiển thị "Shop Support"
     */
    public String getSenderDisplayName() {
        if (isFromManager()) {
            return "Shop Support";
        }
        return sender != null ? sender.getFullname() : "Unknown";
    }
}
