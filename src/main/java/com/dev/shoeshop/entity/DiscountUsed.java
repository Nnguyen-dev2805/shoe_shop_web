package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "discount_used")
public class DiscountUsed {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @NotNull(message = "User cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    @NotNull(message = "Discount cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;
    
    @Column(name = "used_date", nullable = false)
    private LocalDateTime usedDate;
    
    @Column(name = "order_id")
    private Long orderId; // Tham chiếu đến đơn hàng sử dụng discount
    
    @Column(name = "discount_amount")
    private Double discountAmount; // Số tiền được giảm
    
    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    private Boolean isActive = true;
    
    // Constructors
    public DiscountUsed(Users user, Discount discount, Long orderId, Double discountAmount) {
        this.user = user;
        this.discount = discount;
        this.orderId = orderId;
        this.discountAmount = discountAmount;
        this.usedDate = LocalDateTime.now();
        this.isActive = true;
    }
    
    // JPA Lifecycle methods
    @PrePersist
    public void prePersist() {
        if (this.usedDate == null) {
            this.usedDate = LocalDateTime.now();
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
    
    // Business logic methods
    
    /**
     * Kiểm tra discount có được sử dụng trong khoảng thời gian hợp lệ không
     */
    public boolean isValidUsage() {
        if (discount == null || usedDate == null) {
            return false;
        }
        
        return discount.isActive() && 
               !usedDate.isBefore(discount.getStartDate().atStartOfDay()) &&
               !usedDate.isAfter(discount.getEndDate().atTime(23, 59, 59));
    }
    
    /**
     * Đánh dấu discount đã được sử dụng
     */
    public void markAsUsed() {
        this.isActive = false;
    }
    
    /**
     * Kiểm tra xem user có thể sử dụng discount này không
     */
    public boolean canUserUseDiscount() {
        return isActive && 
               user != null && 
               discount != null && 
               discount.canBeUsed() &&
               isValidUsage();
    }
}
