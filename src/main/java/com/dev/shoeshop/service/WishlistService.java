package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.WishlistResponseDTO;
import com.dev.shoeshop.entity.WishList;

import java.util.List;
import java.util.Map;

/**
 * Service interface cho Wishlist
 * Quản lý các business logic liên quan đến wishlist
 */
public interface WishlistService {

    /**
     * Toggle wishlist - Thêm hoặc xóa product khỏi wishlist
     * @param userId ID của user
     * @param productId ID của product
     * @return WishlistResponseDTO với thông tin kết quả
     */
    WishlistResponseDTO toggleWishlist(Long userId, Long productId);

    /**
     * Thêm product vào wishlist
     * @param userId ID của user
     * @param productId ID của product
     * @return WishlistResponseDTO với thông tin kết quả
     */
    WishlistResponseDTO addToWishlist(Long userId, Long productId);

    /**
     * Xóa product khỏi wishlist
     * @param userId ID của user
     * @param productId ID của product
     * @return WishlistResponseDTO với thông tin kết quả
     */
    WishlistResponseDTO removeFromWishlist(Long userId, Long productId);

    /**
     * Lấy danh sách wishlist của user
     * @param userId ID của user
     * @return List<Map<String, Object>> danh sách wishlist items
     */
    List<Map<String, Object>> getUserWishlist(Long userId);

    /**
     * Kiểm tra product có trong wishlist không
     * @param userId ID của user
     * @param productId ID của product
     * @return true nếu có, false nếu không
     */
    boolean isInWishlist(Long userId, Long productId);

    /**
     * Đếm số lượng items trong wishlist
     * @param userId ID của user
     * @return số lượng items
     */
    Long countWishlistItems(Long userId);

    /**
     * Lấy danh sách product IDs trong wishlist
     * @param userId ID của user
     * @return List<Long> danh sách product IDs
     */
    List<Long> getWishlistProductIds(Long userId);

    /**
     * Xóa tất cả items trong wishlist
     * @param userId ID của user
     * @return WishlistResponseDTO với thông tin kết quả
     */
    WishlistResponseDTO clearWishlist(Long userId);
}
