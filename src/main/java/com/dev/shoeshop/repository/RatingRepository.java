package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.Rating;
import com.dev.shoeshop.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    /**
     * Check if a rating already exists for a specific order detail and user
     */
    boolean existsByOrderDetailAndUser(OrderDetail orderDetail, Users user);
    
    /**
     * Find all ratings for a specific product
     */
    List<Rating> findByProductOrderByCreatedDateDesc(Product product);
    
    /**
     * Find all ratings for a specific product ID
     */
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId ORDER BY r.createdDate DESC")
    List<Rating> findByProductIdOrderByCreatedDateDesc(@Param("productId") Long productId);
    
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.star = :star ORDER BY r.createdDate DESC")
    List<Rating> findByProductIdAndStarOrderByCreatedDateDesc(@Param("productId") Long productId, @Param("star") Integer star);
    
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.comment IS NOT NULL AND r.comment != '' ORDER BY r.createdDate DESC")
    List<Rating> findByProductIdWithCommentOrderByCreatedDateDesc(@Param("productId") Long productId);
    
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.image IS NOT NULL AND r.image != '' ORDER BY r.createdDate DESC")
    List<Rating> findByProductIdWithImageOrderByCreatedDateDesc(@Param("productId") Long productId);
    
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.star = :star AND r.comment IS NOT NULL AND r.comment != '' ORDER BY r.createdDate DESC")
    List<Rating> findByProductIdAndStarWithCommentOrderByCreatedDateDesc(@Param("productId") Long productId, @Param("star") Integer star);
    
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.star = :star AND r.image IS NOT NULL AND r.image != '' ORDER BY r.createdDate DESC")
    List<Rating> findByProductIdAndStarWithImageOrderByCreatedDateDesc(@Param("productId") Long productId, @Param("star") Integer star);
    
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.comment IS NOT NULL AND r.comment != '' AND r.image IS NOT NULL AND r.image != '' ORDER BY r.createdDate DESC")
    List<Rating> findByProductIdWithCommentAndImageOrderByCreatedDateDesc(@Param("productId") Long productId);
    
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.star = :star AND r.comment IS NOT NULL AND r.comment != '' AND r.image IS NOT NULL AND r.image != '' ORDER BY r.createdDate DESC")
    List<Rating> findByProductIdAndStarWithCommentAndImageOrderByCreatedDateDesc(@Param("productId") Long productId, @Param("star") Integer star);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product.id = :productId AND r.star = :star")
    Long countByProductIdAndStar(@Param("productId") Long productId, @Param("star") Integer star);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product.id = :productId AND r.comment IS NOT NULL AND r.comment != ''")
    Long countByProductIdWithComment(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product.id = :productId AND r.image IS NOT NULL AND r.image != ''")
    Long countByProductIdWithImage(@Param("productId") Long productId);
}
