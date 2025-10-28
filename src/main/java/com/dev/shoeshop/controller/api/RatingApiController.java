package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.RatingResponseDTO;
import com.dev.shoeshop.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Product Ratings", description = "APIs for managing product reviews and ratings")
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingApiController {
    
    private final RatingService ratingService;
    
    /**
     * Get all ratings for a specific product
     */
    @Operation(
        summary = "Get product ratings",
        description = "Retrieves all ratings for a product with optional filters (star rating, has comment, has image)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ratings"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getRatingsByProduct(
            @Parameter(description = "Product ID", required = true, example = "1") 
            @PathVariable Long productId,
            @Parameter(description = "Filter by star rating (1-5)", example = "5") 
            @RequestParam(required = false) Integer star,
            @Parameter(description = "Filter ratings with comments") 
            @RequestParam(required = false) Boolean hasComment,
            @Parameter(description = "Filter ratings with images") 
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
    
    /**
     * Update rating statistics for all products (Admin endpoint)
     */
    @Operation(
        summary = "Update rating statistics",
        description = "Recalculates and updates rating statistics for all products (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics updated successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/update-statistics")
    public ResponseEntity<Map<String, Object>> updateAllRatingStatistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int updatedCount = ratingService.updateAllProductRatingStatistics();
            
            response.put("success", true);
            response.put("message", "Đã cập nhật rating statistics thành công");
            response.put("updatedCount", updatedCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error updating rating statistics: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật rating statistics: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
