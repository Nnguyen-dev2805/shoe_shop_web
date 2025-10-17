package com.dev.shoeshop.enums;

/**
 * Enum cho kiểu giảm giá
 */
public enum DiscountValueType {
    /**
     * Giảm theo phần trăm (%)
     * Ví dụ: Giảm 10% đơn hàng, Giảm 50% phí ship
     */
    PERCENTAGE,
    
    /**
     * Giảm theo số tiền cố định (VNĐ)
     * Ví dụ: Giảm 50.000đ đơn hàng, Miễn phí ship 30.000đ
     */
    FIXED_AMOUNT;
    
    /**
     * Check if this is percentage discount
     */
    public boolean isPercentage() {
        return this == PERCENTAGE;
    }
    
    /**
     * Check if this is fixed amount discount
     */
    public boolean isFixedAmount() {
        return this == FIXED_AMOUNT;
    }
}
