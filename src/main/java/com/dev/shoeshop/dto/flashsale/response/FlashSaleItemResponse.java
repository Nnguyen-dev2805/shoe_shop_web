package com.dev.shoeshop.dto.flashsale.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO Response cho Flash Sale Item
// Dùng để trả về thông tin sản phẩm flash sale cho front end
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleItemResponse {

    private Long id;

    private Long productDetailId;
    
    private Long productId; // ✅ Thêm Product ID để redirect

    private String productName;

    private String productImage;

    private Integer size;

    private Double originalPrice;

    private Double flashSalePrice;

    private Double discountPercent;

    private Integer stock;

    private Integer sold;

    private Integer remaining;
    
    // ✅ Thêm field cho Product-level data
    private Long productSoldQuantity; // Tổng số đã bán của Product (tất cả size)
    
    private Integer totalStock; // Tổng inventory của Product (tất cả size)
    
    // ========== HELPER METHODS ==========
    
    /**
     * Tính phần trăm đã bán
     * 
     * @return % đã bán (0-100)
     * 
     * Dùng cho: Progress bar stock trên UI
     */
    public double getSoldPercentage() {
        if (stock == null || stock == 0) return 0;
        if (sold == null) return 0;
        return (sold.doubleValue() / stock.doubleValue()) * 100;
    }
    
    /**
     * Kiểm tra còn hàng không
     * 
     * @return true nếu remaining > 0
     * 
     * Dùng cho: Enable/disable nút "MUA NGAY"
     */
    public boolean isAvailable() {
        return remaining != null && remaining > 0;
    }
    
    /**
     * Tính số tiền tiết kiệm
     * 
     * @return Số tiền tiết kiệm (originalPrice - flashSalePrice)
     * 
     * Dùng cho: Hiển thị "Tiết kiệm XXXđ"
     */
    public Double getSavings() {
        if (originalPrice == null || flashSalePrice == null) return 0.0;
        return originalPrice - flashSalePrice;
    }
}
