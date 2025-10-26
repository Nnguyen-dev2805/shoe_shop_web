package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho ChatMessage entity
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Lấy tất cả messages của một conversation
     */
    List<ChatMessage> findByConversationIdOrderBySentAtAsc(Long conversationId);

    /**
     * Lấy messages của một conversation với phân trang
     */
    Page<ChatMessage> findByConversationIdOrderBySentAtDesc(Long conversationId, Pageable pageable);

    /**
     * Đếm số tin nhắn chưa đọc trong một conversation
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.conversation.id = :conversationId AND m.senderType = 'USER' AND m.isRead = false")
    Long countUnreadUserMessagesInConversation(@Param("conversationId") Long conversationId);

    /**
     * Đếm tin nhắn chưa đọc cho user (từ manager)
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.conversation.user.id = :userId AND m.senderType = 'MANAGER' AND m.isRead = false")
    Long countUnreadMessagesForUser(@Param("userId") Long userId);

    /**
     * Lấy tin nhắn cuối cùng của một conversation
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt DESC LIMIT 1")
    ChatMessage findLatestMessageByConversationId(@Param("conversationId") Long conversationId);

    /**
     * Đánh dấu tất cả tin nhắn của user trong conversation là đã đọc
     */
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP WHERE m.conversation.id = :conversationId AND m.senderType = 'USER' AND m.isRead = false")
    int markAllUserMessagesAsRead(@Param("conversationId") Long conversationId);

    /**
     * Đánh dấu tất cả tin nhắn của manager trong conversation là đã đọc (cho user)
     */
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP WHERE m.conversation.id = :conversationId AND m.senderType = 'MANAGER' AND m.isRead = false")
    int markAllManagerMessagesAsRead(@Param("conversationId") Long conversationId);

    /**
     * Lấy N tin nhắn gần nhất của conversation
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt DESC LIMIT :limit")
    List<ChatMessage> findRecentMessages(@Param("conversationId") Long conversationId, @Param("limit") int limit);

    /**
     * Tìm kiếm tin nhắn theo nội dung trong conversation
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.id = :conversationId AND LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY m.sentAt DESC")
    Page<ChatMessage> searchMessagesInConversation(@Param("conversationId") Long conversationId, @Param("keyword") String keyword, Pageable pageable);
}
