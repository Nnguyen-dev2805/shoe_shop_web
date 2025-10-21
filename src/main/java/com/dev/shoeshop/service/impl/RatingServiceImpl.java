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
        }
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
        
        summary.put("averageStars", product.getAverage_stars() != null ? product.getAverage_stars() : 0.0);
        summary.put("totalReviewers", product.getTotal_reviewers() != null ? product.getTotal_reviewers() : 0L);
        
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
    
    private RatingResponseDTO convertToDTO(Rating rating) {
        return RatingResponseDTO.builder()
                .id(rating.getId())
                .star(rating.getStar())
                .comment(rating.getComment())
                .image(rating.getImage())
                .createdDate(rating.getCreatedDate())
                .userName(rating.getUser() != null ? rating.getUser().getFullname() : "Khách hàng")
                .userEmail(rating.getUser() != null ? rating.getUser().getEmail() : "")
                .productName(rating.getProduct() != null ? rating.getProduct().getTitle() : "Sản phẩm")
                .productSize(rating.getProductDetail() != null ? String.valueOf(rating.getProductDetail().getSize()) : "N/A")
                .build();
    }
}
