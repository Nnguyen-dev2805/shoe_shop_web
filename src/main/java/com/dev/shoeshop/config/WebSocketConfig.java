package com.dev.shoeshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for real-time notifications
 * Sử dụng STOMP protocol over WebSocket để gửi thông báo đơn hàng mới đến admin
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker
     * - /topic: for pub-sub (broadcast to all subscribers)
     * - /queue: for point-to-point messages
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple in-memory broker
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefix for messages from client to server
        config.setApplicationDestinationPrefixes("/app");
        
        System.out.println("✅ WebSocket Message Broker configured");
    }

    /**
     * Register STOMP endpoint
     * Client sẽ connect đến /ws endpoint
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register endpoint /ws with SockJS fallback
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allow all origins (adjust for production)
                .withSockJS(); // Enable SockJS fallback for browsers without WebSocket
        
        System.out.println("✅ WebSocket endpoint /ws registered");
    }
}
