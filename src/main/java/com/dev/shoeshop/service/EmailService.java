package com.dev.shoeshop.service;

import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.ReturnRequest;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String verificationCode);
    void sendRegistrationVerificationEmail(String toEmail, String verificationCode);
    void sendOrderShippedEmail(Order order);
    void sendReturnApprovedEmail(ReturnRequest returnRequest);
    void sendRefundCompletedEmail(ReturnRequest returnRequest);
    void sendReturnRejectedEmail(ReturnRequest returnRequest);
}
