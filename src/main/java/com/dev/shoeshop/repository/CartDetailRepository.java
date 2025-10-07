package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Cart;
import com.dev.shoeshop.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    
    /**
     * Find all cart details by cart
     */
    List<CartDetail> findByCart(Cart cart);
    
    /**
     * Find cart detail by ID and user ID (for security)
     */
    @Query("SELECT cd FROM CartDetail cd WHERE cd.id = :detailId AND cd.cart.user.id = :userId")
    Optional<CartDetail> findByIdAndUserId(@Param("detailId") Long detailId, @Param("userId") Long userId);
    
    /**
     * Update quantity of cart detail
     */
    @Modifying
    @Query("UPDATE CartDetail cd SET cd.quantity = :quantity WHERE cd.id = :detailId AND cd.cart.user.id = :userId")
    Long updateQuantity(@Param("detailId") Long detailId, @Param("quantity") Long quantity, @Param("userId") Long userId);
    
    /**
     * Delete cart detail by ID and user ID (for security)
     */
    @Modifying
    @Query("DELETE FROM CartDetail cd WHERE cd.id = :detailId AND cd.cart.user.id = :userId")
    Long deleteByIdAndUserId(@Param("detailId") Long detailId, @Param("userId") Long userId);
    
    /**
     * Count items in cart
     */
    @Query("SELECT COUNT(cd) FROM CartDetail cd WHERE cd.cart = :cart")
    long countByCart(@Param("cart") Cart cart);
}
