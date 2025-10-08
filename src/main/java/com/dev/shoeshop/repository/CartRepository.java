package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Cart;
import com.dev.shoeshop.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * Find active cart by user ID
     */
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId ORDER BY c.createdDate DESC")
    Optional<Cart> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find cart by user entity
     */
    Optional<Cart> findByUser(Users user);
    
    /**
     * Check if user has active cart
     */
    boolean existsByUser(Users user);
    
    /**
     * Get latest cart by user
     */
    @Query("SELECT c FROM Cart c WHERE c.user = :user ORDER BY c.createdDate DESC")
    Optional<Cart> findLatestCartByUser(@Param("user") Users user);
}
