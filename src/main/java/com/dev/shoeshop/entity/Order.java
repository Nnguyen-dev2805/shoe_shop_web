package com.dev.shoeshop.entity;


import com.dev.shoeshop.enums.PayOption;
import com.dev.shoeshop.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
//    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "customer_id", nullable = false) // Khóa ngoại đến User
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Users user;

    @Column(name = "total_price", nullable = false)
//    @NotNull(message = "Total price cannot be null")
    private Double totalPrice;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP) // Định dạng DateTime
    private Date createdDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('IN_STOCK', 'SHIPPED', 'DELIVERED', 'CANCEL', 'RETURN')", nullable = false)
    private ShipmentStatus status;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OrderDetail> orderDetailSet;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_option", columnDefinition = "ENUM('COD', 'VNPAY', 'PAYOS')", nullable = false)
    private PayOption payOption;


    @ManyToOne
//    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "delivery_address_id", nullable = false) // Khóa ngoại đến User
    private Address address;
    
    // ========== THÊM MỚI: Discount & Flash Sale ==========
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount appliedDiscount; // Order voucher/discount đã áp dụng
    
    @Column(name = "discount_amount")
    private Double discountAmount; // Số tiền được giảm từ order voucher
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_discount_id")
    private Discount shippingDiscount; // Shipping voucher đã áp dụng
    
    @Column(name = "shipping_discount_amount")
    private Double shippingDiscountAmount; // Số tiền được giảm từ shipping voucher
    
    @Column(name = "original_total_price")
    private Double originalTotalPrice; // Giá gốc trước khi giảm giá
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id")
    private FlashSale appliedFlashSale; // Nếu mua từ flash sale


    // ========== PAYOS PAYMENT TRACKING ==========

    @Column(name = "payos_order_code")
    private Long payosOrderCode; // PayOS order code để tracking thanh toán

    @Column(name = "payment_status")
    private String paymentStatus; // PENDING, PAID, FAILED, CANCELLED

    @Column(name = "paid_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt; // Thời điểm thanh toán thành công

    // ========== LOYALTY POINTS ==========
    
    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0; // Điểm tích được từ đơn này
    
    @Column(name = "points_redeemed")
    @Builder.Default
    private Integer pointsRedeemed = 0; // Điểm đã sử dụng cho đơn này

    // Business logic methods
    
    /**
     * Tính giá cuối cùng sau khi áp dụng discount
     */
    public Double calculateFinalPrice() {
        if (originalTotalPrice == null) {
            return totalPrice;
        }
        if (discountAmount == null) {
            return originalTotalPrice;
        }
        return Math.max(0, originalTotalPrice - discountAmount);
    }
    
    /**
     * Kiểm tra order có áp dụng discount không
     */
    public boolean hasDiscount() {
        return appliedDiscount != null && discountAmount != null && discountAmount > 0;
    }
    
    /**
     * Kiểm tra order có từ flash sale không
     */
    public boolean isFromFlashSale() {
        return appliedFlashSale != null;
    }
    
    // ========== LOYALTY POINTS METHODS ==========
    
    /**
     * Tính giá trị giảm từ điểm
     */
    public double calculatePointsDiscount() {
        if (pointsRedeemed == null || pointsRedeemed == 0) {
            return 0;
        }
        return pointsRedeemed * 1000.0; // 1 điểm = 1,000 VNĐ
    }
    
    /**
     * Tính giá cuối cùng sau khi trừ điểm (dùng để tính points earned)
     */
    public double getFinalPriceAfterPoints() {
        return Math.max(0, totalPrice - calculatePointsDiscount());
    }
    
    /**
     * Kiểm tra order có dùng điểm không
     */
    public boolean hasPointsRedeemed() {
        return pointsRedeemed != null && pointsRedeemed > 0;
    }
    
    /**
     * Kiểm tra order có tích điểm không
     */
    public boolean hasPointsEarned() {
        return pointsEarned != null && pointsEarned > 0;
    }
}
