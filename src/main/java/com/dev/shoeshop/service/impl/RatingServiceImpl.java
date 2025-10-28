package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.RatingRequestDTO;
import com.dev.shoeshop.dto.RatingResponseDTO;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.Rating;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.OrderDetailRepository;
import com.dev.shoeshop.repository.ProductRepository;
import com.dev.shoeshop.repository.RatingRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    
    private final RatingRepository ratingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    @Override
    @Transactional
    public void submitRatings(RatingRequestDTO ratingRequest, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("=== Submitting ratings for user: " + user.getId() + " ===");
        
        for (RatingRequestDTO.RatingItemDTO ratingItem : ratingRequest.getRatings()) {
            // Validate rating data
            if (ratingItem.getStar() == null || ratingItem.getStar() < 1 || ratingItem.getStar() > 5) {
                throw new RuntimeException("Số sao đánh giá phải từ 1 đến 5");
            }
            
            if (ratingItem.getOrderDetailId() == null) {
                throw new RuntimeException("Order detail ID không được để trống");
            }
            
            // Get order detail
            OrderDetail orderDetail = orderDetailRepository.findById(ratingItem.getOrderDetailId().intValue())
                    .orElseThrow(() -> new RuntimeException("Order detail not found"));
            
            System.out.println("Processing rating for OrderDetail ID: " + orderDetail.getId() + 
                             ", Product: " + orderDetail.getProduct().getProduct().getTitle() + 
                             ", Size: " + orderDetail.getProduct().getSize());
            
            // Check if rating already exists for this order detail
            boolean ratingExists = ratingRepository.existsByOrderDetailAndUser(orderDetail, user);
            if (ratingExists) {
                throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
            }
            
            // Create new rating
            Rating rating = Rating.builder()
                    .star(ratingItem.getStar())
                    .comment(ratingItem.getComment())
                    .image(ratingItem.getImage()) // Lưu đường dẫn hình ảnh
                    .user(user)
                    .orderDetail(orderDetail)
                    .productDetail(orderDetail.getProduct())
                    .product(orderDetail.getProduct().getProduct()) // Set product từ ProductDetail
                    .build(); // Bỏ createdDate và modified vì đã có @CreationTimestamp và @UpdateTimestamp
            
            // Save rating to database
            Rating savedRating = ratingRepository.save(rating);
            System.out.println("✅ Rating saved successfully with ID: " + savedRating.getId() + 
                             ", Star: " + savedRating.getStar() + 
                             ", Comment: " + savedRating.getComment() + 
                             ", Image: " + savedRating.getImage());
            
            // ✅ Update Product statistics after saving rating
            Long productId = orderDetail.getProduct().getProduct().getId();
            System.out.println("🔄 Updating statistics for Product ID: " + productId);
            updateProductRatingStatistics(productId);
            
            // Flush to ensure database is updated immediately
            productRepository.flush();
            System.out.println("💾 Database flushed - Product statistics committed");
        }
    }
    
    /**
     * Update Product rating statistics (average rating, total reviews)
     * Note: This method is called from @Transactional methods, so no need to annotate it again
     */
    private void updateProductRatingStatistics(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        List<Rating> allRatings = ratingRepository.findByProductIdOrderByCreatedDateDesc(productId);
        
        if (allRatings.isEmpty()) {
            // ✅ Primary fields
            product.setAverage_rating(0.0);
            product.setTotal_reviews(0L);
            product.setTotal_stars(0L);
            
            // Deprecated fields (for backward compatibility)
            product.setAverage_stars(0.0);
            product.setTotal_reviewers(0L);
        } else {
            // Calculate total reviews
            long totalReviews = allRatings.size();
            
            // Calculate average rating
            double averageRating = allRatings.stream()
                    .mapToInt(Rating::getStar)
                    .average()
                    .orElse(0.0);
            
            // Calculate total stars
            long totalStars = allRatings.stream()
                    .mapToInt(Rating::getStar)
                    .sum();
            
            // ✅ Update Product fields (primary: average_rating, total_reviews)
            product.setAverage_rating(averageRating);
            product.setTotal_reviews(totalReviews);
            product.setTotal_stars(totalStars);
            
            // Also update deprecated fields for backward compatibility
            product.setAverage_stars(averageRating);
            product.setTotal_reviewers(totalReviews);
            
            System.out.println("📊 Calculated Stats:");
            System.out.println("  Average Rating: " + averageRating);
            System.out.println("  Total Reviews: " + totalReviews);
            System.out.println("  Total Stars: " + totalStars);
        }
        
        // Save to database
        Product savedProduct = productRepository.save(product);
        
        System.out.println("✅ Updated Product ID " + productId + ":");
        System.out.println("  ⭐ average_rating: " + savedProduct.getAverage_rating() + " (PRIMARY)");
        System.out.println("  📊 total_reviews: " + savedProduct.getTotal_reviews() + " (PRIMARY)");
        System.out.println("  📦 total_stars: " + savedProduct.getTotal_stars());
    }
    
    @Override
    public boolean hasRating(OrderDetail orderDetail, Users user) {
        return ratingRepository.existsByOrderDetailAndUser(orderDetail, user);
    }
    
    @Override
    public List<Rating> getRatingsByProductId(Long productId) {
        return ratingRepository.findByProductIdOrderByCreatedDateDesc(productId);
    }
    
    @Override
    public List<RatingResponseDTO> getRatingDTOsByProductId(Long productId) {
        List<Rating> ratings = ratingRepository.findByProductIdOrderByCreatedDateDesc(productId);
        return ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getProductRatingSummary(Long productId) {
        Map<String, Object> summary = new HashMap<>();
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // ✅ Chỉ dùng average_rating và total_reviews (không dùng average_stars, total_reviewers)
        Double averageRating = product.getAverage_rating() != null ? product.getAverage_rating() : 0.0;
        Long totalReviews = product.getTotal_reviews() != null ? product.getTotal_reviews() : 0L;
        
        System.out.println("=== Product Rating Summary (from average_rating) ===");
        System.out.println("Product ID: " + productId);
        System.out.println("Average Rating: " + averageRating);
        System.out.println("Total Reviews: " + totalReviews);
        
        summary.put("averageStars", averageRating);
        summary.put("totalReviewers", totalReviews);
        
        return summary;
    }
    
    @Override
    public Map<String, Object> getRatingStatistics(Long productId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // Count ratings by star level
        for (int star = 1; star <= 5; star++) {
            Long count = ratingRepository.countByProductIdAndStar(productId, star);
            statistics.put("star" + star, count != null ? count : 0L);
        }
        
        // Count ratings with comments
        Long commentCount = ratingRepository.countByProductIdWithComment(productId);
        statistics.put("withComment", commentCount != null ? commentCount : 0L);
        
        // Count ratings with images
        Long imageCount = ratingRepository.countByProductIdWithImage(productId);
        statistics.put("withImage", imageCount != null ? imageCount : 0L);
        
        return statistics;
    }
    
    @Override
    public List<RatingResponseDTO> getFilteredRatings(Long productId, Integer starFilter, Boolean hasComment, Boolean hasImage) {
        List<Rating> ratings;
        
        if (starFilter != null && hasComment != null && hasImage != null) {
            // All filters applied
            if (hasComment && hasImage) {
                ratings = ratingRepository.findByProductIdAndStarWithCommentAndImageOrderByCreatedDateDesc(productId, starFilter);
            } else if (hasComment) {
                ratings = ratingRepository.findByProductIdAndStarWithCommentOrderByCreatedDateDesc(productId, starFilter);
            } else if (hasImage) {
                ratings = ratingRepository.findByProductIdAndStarWithImageOrderByCreatedDateDesc(productId, starFilter);
            } else {
                ratings = ratingRepository.findByProductIdAndStarOrderByCreatedDateDesc(productId, starFilter);
            }
        } else if (starFilter != null) {
            // Only star filter
            ratings = ratingRepository.findByProductIdAndStarOrderByCreatedDateDesc(productId, starFilter);
        } else if (hasComment != null && hasImage != null) {
            // Comment and image filters
            if (hasComment && hasImage) {
                ratings = ratingRepository.findByProductIdWithCommentAndImageOrderByCreatedDateDesc(productId);
            } else if (hasComment) {
                ratings = ratingRepository.findByProductIdWithCommentOrderByCreatedDateDesc(productId);
            } else if (hasImage) {
                ratings = ratingRepository.findByProductIdWithImageOrderByCreatedDateDesc(productId);
            } else {
                ratings = ratingRepository.findByProductIdOrderByCreatedDateDesc(productId);
            }
        } else if (hasComment != null) {
            // Only comment filter
            if (hasComment) {
                ratings = ratingRepository.findByProductIdWithCommentOrderByCreatedDateDesc(productId);
            } else {
                ratings = ratingRepository.findByProductIdOrderByCreatedDateDesc(productId);
            }
        } else if (hasImage != null) {
            // Only image filter
            if (hasImage) {
                ratings = ratingRepository.findByProductIdWithImageOrderByCreatedDateDesc(productId);
            } else {
                ratings = ratingRepository.findByProductIdOrderByCreatedDateDesc(productId);
            }
        } else {
            // No filters
            ratings = ratingRepository.findByProductIdOrderByCreatedDateDesc(productId);
        }
        
        return ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public int updateAllProductRatingStatistics() {
        // Lấy danh sách tất cả product IDs có rating
        List<Long> productIds = ratingRepository.findAll().stream()
                .map(rating -> rating.getProduct().getId())
                .distinct()
                .collect(Collectors.toList());
        
        System.out.println("🔄 Updating rating statistics for " + productIds.size() + " products...");
        
        int updatedCount = 0;
        for (Long productId : productIds) {
            try {
                updateProductRatingStatistics(productId);
                updatedCount++;
            } catch (Exception e) {
                System.err.println("❌ Error updating product " + productId + ": " + e.getMessage());
            }
        }
        
        System.out.println("✅ Successfully updated " + updatedCount + " products");
        return updatedCount;
    }
    
    @Override
    public List<RatingResponseDTO> getRandomRatingsForHomepage() {
        // Get all ratings with 4 or 5 stars and has comment
        List<Rating> allGoodRatings = ratingRepository.findAll().stream()
                .filter(r -> r.getStar() >= 4 && r.getComment() != null && !r.getComment().trim().isEmpty())
                .sorted((r1, r2) -> r2.getCreatedDate().compareTo(r1.getCreatedDate())) // Newest first
                .collect(java.util.stream.Collectors.toList());
        
        List<RatingResponseDTO> result = new java.util.ArrayList<>();
        
        if (allGoodRatings.isEmpty()) {
            // No ratings - return empty list (frontend will show default)
            return result;
        }
        
        // If less than 6 ratings, repeat them to make 6
        while (result.size() < 6) {
            for (Rating rating : allGoodRatings) {
                if (result.size() >= 6) break;
                result.add(convertToDTO(rating));
            }
        }
        
        return result;
    }
    
    private RatingResponseDTO convertToDTO(Rating rating) {
        return RatingResponseDTO.builder()
                .id(rating.getId())
                .star(rating.getStar())
                .comment(rating.getComment())
                .image(rating.getImage())
                .createdDate(rating.getCreatedDate())
                .userName(rating.getUser() != null ? rating.getUser().getFullname() : "Khách hàng")
                .userEmail(rating.getUser() != null ? rating.getUser().getEmail() : "")
                .userAvatar(rating.getUser() != null ? rating.getUser().getProfilePicture() : null)
                .productName(rating.getProduct() != null ? rating.getProduct().getTitle() : "Sản phẩm")
                .productSize(rating.getProductDetail() != null ? String.valueOf(rating.getProductDetail().getSize()) : "N/A")
                .build();
    }
}
