package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity lưu trữ cuộc hội thoại giữa user và shop
 * Mỗi user chỉ có 1 conversation với shop
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_conversations")
@Builder
public class ChatConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Users user;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, CLOSED, ARCHIVED

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "unread_count")
    @Builder.Default
    private Integer unreadCount = 0;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OrderBy("sentAt ASC")
    private List<ChatMessage> messages = new ArrayList<>();

    /**
     * Cập nhật thời gian tin nhắn cuối cùng
     */
    public void updateLastMessageTime() {
        this.lastMessageAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Tăng số lượng tin nhắn chưa đọc
     */
    public void incrementUnreadCount() {
        this.unreadCount = (this.unreadCount == null ? 0 : this.unreadCount) + 1;
    }

    /**
     * Reset số lượng tin nhắn chưa đọc
     */
    public void resetUnreadCount() {
        this.unreadCount = 0;
    }

    /**
     * Thêm message vào conversation
     */
    public void addMessage(ChatMessage message) {
        messages.add(message);
        message.setConversation(this);
        updateLastMessageTime();
    }

    /**
     * Đóng conversation
     */
    public void close() {
        this.status = "CLOSED";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mở lại conversation
     */
    public void reopen() {
        this.status = "ACTIVE";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Kiểm tra conversation có đang active không
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}
