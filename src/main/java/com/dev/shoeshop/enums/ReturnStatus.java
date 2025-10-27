package com.dev.shoeshop.enums;

/**
 * Trạng thái yêu cầu trả hàng
 */
public enum ReturnStatus {
    PENDING("Chờ xác nhận"),
    APPROVED("Đã chấp nhận"),
    REJECTED("Đã từ chối"),
    SHIPPING("Đang gửi về shop"),
    RECEIVED("Shop đã nhận hàng"),
    REFUNDED("Đã hoàn xu");
    
    private final String description;
    
    ReturnStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
