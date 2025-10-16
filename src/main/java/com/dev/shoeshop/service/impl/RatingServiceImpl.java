package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.RatingRequestDTO;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.Rating;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.OrderDetailRepository;
import com.dev.shoeshop.repository.RatingRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    
    private final RatingRepository ratingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    
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
                             ", Comment: " + savedRating.getComment());
        }
    }
    
    @Override
    public boolean hasRating(OrderDetail orderDetail, Users user) {
        return ratingRepository.existsByOrderDetailAndUser(orderDetail, user);
    }
}
