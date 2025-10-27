package com.dev.shoeshop.enums;

/**
 * Loại giao dịch xu
 */
public enum CoinTransactionType {
    REFUND("Hoàn xu từ trả hàng"),
    SPEND("Sử dụng xu thanh toán"),
    EARN("Nhận xu từ khuyến mãi"),
    ADMIN_ADJUST("Admin điều chỉnh");
    
    private final String description;
    
    CoinTransactionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
