package com.dev.shoeshop.service;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String verificationCode);
    void sendRegistrationVerificationEmail(String toEmail, String verificationCode);
}
