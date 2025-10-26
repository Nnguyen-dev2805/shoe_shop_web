package com.dev.shoeshop.controller.websocket;

import com.dev.shoeshop.dto.chat.ChatMessageDTO;
import com.dev.shoeshop.dto.chat.ChatNotificationDTO;
import com.dev.shoeshop.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket Controller cho chat messaging
 * 
 * Message Flow:
 * 1. User gửi tin nhắn qua /app/chat.sendUser
 * 2. Manager nhận notification qua /topic/chat.managers
 * 3. Manager reply qua /app/chat.sendManager
 * 4. User nhận tin nhắn qua /queue/chat.user.{userId}
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Xử lý tin nhắn từ user
     * User gửi qua /app/chat.sendUser
     * Broadcast đến tất cả managers qua /topic/chat.managers
     */
    @MessageMapping("/chat.sendUser")
    @SendTo("/topic/chat.managers")
    public ChatNotificationDTO handleUserMessage(@Payload ChatMessageDTO messageDTO) {
        try {
            log.info("Received message from user {}: {}", messageDTO.getSenderId(), messageDTO.getContent());

            // Lưu tin nhắn vào database
            ChatMessageDTO savedMessage = chatService.sendMessageFromUser(
                    messageDTO.getSenderId(),
                    messageDTO.getContent()
            );

            // Tạo notification cho managers
            ChatNotificationDTO notification = ChatNotificationDTO.newMessage(savedMessage);

            log.info("Broadcasting message to managers from conversation {}", savedMessage.getConversationId());

            return notification;

        } catch (Exception e) {
            log.error("Error handling user message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Xử lý tin nhắn từ manager
     * Manager gửi qua /app/chat.sendManager
     * Gửi trực tiếp đến user qua /queue/chat.user.{userId}
     */
    @MessageMapping("/chat.sendManager")
    public void handleManagerMessage(@Payload ChatMessageDTO messageDTO) {
        try {
            log.info("Received message from manager {} to conversation {}", 
                    messageDTO.getSenderId(), messageDTO.getConversationId());

            // Lưu tin nhắn vào database
            ChatMessageDTO savedMessage = chatService.sendMessageFromManager(
                    messageDTO.getSenderId(),
                    messageDTO.getConversationId(),
                    messageDTO.getContent()
            );

            // Lấy thông tin conversation để biết userId
            var conversation = chatService.getConversationById(savedMessage.getConversationId());

            // Gửi tin nhắn đến user cụ thể
            ChatNotificationDTO notification = ChatNotificationDTO.newMessage(savedMessage);
            messagingTemplate.convertAndSend(
                    "/queue/chat.user." + conversation.getUserId(),
                    notification
            );

            // Broadcast đến tất cả managers để sync
            messagingTemplate.convertAndSend(
                    "/topic/chat.managers",
                    notification
            );

            log.info("Sent message to user {} from manager", conversation.getUserId());

        } catch (Exception e) {
            log.error("Error handling manager message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Đánh dấu tin nhắn đã đọc từ phía user
     */
    @MessageMapping("/chat.markReadUser")
    public void handleUserMarkRead(@Payload Long conversationId) {
        try {
            log.info("User marking messages as read in conversation {}", conversationId);
            chatService.markManagerMessagesAsRead(conversationId);

            // Notify managers
            ChatNotificationDTO notification = ChatNotificationDTO.messageRead(conversationId);
            messagingTemplate.convertAndSend("/topic/chat.managers", notification);

        } catch (Exception e) {
            log.error("Error marking user messages as read: {}", e.getMessage(), e);
        }
    }

    /**
     * Đánh dấu tin nhắn đã đọc từ phía manager
     */
    @MessageMapping("/chat.markReadManager")
    public void handleManagerMarkRead(@Payload Long conversationId) {
        try {
            log.info("Manager marking messages as read in conversation {}", conversationId);
            chatService.markUserMessagesAsRead(conversationId);

            var conversation = chatService.getConversationById(conversationId);

            // Notify user
            ChatNotificationDTO notification = ChatNotificationDTO.messageRead(conversationId);
            messagingTemplate.convertAndSend(
                    "/queue/chat.user." + conversation.getUserId(),
                    notification
            );

        } catch (Exception e) {
            log.error("Error marking manager messages as read: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý typing indicator từ user
     */
    @MessageMapping("/chat.userTyping")
    @SendTo("/topic/chat.managers")
    public ChatNotificationDTO handleUserTyping(@Payload ChatNotificationDTO notification) {
        log.debug("User typing in conversation {}", notification.getConversationId());
        return notification;
    }
}
