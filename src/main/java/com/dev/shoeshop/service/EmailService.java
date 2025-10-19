package com.dev.shoeshop.service;

import com.dev.shoeshop.entity.Order;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String verificationCode);
    void sendRegistrationVerificationEmail(String toEmail, String verificationCode);
    void sendOrderShippedEmail(Order order);
}
