package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request sử dụng điểm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointsRedeemRequest {
    private int points;             // Số điểm muốn dùng
    private double orderAmount;     // Giá trị đơn hàng
}
