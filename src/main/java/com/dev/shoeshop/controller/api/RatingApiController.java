package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.RatingResponseDTO;
import com.dev.shoeshop.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingApiController {
    
    private final RatingService ratingService;
    
    /**
     * Get all ratings for a specific product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getRatingsByProduct(
            @PathVariable Long productId,
            @RequestParam(required = false) Integer star,
            @RequestParam(required = false) Boolean hasComment,
            @RequestParam(required = false) Boolean hasImage) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== Getting ratings for product ID: " + productId + " ===");
            System.out.println("Filters - Star: " + star + ", HasComment: " + hasComment + ", HasImage: " + hasImage);
            
            List<RatingResponseDTO> ratings;
            if (star != null || hasComment != null || hasImage != null) {
                ratings = ratingService.getFilteredRatings(productId, star, hasComment, hasImage);
            } else {
                ratings = ratingService.getRatingDTOsByProductId(productId);
            }
            
            Map<String, Object> summary = ratingService.getProductRatingSummary(productId);
            Map<String, Object> statistics = ratingService.getRatingStatistics(productId);
            
            System.out.println("Found " + ratings.size() + " ratings");
            
            response.put("success", true);
            response.put("ratings", ratings);
            response.put("total", ratings.size());
            response.put("summary", summary);
            response.put("statistics", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting ratings: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy danh sách đánh giá: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
