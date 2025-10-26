package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.chat.ChatConversationDTO;
import com.dev.shoeshop.dto.chat.ChatMessageDTO;
import com.dev.shoeshop.entity.ChatConversation;
import com.dev.shoeshop.entity.ChatMessage;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.ChatConversationRepository;
import com.dev.shoeshop.repository.ChatMessageRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation của ChatService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public ChatConversationDTO getOrCreateConversation(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        ChatConversation conversation = conversationRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ChatConversation newConversation = ChatConversation.builder()
                            .user(user)
                            .status("ACTIVE")
                            .unreadCount(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    log.info("Creating new conversation for user: {}", userId);
                    return conversationRepository.save(newConversation);
                });

        return convertToConversationDTO(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatConversationDTO getConversationById(Long conversationId) {
        ChatConversation conversation = conversationRepository.findByIdWithMessages(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with ID: " + conversationId));
        return convertToConversationDTOWithMessages(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatConversationDTO> getAllConversations(Pageable pageable) {
        return conversationRepository.findAllActiveConversations(pageable)
                .map(this::convertToConversationDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatConversationDTO> getUnreadConversations(Pageable pageable) {
        return conversationRepository.findConversationsWithUnreadMessages(pageable)
                .map(this::convertToConversationDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnreadConversations() {
        return conversationRepository.countUnreadConversations();
    }

    @Override
    public ChatMessageDTO sendMessageFromUser(Long userId, String content) {
        ChatConversation conversation = conversationRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Auto-create conversation if doesn't exist
                    Users user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    ChatConversation newConversation = ChatConversation.builder()
                            .user(user)
                            .status("ACTIVE")
                            .unreadCount(0)
                            .build();
                    return conversationRepository.save(newConversation);
                });

        Users user = conversation.getUser();

        ChatMessage message = ChatMessage.builder()
                .conversation(conversation)
                .sender(user)
                .senderType("USER")
                .content(content)
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build();

        message = messageRepository.save(message);

        // Update conversation
        conversation.updateLastMessageTime();
        conversation.incrementUnreadCount();
        conversationRepository.save(conversation);

        log.info("User {} sent message in conversation {}", userId, conversation.getId());

        return convertToMessageDTO(message);
    }

    @Override
    public ChatMessageDTO sendMessageFromManager(Long managerId, Long conversationId, String content) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with ID: " + conversationId));

        Users manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));

        ChatMessage message = ChatMessage.builder()
                .conversation(conversation)
                .sender(manager)
                .senderType("MANAGER")
                .content(content)
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build();

        message = messageRepository.save(message);

        // Update conversation
        conversation.updateLastMessageTime();
        conversationRepository.save(conversation);

        log.info("Manager {} sent message in conversation {}", managerId, conversationId);

        return convertToMessageDTO(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessageHistory(Long conversationId) {
        List<ChatMessage> messages = messageRepository.findByConversationIdOrderBySentAtAsc(conversationId);
        return messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> getMessageHistoryPaged(Long conversationId, Pageable pageable) {
        return messageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable)
                .map(this::convertToMessageDTO);
    }

    @Override
    public void markUserMessagesAsRead(Long conversationId) {
        int updatedCount = messageRepository.markAllUserMessagesAsRead(conversationId);
        
        if (updatedCount > 0) {
            ChatConversation conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
            conversation.resetUnreadCount();
            conversationRepository.save(conversation);
            
            log.info("Marked {} user messages as read in conversation {}", updatedCount, conversationId);
        }
    }

    @Override
    public void markManagerMessagesAsRead(Long conversationId) {
        int updatedCount = messageRepository.markAllManagerMessagesAsRead(conversationId);
        log.info("Marked {} manager messages as read in conversation {}", updatedCount, conversationId);
    }

    @Override
    public void closeConversation(Long conversationId) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with ID: " + conversationId));
        conversation.close();
        conversationRepository.save(conversation);
        log.info("Closed conversation {}", conversationId);
    }

    @Override
    public void reopenConversation(Long conversationId) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with ID: " + conversationId));
        conversation.reopen();
        conversationRepository.save(conversation);
        log.info("Reopened conversation {}", conversationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> searchMessages(Long conversationId, String keyword, Pageable pageable) {
        return messageRepository.searchMessagesInConversation(conversationId, keyword, pageable)
                .map(this::convertToMessageDTO);
    }

    // ================ Helper Methods ================

    private ChatConversationDTO convertToConversationDTO(ChatConversation conversation) {
        ChatMessage lastMessage = messageRepository.findLatestMessageByConversationId(conversation.getId());
        
        return ChatConversationDTO.builder()
                .id(conversation.getId())
                .userId(conversation.getUser().getId())
                .userName(conversation.getUser().getFullname())
                .userEmail(conversation.getUser().getEmail())
                .status(conversation.getStatus())
                .unreadCount(conversation.getUnreadCount())
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .lastMessage(lastMessage != null ? convertToMessageDTO(lastMessage) : null)
                .build();
    }

    private ChatConversationDTO convertToConversationDTOWithMessages(ChatConversation conversation) {
        List<ChatMessageDTO> messageDTOs = conversation.getMessages().stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());

        ChatMessageDTO lastMessageDTO = messageDTOs.isEmpty() ? null : messageDTOs.get(messageDTOs.size() - 1);

        return ChatConversationDTO.builder()
                .id(conversation.getId())
                .userId(conversation.getUser().getId())
                .userName(conversation.getUser().getFullname())
                .userEmail(conversation.getUser().getEmail())
                .status(conversation.getStatus())
                .unreadCount(conversation.getUnreadCount())
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .messages(messageDTOs)
                .lastMessage(lastMessageDTO)
                .build();
    }

    private ChatMessageDTO convertToMessageDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSenderDisplayName())
                .senderType(message.getSenderType())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .sentAt(message.getSentAt())
                .readAt(message.getReadAt())
                .build();
    }
}
