package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "discount")
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
     * Tính giá trị giảm giá
     */
    public Double calculateDiscountAmount(Double orderValue) {
        if (orderValue == null || percent == null) {
            return 0.0;
        }
        
        if (minOrderValue != null && orderValue < minOrderValue) {
            return 0.0;
        }
        
        return orderValue * percent;
    }

    /**
     * Lấy phần trăm hiển thị (0.2 -> 20%)
     */
    public String getPercentDisplay() {
        if (percent == null) return "0%";
        return String.format("%.1f%%", percent * 100);
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
