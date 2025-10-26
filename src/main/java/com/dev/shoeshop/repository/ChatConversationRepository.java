package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.ChatConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho ChatConversation entity
 */
@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    /**
     * Tìm conversation của một user
     */
    Optional<ChatConversation> findByUserId(Long userId);

    /**
     * Tìm conversation của một user với fetch messages
     */
    @Query("SELECT c FROM ChatConversation c LEFT JOIN FETCH c.messages WHERE c.user.id = :userId")
    Optional<ChatConversation> findByUserIdWithMessages(@Param("userId") Long userId);

    /**
     * Kiểm tra user đã có conversation chưa
     */
    boolean existsByUserId(Long userId);

    /**
     * Lấy tất cả conversations đang active, sắp xếp theo tin nhắn mới nhất
     * Dùng cho manager view
     */
    @Query("SELECT c FROM ChatConversation c WHERE c.status = 'ACTIVE' ORDER BY c.lastMessageAt DESC")
    Page<ChatConversation> findAllActiveConversations(Pageable pageable);

    /**
     * Lấy tất cả conversations có tin nhắn chưa đọc
     */
    @Query("SELECT c FROM ChatConversation c WHERE c.status = 'ACTIVE' AND c.unreadCount > 0 ORDER BY c.lastMessageAt DESC")
    Page<ChatConversation> findConversationsWithUnreadMessages(Pageable pageable);

    /**
     * Đếm số conversation có tin nhắn chưa đọc
     */
    @Query("SELECT COUNT(c) FROM ChatConversation c WHERE c.status = 'ACTIVE' AND c.unreadCount > 0")
    Long countUnreadConversations();

    /**
     * Tìm conversation theo ID với fetch messages
     */
    @Query("SELECT c FROM ChatConversation c LEFT JOIN FETCH c.messages WHERE c.id = :id")
    Optional<ChatConversation> findByIdWithMessages(@Param("id") Long id);

    /**
     * Lấy conversation mới nhất của user
     */
    @Query("SELECT c FROM ChatConversation c WHERE c.user.id = :userId ORDER BY c.createdAt DESC LIMIT 1")
    Optional<ChatConversation> findLatestByUserId(@Param("userId") Long userId);
}
