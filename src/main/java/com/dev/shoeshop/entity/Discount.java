package com.dev.shoeshop.entity;

import com.dev.shoeshop.enums.DiscountType;
import com.dev.shoeshop.enums.VoucherType;
import com.dev.shoeshop.enums.DiscountValueType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "discount")
@Builder
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Discount name cannot be blank")
    @Size(max = 100, message = "Discount name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity must not exceed 10000")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Percent cannot be null")
    @DecimalMin(value = "0.0", message = "Percent must be at least 0%")
    @DecimalMax(value = "1.0", message = "Percent cannot exceed 100% (1.0)")
    @Column(name = "discount_percent", nullable = false)
    private Double percent;

    @NotNull(message = "Status cannot be null")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|COMING|EXPIRED)$", message = "Status must be ACTIVE, INACTIVE, COMING, or EXPIRED")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @DecimalMin(value = "0.0", message = "Minimum order value must be at least 0")
    @Column(name = "min_order_value")
    private Double minOrderValue;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @NotNull(message = "Start date cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_delete", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDelete = false;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_date")
    private LocalDate updatedDate;
    
    // ========== THÊM MỚI: Shipping Voucher Support ==========
    
    /**
     * Loại voucher: ORDER_DISCOUNT (giảm đơn hàng) hoặc SHIPPING_DISCOUNT (giảm phí ship)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private VoucherType type = VoucherType.ORDER_DISCOUNT;
    
    /**
     * Kiểu giảm: PERCENTAGE (%) hoặc FIXED_AMOUNT (VNĐ)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 50)
    private DiscountValueType discountValueType = DiscountValueType.PERCENTAGE;
    
    /**
     * Giảm tối đa (chỉ dùng cho shipping voucher %)
     * Ví dụ: Giảm 50% phí ship tối đa 20.000đ
     */
    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;
    
    // ========== CŨ: Phân loại voucher theo scope ==========
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_scope", length = 20)
    private DiscountType discountType = DiscountType.ORDER; // ORDER, PRODUCT, CATEGORY, SHIPPING
    
    @Column(name = "applies_to_sale_items", nullable = false)
    private Boolean appliesToSaleItems = true; // Có áp dụng cho sản phẩm đang sale không
    
    // Nếu discountType = PRODUCT, lưu danh sách product IDs (JSON hoặc bảng riêng)
    @Column(name = "applicable_product_ids", columnDefinition = "TEXT")
    private String applicableProductIds; // Lưu dạng "1,2,3,5" hoặc JSON
    
    // Nếu discountType = CATEGORY, lưu danh sách category IDs
    @Column(name = "applicable_category_ids", columnDefinition = "TEXT")
    private String applicableCategoryIds; // Lưu dạng "1,2,3"

    // Quan hệ với DiscountUsed (Many-to-Many với Users thông qua bảng trung gian)
    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DiscountUsed> discountUsages = new ArrayList<>();

    // Business logic methods

    /**
     * Kiểm tra discount có đang active không
     */
    public boolean isActive() {
        if (status == null || startDate == null || endDate == null) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        return "ACTIVE".equals(status) && 
               !now.isBefore(startDate) && 
               !now.isAfter(endDate);
    }

    /**
     * Kiểm tra discount có sắp bắt đầu không
     */
    public boolean isComing() {
        if (status == null || startDate == null) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        return "COMING".equals(status) && now.isBefore(startDate);
    }

    /**
     * Kiểm tra discount có hết hạn không
     */
    public boolean isExpired() {
        if (endDate == null) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        return now.isAfter(endDate);
    }

    /**
     * Kiểm tra discount có thể sử dụng không
     */
    public boolean canBeUsed() {
        return isActive() && quantity > 0 && !isDelete;
    }

    /**
     * Giảm số lượng sử dụng
     */
    public void decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }

    /**
     * Tính giá trị giảm giá (cho ORDER voucher)
     */
    public Double calculateDiscountAmount(Double orderValue) {
        if (orderValue == null || percent == null) {
            return 0.0;
        }
        
        if (minOrderValue != null && orderValue < minOrderValue) {
            return 0.0;
        }
        
        Double discountAmount = orderValue * percent;
        
        // Áp dụng max discount amount nếu có
        if (maxDiscountAmount != null && discountAmount > maxDiscountAmount) {
            discountAmount = maxDiscountAmount;
        }
        
        return discountAmount;
    }
    
    /**
     * Tính giá trị giảm phí ship (cho SHIPPING voucher)
     * @param shippingFee Phí ship ban đầu
     * @param orderValue Giá trị đơn hàng (để check minOrderValue)
     * @return Số tiền được giảm
     */
    public Double calculateShippingDiscount(Double shippingFee, Double orderValue) {
        if (shippingFee == null || shippingFee <= 0) {
            return 0.0;
        }
        
        // Check voucher type
        if (type != VoucherType.SHIPPING_DISCOUNT) {
            return 0.0;
        }
        
        // Check min order value
        if (minOrderValue != null && (orderValue == null || orderValue < minOrderValue)) {
            return 0.0;
        }
        
        Double discountAmount = 0.0;
        
        if (discountValueType == DiscountValueType.FIXED_AMOUNT) {
            // Fixed amount: Giảm cố định (ví dụ: 30.000đ)
            // percent field chứa giá trị fixed amount
            discountAmount = Math.min(percent, shippingFee);
        } else if (discountValueType == DiscountValueType.PERCENTAGE) {
            // Percentage: Giảm % (ví dụ: 50%)
            discountAmount = shippingFee * percent;
            
            // Apply max discount nếu có
            if (maxDiscountAmount != null && discountAmount > maxDiscountAmount) {
                discountAmount = maxDiscountAmount;
            }
            
            // Không được giảm quá shipping fee
            discountAmount = Math.min(discountAmount, shippingFee);
        }
        
        return discountAmount;
    }
    
    /**
     * Kiểm tra có phải shipping voucher không
     */
    public boolean isShippingVoucher() {
        return type == VoucherType.SHIPPING_DISCOUNT;
    }
    
    /**
     * Kiểm tra có phải order voucher không
     */
    public boolean isOrderVoucher() {
        return type == VoucherType.ORDER_DISCOUNT;
    }
    
    /**
     * Kiểm tra discount có áp dụng cho product này không
     * @param productId ID của product cần kiểm tra
     * @return true nếu discount áp dụng cho product
     */
    public boolean isApplicableToProduct(Long productId) {
        if (discountType == null || !DiscountType.PRODUCT.equals(discountType)) {
            return false;
        }
        
        if (applicableProductIds == null || applicableProductIds.isEmpty()) {
            return false;
        }
        
        // Parse danh sách product IDs (dạng "1,2,3,5")
        String[] productIds = applicableProductIds.split(",");
        for (String id : productIds) {
            if (id.trim().equals(productId.toString())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Kiểm tra discount có áp dụng cho category này không
     * @param categoryId ID của category cần kiểm tra
     * @return true nếu discount áp dụng cho category
     */
    public boolean isApplicableToCategory(Long categoryId) {
        if (discountType == null || !DiscountType.CATEGORY.equals(discountType)) {
            return false;
        }
        
        if (applicableCategoryIds == null || applicableCategoryIds.isEmpty()) {
            return false;
        }
        
        // Parse danh sách category IDs
        String[] categoryIds = applicableCategoryIds.split(",");
        for (String id : categoryIds) {
            if (id.trim().equals(categoryId.toString())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Kiểm tra discount type
     */
    public boolean isOrderDiscount() {
        return DiscountType.ORDER.equals(discountType);
    }
    
    public boolean isProductDiscount() {
        return DiscountType.PRODUCT.equals(discountType);
    }
    
    public boolean isCategoryDiscount() {
        return DiscountType.CATEGORY.equals(discountType);
    }
    
    public boolean isShippingDiscount() {
        return DiscountType.SHIPPING.equals(discountType);
    }

    /**
     * Lấy phần trăm hiển thị (0.2 -> 20%)
     */
    public String getPercentDisplay() {
        if (percent == null) return "0%";
        return String.format("%.1f%%", percent * 100);
    }

    /**
     * Đếm số lần discount đã được sử dụng
     */
    public long getUsageCount() {
        if (discountUsages == null) return 0;
        return discountUsages.stream()
                .filter(DiscountUsed::getIsActive)
                .count();
    }

    /**
     * Kiểm tra xem discount còn có thể sử dụng không (dựa trên số lượng đã sử dụng)
     */
    public boolean hasRemainingUsage() {
        return getUsageCount() < quantity;
    }

    /**
     * Lấy số lượng discount còn lại có thể sử dụng
     */
    public long getRemainingQuantity() {
        return quantity - getUsageCount();
    }

    /**
     * Kiểm tra user đã sử dụng discount này chưa
     */
    public boolean hasUserUsedDiscount(Users user) {
        if (discountUsages == null || user == null) return false;
        return discountUsages.stream()
                .anyMatch(usage -> usage.getUser().getId().equals(user.getId()) && usage.getIsActive());
    }

    // JPA Lifecycle methods

    @PrePersist
    public void prePersist() {
        if (this.createdDate == null) {
            this.createdDate = LocalDate.now();
        }
        if (this.isDelete == null) {
            this.isDelete = false;
        }
        if (this.status == null) {
            this.status = "INACTIVE";
        }
        if (this.discountType == null) {
            this.discountType = DiscountType.ORDER;
        }
        if (this.appliesToSaleItems == null) {
            this.appliesToSaleItems = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDate.now();
        
        // Auto-update status based on dates
        updateStatusByDate();
    }

    /**
     * Tự động cập nhật status dựa trên ngày tháng
     */
    private void updateStatusByDate() {
        if (startDate == null || endDate == null) {
            return;
        }
        
        LocalDate now = LocalDate.now();
        
        if (now.isAfter(endDate)) {
            this.status = "EXPIRED";
        } else if (!now.isBefore(startDate) && !now.isAfter(endDate)) {
            if ("COMING".equals(this.status)) {
                this.status = "ACTIVE";
            }
        }
    }

    // Static factory methods

    /**
     * Tạo discount mới với các tham số cơ bản
     */
    public static Discount create(String name, Double percent, String status, 
                                 LocalDate startDate, LocalDate endDate) {
        Discount discount = new Discount();
        discount.setName(name);
        discount.setPercent(percent);
        discount.setStatus(status);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setQuantity(1000); // Default quantity
        discount.setIsDelete(false);
        return discount;
    }

    /**
     * Tạo discount với đầy đủ tham số
     */
    public static Discount createFull(String name, Integer quantity, Double percent, 
                                     String status, Double minOrderValue,
                                     LocalDate startDate, LocalDate endDate, Long createdBy) {
        Discount discount = new Discount();
        discount.setName(name);
        discount.setQuantity(quantity);
        discount.setPercent(percent);
        discount.setStatus(status);
        discount.setMinOrderValue(minOrderValue);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setCreatedBy(createdBy);
        discount.setIsDelete(false);
        return discount;
    }
}
