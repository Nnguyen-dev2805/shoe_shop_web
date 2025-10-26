package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.chat.ChatConversationDTO;
import com.dev.shoeshop.dto.chat.ChatMessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface cho chat functionality
 */
public interface ChatService {

    /**
     * Tạo hoặc lấy conversation của user
     * Nếu user chưa có conversation, tạo mới
     */
    ChatConversationDTO getOrCreateConversation(Long userId);

    /**
     * Lấy conversation theo ID
     */
    ChatConversationDTO getConversationById(Long conversationId);

    /**
     * Lấy tất cả conversations cho manager
     * Sắp xếp theo tin nhắn mới nhất
     */
    Page<ChatConversationDTO> getAllConversations(Pageable pageable);

    /**
     * Lấy conversations có tin nhắn chưa đọc
     */
    Page<ChatConversationDTO> getUnreadConversations(Pageable pageable);

    /**
     * Đếm số conversation có tin nhắn chưa đọc
     */
    Long countUnreadConversations();

    /**
     * Gửi tin nhắn từ user
     */
    ChatMessageDTO sendMessageFromUser(Long userId, String content);

    /**
     * Gửi tin nhắn từ manager
     */
    ChatMessageDTO sendMessageFromManager(Long managerId, Long conversationId, String content);

    /**
     * Lấy lịch sử tin nhắn của conversation
     */
    List<ChatMessageDTO> getMessageHistory(Long conversationId);

    /**
     * Lấy lịch sử tin nhắn với phân trang
     */
    Page<ChatMessageDTO> getMessageHistoryPaged(Long conversationId, Pageable pageable);

    /**
     * Đánh dấu tin nhắn của user là đã đọc (cho manager)
     */
    void markUserMessagesAsRead(Long conversationId);

    /**
     * Đánh dấu tin nhắn của manager là đã đọc (cho user)
     */
    void markManagerMessagesAsRead(Long conversationId);

    /**
     * Đóng conversation
     */
    void closeConversation(Long conversationId);

    /**
     * Mở lại conversation
     */
    void reopenConversation(Long conversationId);

    /**
     * Tìm kiếm tin nhắn trong conversation
     */
    Page<ChatMessageDTO> searchMessages(Long conversationId, String keyword, Pageable pageable);
}
