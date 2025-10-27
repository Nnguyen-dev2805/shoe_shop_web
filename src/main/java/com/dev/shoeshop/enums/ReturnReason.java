package com.dev.shoeshop.enums;

/**
 * Lý do trả hàng
 */
public enum ReturnReason {
    WRONG_SIZE("Sai size"),
    DEFECTIVE("Lỗi sản phẩm"),
    NOT_AS_DESCRIBED("Không đúng mô tả"),
    WRONG_ITEM("Gửi nhầm hàng"),
    DAMAGED("Hàng bị hư hỏng"),
    CHANGE_MIND("Đổi ý không muốn mua");
    
    private final String description;
    
    ReturnReason(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
