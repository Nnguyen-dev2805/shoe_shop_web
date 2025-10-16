package com.dev.shoeshop.dto.flashsale.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 DTO Response cho stock info (thông tin tồn kho)
 Dùng cho AJAX polling - update stock real-time
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    
    /** Tổng stock dành cho flash sale */
    private Integer stock;

    /** Số lượng đã bán */
    private Integer sold;

    /** Số lượng còn lại */
    private Integer remaining;
    
    /** Phần trăm đã bán (0-100) */
    private Double soldPercentage;
    
    /**
     * Kiểm tra còn hàng không
     * 
     * @return true nếu remaining > 0
     */
    public boolean hasStock() {
        return remaining != null && remaining > 0;
    }
}
