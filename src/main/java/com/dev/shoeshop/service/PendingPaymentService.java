package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.PendingPaymentDTO;

/**
 * Service to manage pending PayOS payments
 * Stores payment data temporarily until PayOS webhook confirms payment
 */
public interface PendingPaymentService {
    
    /**
     * Store pending payment data
     * @param payosOrderCode PayOS order code
     * @param paymentData Payment information
     */
    void storePendingPayment(Long payosOrderCode, PendingPaymentDTO paymentData);
    
    /**
     * Retrieve pending payment data
     * @param payosOrderCode PayOS order code
     * @return Payment data or null if not found
     */
    PendingPaymentDTO getPendingPayment(Long payosOrderCode);
    
    /**
     * Remove pending payment after processing
     * @param payosOrderCode PayOS order code
     */
    void removePendingPayment(Long payosOrderCode);
    
    /**
     * Clean up expired pending payments (older than 30 minutes)
     */
    void cleanupExpiredPayments();
}
