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

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    
    private final RatingRepository ratingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public void submitRatings(RatingRequestDTO ratingRequest, Long userId) {
        System.out.println("=== RatingServiceImpl.submitRatings ===");
        System.out.println("OrderId: " + ratingRequest.getOrderId());
        System.out.println("UserId: " + userId);
        System.out.println("Ratings count: " + ratingRequest.getRatings().size());
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("User found: " + user.getEmail());
        
        for (RatingRequestDTO.RatingItemDTO ratingItem : ratingRequest.getRatings()) {
            System.out.println("Processing rating for OrderDetailId: " + ratingItem.getOrderDetailId());
            // Get order detail
            OrderDetail orderDetail = orderDetailRepository.findById(ratingItem.getOrderDetailId().intValue())
                    .orElseThrow(() -> new RuntimeException("Order detail not found"));
            System.out.println("OrderDetail found: " + orderDetail.getId());
            
            // Check ProductDetail
            if (orderDetail.getProduct() == null) {
                throw new RuntimeException("ProductDetail not found in OrderDetail");
            }
            System.out.println("ProductDetail found: " + orderDetail.getProduct().getId());
            
            // Check if rating already exists for this order detail
            boolean ratingExists = ratingRepository.existsByOrderDetailAndUser(orderDetail, user);
            System.out.println("Rating exists: " + ratingExists);
            if (ratingExists) {
                throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
            }
            
            // Create new rating - chỉ lưu OrderDetail, ProductDetail sẽ được tự động lấy từ OrderDetail
            Rating rating = Rating.builder()
                    .star(ratingItem.getStar())
                    .comment(ratingItem.getComment())
                    .user(user)
                    .orderDetail(orderDetail)
                    .productDetail(orderDetail.getProduct()) // ProductDetail từ OrderDetail
                    .createdDate(new Date())
                    .modified(new Date())
                    .build();
            
            System.out.println("Rating object created:");
            System.out.println("- Star: " + rating.getStar());
            System.out.println("- Comment: " + rating.getComment());
            System.out.println("- User ID: " + rating.getUser().getId());
            System.out.println("- OrderDetail ID: " + rating.getOrderDetail().getId());
            System.out.println("- ProductDetail ID: " + (rating.getProductDetail() != null ? rating.getProductDetail().getId() : "null"));
            
            System.out.println("Saving rating...");
            ratingRepository.save(rating);
            System.out.println("Rating saved successfully!");
        }
    }
}
