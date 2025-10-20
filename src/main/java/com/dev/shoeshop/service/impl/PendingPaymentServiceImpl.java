package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.PendingPaymentDTO;
import com.dev.shoeshop.service.PendingPaymentService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of PendingPaymentService
 * For production, consider using Redis for distributed caching
 */
@Service
public class PendingPaymentServiceImpl implements PendingPaymentService {
    
    // In-memory storage (use Redis in production for scalability)
    private final Map<Long, PendingPaymentDTO> pendingPayments = new ConcurrentHashMap<>();
    
    private static final long EXPIRY_TIME_MS = 30 * 60 * 1000; // 30 minutes
    
    @Override
    public void storePendingPayment(Long payosOrderCode, PendingPaymentDTO paymentData) {
        paymentData.setCreatedAt(System.currentTimeMillis());
        pendingPayments.put(payosOrderCode, paymentData);
        System.out.println("✅ Stored pending payment for PayOS order code: " + payosOrderCode);
    }
    
    @Override
    public PendingPaymentDTO getPendingPayment(Long payosOrderCode) {
        PendingPaymentDTO payment = pendingPayments.get(payosOrderCode);
        if (payment != null) {
            System.out.println("✅ Retrieved pending payment for PayOS order code: " + payosOrderCode);
        } else {
            System.out.println("⚠️ No pending payment found for PayOS order code: " + payosOrderCode);
        }
        return payment;
    }
    
    @Override
    public void removePendingPayment(Long payosOrderCode) {
        pendingPayments.remove(payosOrderCode);
        System.out.println("🗑️ Removed pending payment for PayOS order code: " + payosOrderCode);
    }
    
    @Override
    public void cleanupExpiredPayments() {
        long now = System.currentTimeMillis();
        final int[] removedCount = {0};
        
        pendingPayments.entrySet().removeIf(entry -> {
            boolean expired = (now - entry.getValue().getCreatedAt()) > EXPIRY_TIME_MS;
            if (expired) {
                removedCount[0]++;
            }
            return expired;
        });
        
        if (removedCount[0] > 0) {
            System.out.println("🧹 Cleaned up " + removedCount[0] + " expired pending payments");
        }
    }
}
