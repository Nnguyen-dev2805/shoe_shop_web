package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.WishlistResponseDTO;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.entity.WishList;
import com.dev.shoeshop.repository.ProductRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.repository.WishlistRepository;
import com.dev.shoeshop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Implementation của WishlistService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public WishlistResponseDTO toggleWishlist(Long userId, Long productId) {
        log.info("Toggle wishlist for userId: {}, productId: {}", userId, productId);
        
        try {
            // Kiểm tra product có trong wishlist không
            boolean exists = wishlistRepository.existsByUser_IdAndProduct_IdAndIsActive(userId, productId, true);
            
            if (exists) {
                // Nếu có rồi thì xóa (soft delete)
                log.info("Product {} already in wishlist, removing...", productId);
                return removeFromWishlist(userId, productId);
            } else {
                // Nếu chưa có thì thêm
                log.info("Product {} not in wishlist, adding...", productId);
                return addToWishlist(userId, productId);
            }
        } catch (Exception e) {
            log.error("Error toggling wishlist: ", e);
            return WishlistResponseDTO.builder()
                    .success(false)
                    .message("Có lỗi xảy ra khi cập nhật wishlist")
                    .build();
        }
    }

    @Override
    public WishlistResponseDTO addToWishlist(Long userId, Long productId) {
        log.info("Adding product {} to wishlist for user {}", productId, userId);
        
        try {
            // Validate user
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            
            // Validate product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            
            if (product.isDelete()) {
                return WishlistResponseDTO.builder()
                        .success(false)
                        .message("Sản phẩm không còn tồn tại")
                        .build();
            }
            
            // Kiểm tra đã có trong wishlist chưa
            Optional<WishList> existingWishlist = wishlistRepository.findByUser_IdAndProduct_Id(userId, productId);
            
            if (existingWishlist.isPresent()) {
                WishList wishlist = existingWishlist.get();
                if (wishlist.getIsActive()) {
                    log.info("Product {} already in active wishlist", productId);
                    return WishlistResponseDTO.builder()
                            .success(true)
                            .message("Sản phẩm đã có trong danh sách yêu thích")
                            .isInWishlist(true)
                            .count(countWishlistItems(userId))
                            .build();
                } else {
                    // Activate lại nếu đã soft delete
                    log.info("Reactivating wishlist item for product {}", productId);
                    wishlist.activate();
                    wishlistRepository.save(wishlist);
                }
            } else {
                // Tạo mới wishlist
                log.info("Creating new wishlist item for product {}", productId);
                WishList newWishlist = WishList.createWishlist(user, product);
                wishlistRepository.save(newWishlist);
            }
            
            Long count = countWishlistItems(userId);
            Long totalLikes = countProductLikes(productId);
            log.info("Product {} added to wishlist successfully. Total items: {}, Total likes: {}", productId, count, totalLikes);
            
            return WishlistResponseDTO.builder()
                    .success(true)
                    .message("Đã thêm sản phẩm vào danh sách yêu thích")
                    .isInWishlist(true)
                    .count(count)
                    .totalLikes(totalLikes)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error adding to wishlist: ", e);
            return WishlistResponseDTO.builder()
                    .success(false)
                    .message("Có lỗi xảy ra: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public WishlistResponseDTO removeFromWishlist(Long userId, Long productId) {
        log.info("Removing product {} from wishlist for user {}", productId, userId);
        
        try {
            // Kiểm tra có tồn tại không
            boolean exists = wishlistRepository.existsByUser_IdAndProduct_IdAndIsActive(userId, productId, true);
            
            if (!exists) {
                log.info("Product {} not found in wishlist", productId);
                return WishlistResponseDTO.builder()
                        .success(false)
                        .message("Sản phẩm không có trong danh sách yêu thích")
                        .isInWishlist(false)
                        .build();
            }
            
            // Soft delete
            wishlistRepository.softDeleteByUser_IdAndProduct_Id(userId, productId);
            
            Long count = countWishlistItems(userId);
            Long totalLikes = countProductLikes(productId);
            log.info("Product {} removed from wishlist successfully. Remaining items: {}, Total likes: {}", productId, count, totalLikes);
            
            return WishlistResponseDTO.builder()
                    .success(true)
                    .message("Đã xóa sản phẩm khỏi danh sách yêu thích")
                    .isInWishlist(false)
                    .count(count)
                    .totalLikes(totalLikes)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error removing from wishlist: ", e);
            return WishlistResponseDTO.builder()
                    .success(false)
                    .message("Có lỗi xảy ra: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserWishlist(Long userId) {
        log.info("Getting wishlist for user {}", userId);
        
        try {
            List<WishList> wishlists = wishlistRepository.findActiveWishlistByUserId(userId);
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (WishList wishlist : wishlists) {
                Map<String, Object> item = new HashMap<>();
                Product product = wishlist.getProduct();
                
                item.put("wishlistId", wishlist.getId());
                item.put("productId", product.getId());
                item.put("title", product.getTitle());
                item.put("description", product.getDescription());
                item.put("price", product.getPrice());
                item.put("image", product.getImage());
                item.put("averageRating", product.getAverage_rating());
                item.put("totalReviews", product.getTotal_reviews());
                item.put("isDelete", product.isDelete());
                item.put("createdAt", wishlist.getCreatedAt());
                
                // Brand info
                if (product.getBrand() != null) {
                    item.put("brandId", product.getBrand().getId());
                    item.put("brandName", product.getBrand().getName());
                }
                
                // Category info
                if (product.getCategory() != null) {
                    item.put("categoryId", product.getCategory().getId());
                    item.put("categoryName", product.getCategory().getName());
                }
                
                result.add(item);
            }
            
            log.info("Found {} wishlist items for user {}", result.size(), userId);
            return result;
            
        } catch (Exception e) {
            log.error("Error getting wishlist: ", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInWishlist(Long userId, Long productId) {
        log.debug("Checking if product {} is in wishlist for user {}", productId, userId);
        return wishlistRepository.existsByUser_IdAndProduct_IdAndIsActive(userId, productId, true);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countWishlistItems(Long userId) {
        log.debug("Counting wishlist items for user {}", userId);
        return wishlistRepository.countByUser_IdAndIsActive(userId, true);
    }
    
    /**
     * Đếm tổng số người thích product này (total likes)
     * @param productId ID của product
     * @return Số người thích
     */
    @Transactional(readOnly = true)
    public Long countProductLikes(Long productId) {
        log.debug("Counting total likes for product {}", productId);
        return wishlistRepository.countByProduct_IdAndIsActive(productId, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getWishlistProductIds(Long userId) {
        log.debug("Getting wishlist product IDs for user {}", userId);
        return wishlistRepository.findProductIdsByUser_Id(userId);
    }

    @Override
    public WishlistResponseDTO clearWishlist(Long userId) {
        log.info("Clearing wishlist for user {}", userId);
        
        try {
            wishlistRepository.softDeleteAllByUser_Id(userId);
            
            log.info("Wishlist cleared successfully for user {}", userId);
            return WishlistResponseDTO.builder()
                    .success(true)
                    .message("Đã xóa tất cả sản phẩm khỏi danh sách yêu thích")
                    .count(0L)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error clearing wishlist: ", e);
            return WishlistResponseDTO.builder()
                    .success(false)
                    .message("Có lỗi xảy ra khi xóa wishlist")
                    .build();
        }
    }
}
