package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho response validate/redeem points
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointsRedeemResponse {
    private boolean canRedeem;          // Có thể dùng điểm không
    private String message;             // Thông báo (nếu không thể dùng)
    private int pointsToRedeem;         // Số điểm sẽ dùng
    private double discountAmount;      // Số tiền được giảm (VNĐ)
    private int remainingPoints;        // Số điểm còn lại sau khi dùng
    private double finalOrderAmount;    // Giá đơn hàng sau khi giảm
}
