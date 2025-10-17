package com.dev.shoeshop.enums;

/**
 * Enum cho loại voucher
 */
public enum VoucherType {
    /**
     * Voucher giảm giá đơn hàng (giảm % trên tổng tiền hàng)
     */
    ORDER_DISCOUNT,
    
    /**
     * Voucher giảm phí vận chuyển
     */
    SHIPPING_DISCOUNT;
    
    /**
     * Check if this is an order discount voucher
     */
    public boolean isOrderDiscount() {
        return this == ORDER_DISCOUNT;
    }
    
    /**
     * Check if this is a shipping discount voucher
     */
    public boolean isShippingDiscount() {
        return this == SHIPPING_DISCOUNT;
    }
}
