package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO {
    private Long id;
    private int star;
    private String comment;
    private String image;
    private Date createdDate;
    private String userName;
    private String userEmail;
    private String userAvatar;  // User avatar from database
    private String productName;
    private String productSize;
    private Double averageStars;
    private Long totalReviewers;
}
