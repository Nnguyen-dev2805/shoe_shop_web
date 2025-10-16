package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullname;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone", length = 10)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // ⚡ THÊM: OAuth2 fields
    @Column(name = "provider", length = 50)
    private String provider; // GOOGLE, FACEBOOK, LOCAL
    
    @Column(name = "profile_picture", length = 500)
    private String profilePicture;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<UserAddress> userAddresses = new ArrayList<>();

    // Quan hệ với DiscountUsed (Many-to-Many với Discount thông qua bảng trung gian)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<DiscountUsed> discountUsages = new ArrayList<>();

    // Quan hệ One-to-One với Shipper (optional - user có thể đăng ký làm shipper)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Shipper shipper;

    // Business logic methods for discount usage

    /**
     * Đếm số lần user đã sử dụng discount
     */
    public long getDiscountUsageCount() {
        if (discountUsages == null) return 0;
        return discountUsages.stream()
                .filter(DiscountUsed::getIsActive)
                .count();
    }

    /**
     * Kiểm tra user đã sử dụng discount cụ thể chưa
     */
    public boolean hasUsedDiscount(Discount discount) {
        if (discountUsages == null || discount == null) return false;
        return discountUsages.stream()
                .anyMatch(usage -> usage.getDiscount().getId().equals(discount.getId()) && usage.getIsActive());
    }

    /**
     * Lấy danh sách các discount user đã sử dụng
     */
    public List<Discount> getUsedDiscounts() {
        if (discountUsages == null) return new ArrayList<>();
        return discountUsages.stream()
                .filter(DiscountUsed::getIsActive)
                .map(DiscountUsed::getDiscount)
                .toList();
    }

    /**
     * Lấy tổng số tiền user đã tiết kiệm từ discount
     */
    public Double getTotalDiscountSavings() {
        if (discountUsages == null) return 0.0;
        return discountUsages.stream()
                .filter(DiscountUsed::getIsActive)
                .mapToDouble(usage -> usage.getDiscountAmount() != null ? usage.getDiscountAmount() : 0.0)
                .sum();
    }

    // Business logic methods for shipper

    /**
     * Kiểm tra user có phải là shipper không
     */
    public boolean isShipper() {
        return shipper != null;
    }

    /**
     * Kiểm tra user có đang hoạt động như shipper không
     */
    public boolean isActiveShipper() {
        return shipper != null && shipper.isWorking();
    }

    /**
     * Lấy thông tin shipper của user
     */
    public Shipper getShipperInfo() {
        return shipper;
    }

    /**
     * Lấy thông tin công ty vận chuyển của user (nếu là shipper)
     */
    public ShippingCompany getShippingCompany() {
        return shipper != null ? shipper.getShippingCompany() : null;
    }


    /**
     * Đăng ký user làm shipper
     */
    public void registerAsShipper(ShippingCompany shippingCompany) {
        if (shipper == null) {
            shipper = Shipper.createFromUser(this, shippingCompany);
        }
    }

    /**
     * Hủy đăng ký shipper
     */
    public void unregisterAsShipper() {
        if (shipper != null) {
            shipper = null;
        }
    }

    /**
     * Lấy role hiển thị (bao gồm cả shipper nếu có)
     */
    public String getDisplayRole() {
        if (role == null) return "Unknown";
        
        String baseRole = role.getRoleName();
        if (isActiveShipper()) {
            return baseRole + " (Shipper)";
        }
        return baseRole;
    }
}
