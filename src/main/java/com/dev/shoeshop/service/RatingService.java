package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.RatingRequestDTO;

public interface RatingService {
    
    /**
     * Submit ratings for products in an order
     * @param ratingRequest Rating request containing order ID and ratings
     * @param userId User ID who is submitting the ratings
     */
    void submitRatings(RatingRequestDTO ratingRequest, Long userId);
}
