package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingRequestDTO {
    
    private Long orderId;
    private List<RatingItemDTO> ratings;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RatingItemDTO {
        private Long orderDetailId;
        private Integer star;
        private String comment;
    }
}
