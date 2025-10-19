package com.dev.shoeshop.enums;

public enum DiscountValueType {
    // Giảm theo phần trăm (%)
    PERCENTAGE,
    // Giảm theo số tiền cố định
    FIXED_AMOUNT;
    public boolean isPercentage() {
        return this == PERCENTAGE;
    }
    public boolean isFixedAmount() {
        return this == FIXED_AMOUNT;
    }
}
