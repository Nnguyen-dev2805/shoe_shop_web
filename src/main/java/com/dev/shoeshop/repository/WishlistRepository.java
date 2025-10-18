package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho WishList entity
 * Quản lý các thao tác CRUD và query cho wishlist
 */
@Repository
public interface WishlistRepository extends JpaRepository<WishList, Long> {

    /**
     * Tìm wishlist theo user ID và product ID
     * @param userId ID của user
     * @param productId ID của product
     * @return Optional<WishList>
     */
    Optional<WishList> findByUser_IdAndProduct_Id(Long userId, Long productId);

    /**
     * Lấy danh sách wishlist của user (chỉ active items)
     * Sắp xếp theo ngày tạo mới nhất
     * @param userId ID của user
     * @return List<WishList>
     */
    @Query("SELECT w FROM WishList w " +
           "JOIN FETCH w.product p " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.category " +
           "WHERE w.user.id = :userId AND w.isActive = true " +
           "ORDER BY w.createdAt DESC")
    List<WishList> findActiveWishlistByUserId(@Param("userId") Long userId);

    /**
     * Đếm số lượng wishlist items của user (chỉ active)
     * @param userId ID của user
     * @return số lượng items
     */
    Long countByUser_IdAndIsActive(Long userId, Boolean isActive);
    
    /**
     * Đếm tổng số người thích product này (total likes)
     * @param productId ID của product
     * @param isActive trạng thái active
     * @return số người thích
     */
    Long countByProduct_IdAndIsActive(Long productId, Boolean isActive);

    /**
     * Kiểm tra product có trong wishlist của user không
     * @param userId ID của user
     * @param productId ID của product
     * @param isActive trạng thái active
     * @return true nếu có, false nếu không
     */
    boolean existsByUser_IdAndProduct_IdAndIsActive(Long userId, Long productId, Boolean isActive);

    /**
     * Xóa wishlist item (soft delete - set isActive = false)
     * @param userId ID của user
     * @param productId ID của product
     */
    @Modifying
    @Query("UPDATE WishList w SET w.isActive = false WHERE w.user.id = :userId AND w.product.id = :productId")
    void softDeleteByUser_IdAndProduct_Id(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * Xóa tất cả wishlist items của user (soft delete)
     * @param userId ID của user
     */
    @Modifying
    @Query("UPDATE WishList w SET w.isActive = false WHERE w.user.id = :userId")
    void softDeleteAllByUser_Id(@Param("userId") Long userId);

    /**
     * Lấy danh sách product IDs trong wishlist của user
     * @param userId ID của user
     * @return List<Long> danh sách product IDs
     */
    @Query("SELECT w.product.id FROM WishList w WHERE w.user.id = :userId AND w.isActive = true")
    List<Long> findProductIdsByUser_Id(@Param("userId") Long userId);
}
