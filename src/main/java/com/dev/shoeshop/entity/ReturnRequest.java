package com.dev.shoeshop.entity;

import com.dev.shoeshop.enums.ReturnReason;
import com.dev.shoeshop.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Yêu cầu trả hàng
 */
@Entity
@Table(name = "return_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 50)
    private ReturnReason reason;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "images", columnDefinition = "TEXT")
    private String images;  // JSON array hoặc comma-separated URLs
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private ReturnStatus status = ReturnStatus.PENDING;
    
    @Column(name = "admin_note", length = 500)
    private String adminNote;
    
    @Column(name = "refund_amount", precision = 15, scale = 2)
    private BigDecimal refundAmount;  // Số xu hoàn lại
    
    @Column(name = "shipping_tracking_code", length = 100)
    private String shippingTrackingCode;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "approved_date")
    private LocalDateTime approvedDate;
    
    @Column(name = "received_date")
    private LocalDateTime receivedDate;
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
    
    // Business logic methods
    
    /**
     * Kiểm tra đã được approve chưa
     */
    public boolean isApproved() {
        return status == ReturnStatus.APPROVED || 
               status == ReturnStatus.SHIPPING ||
               status == ReturnStatus.RECEIVED ||
               status == ReturnStatus.REFUNDED;
    }
    
    /**
     * Kiểm tra đã hoàn xu chưa
     */
    public boolean isRefunded() {
        return status == ReturnStatus.REFUNDED;
    }
    
    /**
     * Kiểm tra có thể cancel không
     */
    public boolean canCancel() {
        return status == ReturnStatus.PENDING;
    }
}
