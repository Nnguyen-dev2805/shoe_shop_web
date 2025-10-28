package com.dev.shoeshop.enums;

/**
 * Lý do trả hàng
 */
public enum ReturnReason {
    WRONG_SIZE("Size không đúng"),
    DEFECTIVE("Lỗi sản phẩm"),
    NOT_AS_DESCRIBED("Không đúng mô tả"),
    WRONG_ITEM("Gửi nhầm hàng"),
    DAMAGED("Hàng bị hỏng"),
    WRONG_PRODUCT("Sai sản phẩm"),
    QUALITY_ISSUE("Vấn đề chất lượng"),
    CHANGE_MIND("Đổi ý"),
    OTHER("Lý do khác");
    
    private final String description;
    
    ReturnReason(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
