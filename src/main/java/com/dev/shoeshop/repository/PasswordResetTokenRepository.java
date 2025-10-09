package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByEmailAndVerificationCodeAndIsUsedFalse(String email, String verificationCode);
    
    Optional<PasswordResetToken> findTopByEmailAndIsUsedFalseOrderByCreatedAtDesc(String email);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE PasswordResetToken p SET p.isUsed = true WHERE p.email = :email AND p.isUsed = false")
    void markAllTokensAsUsedForEmail(@Param("email") String email);
}
