package com.dev.shoeshop.dto.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResponse {
    private Long id;
    private String name;
    private Integer quantity;
    private Double percent;
    private String status;
    private Double minOrderValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdDate;
    
    // Helper method để format percent cho hiển thị
    public String getPercentDisplay() {
        if (percent == null) return "0%";
        return String.format("%.1f%%", percent * 100);
    }
    
    // Helper method để kiểm tra discount có active không
    public boolean isActive() {
        if (status == null || startDate == null || endDate == null) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        return "ACTIVE".equals(status) && 
               !now.isBefore(startDate) && 
               !now.isAfter(endDate);
    }
    
    // Helper method để kiểm tra discount có coming không
    public boolean isComing() {
        if (status == null || startDate == null) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        return "COMING".equals(status) && now.isBefore(startDate);
    }
    
    // Helper method để kiểm tra discount có expired không
    public boolean isExpired() {
        if (endDate == null) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        return now.isAfter(endDate);
    }
}
