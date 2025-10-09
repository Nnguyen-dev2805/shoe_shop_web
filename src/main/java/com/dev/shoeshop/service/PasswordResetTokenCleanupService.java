package com.dev.shoeshop.service;

import com.dev.shoeshop.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenCleanupService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * Clean up expired password reset tokens every hour
     */
    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 ms)
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            passwordResetTokenRepository.deleteExpiredTokens(now);
            log.info("Cleaned up expired password reset tokens at {}", now);
        } catch (Exception e) {
            log.error("Error cleaning up expired password reset tokens", e);
        }
    }
}
