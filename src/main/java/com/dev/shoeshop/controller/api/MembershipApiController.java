package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.MembershipInfoDTO;
import com.dev.shoeshop.dto.PointsRedeemRequest;
import com.dev.shoeshop.dto.PointsRedeemResponse;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller cho Membership và Loyalty Points
 */
@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
@Slf4j
public class MembershipApiController {

    private final MembershipService membershipService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    /**
     * Lấy thông tin membership của user đang login
     * GET /api/membership/info
     */
    @GetMapping("/info")
    public ResponseEntity<?> getMembershipInfo(Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }

            Users user = userRepository.findByEmail(auth.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            MembershipInfoDTO info = membershipService.getMembershipInfo(user);
            return ResponseEntity.ok(info);

        } catch (Exception e) {
            log.error("Error getting membership info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy số điểm hiện tại
     * GET /api/membership/points/balance
     */
    @GetMapping("/points/balance")
    public ResponseEntity<?> getPointsBalance(Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }

            Users user = userRepository.findByEmail(auth.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            // Count delivered orders
            long deliveredOrderCount = orderRepository.countByUserAndStatus(user, ShipmentStatus.DELIVERED);
            
            Map<String, Object> response = new HashMap<>();
            response.put("points", user.getLoyaltyPoints());
            response.put("pointsValue", user.getPointsValue());
            response.put("tier", user.getMembershipTier().name());
            response.put("tierDisplayName", user.getMembershipTier().getDisplayName());
            response.put("totalSpending", user.getTotalSpending()); // ✅ Add total spending
            response.put("earnRate", user.getMembershipTier().getEarnRatePercent()); // ✅ Add earn rate
            response.put("orderCount", deliveredOrderCount); // ✅ Add order count

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting points balance", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Validate việc sử dụng điểm (check trước khi apply)
     * POST /api/membership/points/validate-redeem
     * Body: { "points": 50, "orderAmount": 1000000 }
     */
    @PostMapping("/points/validate-redeem")
    public ResponseEntity<?> validateRedeemPoints(
            @RequestBody PointsRedeemRequest request,
            Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }

            Users user = userRepository.findByEmail(auth.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            PointsRedeemResponse response = membershipService.validateRedeemPoints(user, request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error validating redeem points", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy số điểm tối đa có thể dùng cho đơn hàng
     * GET /api/membership/points/max-redeemable?orderAmount=1000000
     */
    @GetMapping("/points/max-redeemable")
    public ResponseEntity<?> getMaxRedeemablePoints(
            @RequestParam double orderAmount,
            Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }

            Users user = userRepository.findByEmail(auth.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            int maxPoints = user.getMaxRedeemablePoints(orderAmount);

            Map<String, Object> response = new HashMap<>();
            response.put("maxPoints", maxPoints);
            response.put("maxValue", maxPoints * 1000);
            response.put("userPoints", user.getLoyaltyPoints());
            response.put("orderAmount", orderAmount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting max redeemable points", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Tính điểm sẽ nhận được từ đơn hàng
     * GET /api/membership/points/calculate-earn?orderAmount=1000000
     */
    @GetMapping("/points/calculate-earn")
    public ResponseEntity<?> calculatePointsEarn(
            @RequestParam double orderAmount,
            Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }

            Users user = userRepository.findByEmail(auth.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            int points = membershipService.calculatePointsEarned(orderAmount, user);

            Map<String, Object> response = new HashMap<>();
            response.put("points", points);
            response.put("orderAmount", orderAmount);
            response.put("tier", user.getMembershipTier().name());
            response.put("earnRate", user.getMembershipTier().getEarnRate() * 100);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error calculating points earn", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     * GET /api/membership/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "MembershipService",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
