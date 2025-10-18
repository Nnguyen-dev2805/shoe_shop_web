package com.dev.shoeshop.dto.flashsale.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa thông tin Flash Sale cho cart item
 * Dùng để hiển thị giá flash sale trong payment page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemFlashSaleInfo {
    
    /**
     * ProductDetail ID
     */
    private Long productDetailId;
    
    /**
     * Có đang trong Flash Sale không
     */
    private boolean hasFlashSale;
    
    /**
     * Giá gốc (base price + size fee)
     */
    private Double originalPrice;
    
    /**
     * Giá Flash Sale (nếu có)
     */
    private Double flashSalePrice;
    
    /**
     * % giảm giá
     */
    private Double discountPercent;
    
    /**
     * Flash Sale còn lại bao nhiêu sản phẩm
     */
    private Integer remainingStock;
    
    /**
     * Flash Sale Name (để hiển thị)
     */
    private String flashSaleName;
    
    /**
     * Flash Sale end time
     */
    private String endTime;
}
