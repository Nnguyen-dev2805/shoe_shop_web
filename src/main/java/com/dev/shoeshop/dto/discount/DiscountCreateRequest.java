package com.dev.shoeshop.dto.discount;

import com.dev.shoeshop.enums.VoucherType;
import com.dev.shoeshop.enums.DiscountValueType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCreateRequest {
    
    @NotBlank(message = "Discount name is required")
    @Size(max = 100, message = "Discount name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity must not exceed 10000")
    private Integer quantity;
    
    @NotNull(message = "Percent is required")
    @DecimalMin(value = "0.0", message = "Value must be at least 0")
    // Removed @DecimalMax - for PERCENTAGE: 0-1, for FIXED_AMOUNT: any positive amount
    private Double percent;  // For PERCENTAGE: 0-1, For FIXED_AMOUNT: actual amount (e.g., 30000)
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|COMING|EXPIRED)$", message = "Status must be ACTIVE, INACTIVE, COMING, or EXPIRED")
    private String status;
    
    @DecimalMin(value = "0.0", message = "Minimum order value must be at least 0")
    private Double minOrderValue;
    
    @NotNull(message = "Start date is required")
    // Removed @Future - allow today's date for immediate activation
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    // Removed @Future - validation handled in isValidDateRange()
    private LocalDate endDate;
    
    // ========== NEW: Shipping Voucher Fields ==========
    
    @NotNull(message = "Voucher type is required")
    private VoucherType type = VoucherType.ORDER_DISCOUNT;
    
    @NotNull(message = "Discount value type is required")
    private DiscountValueType discountValueType = DiscountValueType.PERCENTAGE;
    
    @DecimalMin(value = "0.0", message = "Max discount amount must be at least 0")
    private Double maxDiscountAmount;  // Only for SHIPPING + PERCENTAGE
    
    // Validation method để kiểm tra end date > start date
    @AssertTrue(message = "End date must be after start date")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null cases
        }
        return endDate.isAfter(startDate);
    }
}
