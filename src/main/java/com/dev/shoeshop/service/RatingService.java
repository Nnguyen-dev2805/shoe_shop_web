package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.RatingRequestDTO;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.Users;

public interface RatingService {
    
    /**
     * Submit ratings for products in an order
     * @param ratingRequest Rating request containing order ID and ratings
     * @param userId User ID who is submitting the ratings
     */
    void submitRatings(RatingRequestDTO ratingRequest, Long userId);
    
    /**
     * Check if a rating already exists for a specific order detail and user
     * @param orderDetail OrderDetail to check
     * @param user User to check
     * @return true if rating exists, false otherwise
     */
    boolean hasRating(OrderDetail orderDetail, Users user);
}
