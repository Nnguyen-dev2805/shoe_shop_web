package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.MembershipInfoDTO;
import com.dev.shoeshop.dto.PointsRedeemRequest;
import com.dev.shoeshop.dto.PointsRedeemResponse;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.MembershipTier;
import com.dev.shoeshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service xử lý logic membership và loyalty points
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipService {

    private final UserRepository userRepository;

    // ==================== MEMBERSHIP INFO ====================

    /**
     * Lấy thông tin membership của user
     */
    public MembershipInfoDTO getMembershipInfo(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return MembershipInfoDTO.fromUser(user);
    }

    /**
     * Lấy thông tin membership theo userId
     */
    public MembershipInfoDTO getMembershipInfo(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getMembershipInfo(user);
    }

    // ==================== POINTS CALCULATION ====================

    /**
     * Tính điểm earned cho đơn hàng
     * @param orderAmount Giá trị đơn hàng (sau khi trừ điểm nếu có)
     * @param tier Hạng membership hiện tại
     * @return Số điểm được tích
     */
    public int calculatePointsEarned(double orderAmount, MembershipTier tier) {
        if (orderAmount <= 0) {
            return 0;
        }
        return tier.calculatePointsEarned(orderAmount);
    }

    /**
     * Tính điểm earned cho user (dựa trên tier hiện tại)
     */
    public int calculatePointsEarned(double orderAmount, Users user) {
        return calculatePointsEarned(orderAmount, user.getMembershipTier());
    }

    // ==================== EARN POINTS ====================

    /**
     * Tích điểm từ đơn hàng (chỉ gọi khi order completed/delivered)
     * @param user User nhận điểm
     * @param order Đơn hàng
     * @return Số điểm đã tích
     */
    @Transactional
    public int earnPointsFromOrder(Users user, Order order) {
        if (user == null || order == null) {
            throw new IllegalArgumentException("User and Order cannot be null");
        }

        // Tính điểm dựa trên giá sau khi trừ points (nếu có)
        double amountForPoints = order.getFinalPriceAfterPoints();
        int points = calculatePointsEarned(amountForPoints, user);

        if (points > 0) {
            // Cộng điểm cho user
            user.addPoints(points);
            
            // Lưu vào order
            order.setPointsEarned(points);
            
            log.info("User {} earned {} points from order {} (amount: {})", 
                     user.getId(), points, order.getId(), amountForPoints);
        }

        return points;
    }

    // ==================== TIER UPGRADE ====================

    /**
     * Cập nhật tổng chi tiêu và check upgrade tier
     * @param user User cần update
     * @param orderAmount Giá trị đơn hàng
     * @return true nếu đã upgrade tier
     */
    @Transactional
    public boolean updateSpendingAndCheckUpgrade(Users user, double orderAmount) {
        if (user == null || orderAmount <= 0) {
            return false;
        }

        // Cộng vào tổng chi tiêu
        user.addSpending(orderAmount);

        // Check và upgrade tier nếu đủ điều kiện
        boolean upgraded = user.upgradeToEligibleTier();

        if (upgraded) {
            log.info("User {} upgraded to tier {} (total spending: {})", 
                     user.getId(), user.getMembershipTier(), user.getTotalSpending());
        }

        return upgraded;
    }

    // ==================== REDEEM POINTS ====================

    /**
     * Validate xem có thể redeem points không
     */
    public PointsRedeemResponse validateRedeemPoints(Users user, PointsRedeemRequest request) {
        int points = request.getPoints();
        double orderAmount = request.getOrderAmount();

        // Validation
        if (points <= 0) {
            return PointsRedeemResponse.builder()
                    .canRedeem(false)
                    .message("Số điểm phải lớn hơn 0")
                    .build();
        }

        if (orderAmount < 100_000) {
            return PointsRedeemResponse.builder()
                    .canRedeem(false)
                    .message("Đơn hàng tối thiểu 100,000đ mới được dùng điểm")
                    .build();
        }

        if (points > user.getLoyaltyPoints()) {
            return PointsRedeemResponse.builder()
                    .canRedeem(false)
                    .message("Bạn không đủ điểm (có " + user.getLoyaltyPoints() + " điểm)")
                    .build();
        }

        // Check tối đa 50% giá trị đơn
        int maxPoints = MembershipTier.getMaxRedeemablePoints(orderAmount);
        if (points > maxPoints) {
            return PointsRedeemResponse.builder()
                    .canRedeem(false)
                    .message("Chỉ được dùng tối đa " + maxPoints + " điểm (50% giá trị đơn)")
                    .build();
        }

        // Valid!
        double discount = MembershipTier.pointsToMoney(points);
        return PointsRedeemResponse.builder()
                .canRedeem(true)
                .message("OK")
                .pointsToRedeem(points)
                .discountAmount(discount)
                .remainingPoints(user.getLoyaltyPoints() - points)
                .finalOrderAmount(orderAmount - discount)
                .build();
    }

    /**
     * Sử dụng điểm cho đơn hàng (chỉ validate, không save vào DB)
     * Gọi method này TRƯỚC KHI tạo order
     */
    public PointsRedeemResponse redeemPoints(Users user, PointsRedeemRequest request) {
        PointsRedeemResponse response = validateRedeemPoints(user, request);
        
        if (!response.isCanRedeem()) {
            throw new RuntimeException(response.getMessage());
        }

        return response;
    }

    /**
     * Apply điểm vào order và trừ điểm từ user
     * Gọi method này SAU KHI tạo order, TRƯỚC KHI save
     * 
     * NOTE: Frontend đã tính finalTotalPrice (bao gồm trừ điểm), 
     * nên backend CHỈ cần trừ điểm từ user và lưu pointsRedeemed.
     * KHÔNG ghi đè order.totalPrice để tránh trừ 2 lần!
     */
    @Transactional
    public void applyPointsToOrder(Users user, Order order, int points) {
        if (points <= 0) {
            return;
        }

        // Check if user has enough points
        if (user.getLoyaltyPoints() < points) {
            throw new RuntimeException("Không đủ điểm (có " + user.getLoyaltyPoints() + " điểm)");
        }

        // Trừ điểm từ user
        user.deductPoints(points);

        // Lưu vào order (CHỈ lưu số điểm, KHÔNG thay đổi totalPrice)
        order.setPointsRedeemed(points);
        
        log.info("User {} redeemed {} points for order {} (totalPrice unchanged: {})", 
                 user.getId(), points, order.getId(), order.getTotalPrice());
    }

    // ==================== REFUND ====================

    /**
     * Hoàn điểm khi hủy đơn hàng
     */
    @Transactional
    public void refundPointsFromCancelledOrder(Users user, Order order) {
        if (user == null || order == null) {
            throw new IllegalArgumentException("User and Order cannot be null");
        }

        boolean changed = false;

        // 1. Hoàn lại điểm đã dùng
        if (order.getPointsRedeemed() != null && order.getPointsRedeemed() > 0) {
            user.addPoints(order.getPointsRedeemed());
            log.info("Refunded {} redeemed points to user {} for cancelled order {}", 
                     order.getPointsRedeemed(), user.getId(), order.getId());
            changed = true;
        }

        // 2. Trừ lại điểm đã tích
        if (order.getPointsEarned() != null && order.getPointsEarned() > 0) {
            user.deductPoints(order.getPointsEarned());
            log.info("Deducted {} earned points from user {} for cancelled order {}", 
                     order.getPointsEarned(), user.getId(), order.getId());
            changed = true;
        }

        // 3. Trừ lại tổng chi tiêu (KHÔNG làm xuống hạng)
        if (order.getTotalPrice() != null && order.getTotalPrice() > 0) {
            user.deductSpending(order.getTotalPrice());
            log.info("Deducted {} spending from user {} for cancelled order {}", 
                     order.getTotalPrice(), user.getId(), order.getId());
            changed = true;
        }

        if (changed) {
            userRepository.save(user);
        }
    }

    // ==================== ADMIN OPERATIONS ====================

    /**
     * Admin điều chỉnh điểm thủ công
     */
    @Transactional
    public void adjustPoints(Long userId, int pointsChange, String reason) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (pointsChange > 0) {
            user.addPoints(pointsChange);
        } else if (pointsChange < 0) {
            user.deductPoints(Math.abs(pointsChange));
        }

        userRepository.save(user);

        log.info("Admin adjusted {} points for user {} (reason: {})", 
                 pointsChange, userId, reason);
    }

    /**
     * Admin cập nhật tier thủ công
     */
    @Transactional
    public void updateTier(Long userId, MembershipTier newTier) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MembershipTier oldTier = user.getMembershipTier();
        user.setMembershipTier(newTier);
        
        userRepository.save(user);

        log.info("Admin updated user {} tier from {} to {}", 
                 userId, oldTier, newTier);
    }
}
