package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.Rating;
import com.dev.shoeshop.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    /**
     * Check if a rating already exists for a specific order detail and user
     */
    boolean existsByOrderDetailAndUser(OrderDetail orderDetail, Users user);
}
