package com.dev.shoeshop.dto.flashsale.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// DTO Response cho Flash Sale
// Dùng để trả về thông tin flash sale cho frontend
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleResponse {

    private Long id;

    private String name;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private Integer totalItems;

    private Integer totalSold;

    private String bannerImage;

    private List<FlashSaleItemResponse> items = new ArrayList<>();
    
    // ========== HELPER METHODS ==========
    
    /**
     * Kiểm tra flash sale có đang active không
     * 
     * @return true nếu status = ACTIVE
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    /**
     * Tính phần trăm đã bán
     * 
     * @return % đã bán (0-100)
     * 
     * Dùng cho: Progress bar trên UI
     */
    public double getSoldPercentage() {
        if (totalItems == null || totalItems == 0) return 0;
        if (totalSold == null) return 0;
        return (totalSold.doubleValue() / totalItems.doubleValue()) * 100;
    }
    
    /**
     * Tính số sản phẩm còn lại
     * 
     * @return Số lượng còn lại
     */
    public int getRemainingItems() {
        if (totalItems == null) return 0;
        if (totalSold == null) return totalItems;
        return Math.max(0, totalItems - totalSold);
    }
}
