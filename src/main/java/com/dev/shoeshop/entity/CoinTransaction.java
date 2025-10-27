package com.dev.shoeshop.entity;

import com.dev.shoeshop.enums.CoinTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Lịch sử giao dịch DeeG Xu
 */
@Entity
@Table(name = "coin_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Users user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private CoinTransactionType transactionType;
    
    @Column(name = "amount", nullable = false)
    private Long amount;  // + cho nhận xu, - cho tiêu xu
    
    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;  // Số dư sau giao dịch
    
    @Column(name = "reference_type", length = 50)
    private String referenceType;  // "ORDER", "RETURN_REQUEST", "PROMOTION"
    
    @Column(name = "reference_id")
    private Long referenceId;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}
