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
    @Column(name = "pay_option", columnDefinition = "ENUM('COD', 'VNPAY')", nullable = false)
    private PayOption payOption;


    @ManyToOne
//    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "delivery_address_id", nullable = false) // Khóa ngoại đến User
    private Address address;
    
    // ========== THÊM MỚI: Discount & Flash Sale ==========
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount appliedDiscount; // Voucher/discount đã áp dụng
    
    @Column(name = "discount_amount")
    private Double discountAmount; // Số tiền được giảm
    
    @Column(name = "original_total_price")
    private Double originalTotalPrice; // Giá gốc trước khi giảm giá
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id")
    private FlashSale appliedFlashSale; // Nếu mua từ flash sale
    
    @Column(name = "discount_code")
    private String discountCode; // Mã voucher (nếu có)
    
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
}
