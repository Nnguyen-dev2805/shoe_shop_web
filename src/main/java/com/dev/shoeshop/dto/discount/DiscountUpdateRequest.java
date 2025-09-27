package com.dev.shoeshop.dto.discount;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountUpdateRequest {
    
    @NotNull(message = "ID is required")
    private Long id;
    
    @NotBlank(message = "Discount name is required")
    @Size(max = 100, message = "Discount name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity must not exceed 10000")
    private Integer quantity;
    
    @NotNull(message = "Percent is required")
    @DecimalMin(value = "0.0", message = "Percent must be at least 0")
    @DecimalMax(value = "1.0", message = "Percent must not exceed 1.0 (100%)")
    private Double percent;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|COMING|EXPIRED)$", message = "Status must be ACTIVE, INACTIVE, COMING, or EXPIRED")
    private String status;
    
    @DecimalMin(value = "0.0", message = "Minimum order value must be at least 0")
    private Double minOrderValue;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    // Validation method để kiểm tra end date > start date
    @AssertTrue(message = "End date must be after start date")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null cases
        }
        return endDate.isAfter(startDate);
    }
}
