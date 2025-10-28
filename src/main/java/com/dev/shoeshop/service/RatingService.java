package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.RatingRequestDTO;
import com.dev.shoeshop.dto.RatingResponseDTO;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.Rating;
import com.dev.shoeshop.entity.Users;

import java.util.List;

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
    
    /**
     * Get all ratings for a specific product
     * @param productId Product ID
     * @return List of ratings ordered by created date descending
     */
    List<Rating> getRatingsByProductId(Long productId);
    
    /**
     * Get all ratings for a specific product as DTOs
     * @param productId Product ID
     * @return List of rating DTOs ordered by created date descending
     */
    List<RatingResponseDTO> getRatingDTOsByProductId(Long productId);
    
    /**
     * Get product rating summary
     * @param productId Product ID
     * @return Map containing average stars and total reviewers
     */
    java.util.Map<String, Object> getProductRatingSummary(Long productId);
    
    /**
     * Get rating statistics by star level
     * @param productId Product ID
     * @return Map containing statistics
     */
    java.util.Map<String, Object> getRatingStatistics(Long productId);
    
    /**
     * Get filtered ratings by criteria
     */
    List<RatingResponseDTO> getFilteredRatings(Long productId, Integer starFilter, Boolean hasComment, Boolean hasImage);
    
    /**
     * Update rating statistics for all products that have ratings
     * @return Number of products updated
     */
    int updateAllProductRatingStatistics();
    
    /**
     * Get random ratings for homepage testimonials
     * Always returns exactly 6 ratings (repeats if less than 6 available)
     * @return List of 6 rating DTOs
     */
    List<RatingResponseDTO> getRandomRatingsForHomepage();
}
