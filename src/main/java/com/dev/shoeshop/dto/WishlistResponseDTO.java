package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho response của Wishlist API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponseDTO {
    
    /**
     * Trạng thái thành công hay thất bại
     */
    private boolean success;
    
    /**
     * Thông báo kết quả
     */
    private String message;
    
    /**
     * Product có trong wishlist hay không (dùng cho toggle)
     */
    private Boolean isInWishlist;
    
    /**
     * Số lượng items trong wishlist
     */
    private Long count;
    
    /**
     * Data object (có thể là wishlist item, product, etc.)
     */
    private Object data;
}
