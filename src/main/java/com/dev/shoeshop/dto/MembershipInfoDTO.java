package com.dev.shoeshop.dto;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.MembershipTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO để trả về thông tin membership của user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipInfoDTO {
    
    // Current tier info
    private String tierName;            // "SILVER", "GOLD", etc.
    private String tierDisplayName;     // "🥈 Bạc", "🥇 Vàng", etc.
    private String tierIcon;            // "🥈", "🥇", etc.
    private String tierColor;           // "#C0C0C0", "#FFD700", etc.
    
    // Points info
    private int currentPoints;          // Số điểm hiện tại
    private double pointsValue;         // Giá trị quy đổi (VNĐ)
    
    // Spending info
    private double totalSpending;       // Tổng chi tiêu tích lũy
    
    // Benefits
    private double earnRate;            // % tích điểm (1.0 = 1%)
    private double shippingDiscount;    // Giảm ship (VNĐ)
    private boolean isFreeShipping;     // Free ship hay không
    private int monthlyVouchers;        // Số voucher/tháng
    private int pointsExpiryMonths;     // Điểm hết hạn sau X tháng
    
    // Next tier info
    private boolean hasNextTier;        // Còn tier cao hơn không
    private String nextTierName;        // Tên tier tiếp theo
    private String nextTierDisplayName; // Display name tier tiếp theo
    private double nextTierMinSpending; // Chi tiêu tối thiểu của tier tiếp theo
    private double remainingToNextTier; // Còn thiếu bao nhiêu để lên hạng
    private int progressPercent;        // % tiến độ (0-100)
    
    /**
     * Factory method để tạo DTO từ Users entity
     */
    public static MembershipInfoDTO fromUser(Users user) {
        if (user == null) {
            return null;
        }
        
        MembershipTier currentTier = user.getMembershipTier();
        MembershipTier nextTier = currentTier.getNextTier();
        
        return MembershipInfoDTO.builder()
                // Current tier
                .tierName(currentTier.name())
                .tierDisplayName(currentTier.getDisplayName())
                .tierIcon(currentTier.getIcon())
                .tierColor(currentTier.getColor())
                
                // Points
                .currentPoints(user.getLoyaltyPoints())
                .pointsValue(user.getPointsValue())
                
                // Spending
                .totalSpending(user.getTotalSpending())
                
                // Benefits
                .earnRate(currentTier.getEarnRate() * 100) // Convert to percentage
                .shippingDiscount(currentTier.getShippingDiscount())
                .isFreeShipping(currentTier.isFreeShipping())
                .monthlyVouchers(currentTier.getMonthlyVouchers())
                .pointsExpiryMonths(currentTier.getPointsExpiryMonths())
                
                // Next tier
                .hasNextTier(nextTier != null)
                .nextTierName(nextTier != null ? nextTier.name() : null)
                .nextTierDisplayName(nextTier != null ? nextTier.getDisplayName() : "MAX")
                .nextTierMinSpending(nextTier != null ? nextTier.getMinSpending() : 0)
                .remainingToNextTier(user.getRemainingToNextTier())
                .progressPercent(user.getProgressToNextTier())
                
                .build();
    }
}
