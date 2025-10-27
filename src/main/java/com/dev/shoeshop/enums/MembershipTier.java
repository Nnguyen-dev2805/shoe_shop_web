package com.dev.shoeshop.enums;

/**
 * Enum định nghĩa 4 hạng thành viên
 * Không dùng database table để giữ đơn giản, configs được hardcode
 */
public enum MembershipTier {
    SILVER("🥈 Bạc", 0, 0.01, 5000, 1, 12, 0),
    GOLD("🥇 Vàng", 5_000_000, 0.015, 15000, 2, 12, 50),
    PLATINUM("💎 Bạch Kim", 20_000_000, 0.02, 30000, 3, 12, 200),
    DIAMOND("💠 Kim Cương", 50_000_000, 0.03, 0, 5, 999, 500); // 0 = FREE SHIP, 999 = không hết hạn

    private final String displayName;
    private final double minSpending;           // Tổng chi tiêu tối thiểu để đạt hạng này
    private final double earnRate;              // Tỷ lệ tích điểm (1%, 1.5%, 2%, 3%)
    private final double shippingDiscount;      // Giảm ship (VNĐ), 0 = free ship
    private final int monthlyVouchers;          // Số voucher/tháng
    private final int pointsExpiryMonths;       // Điểm hết hạn sau X tháng
    private final int upgradeBonus;             // Bonus điểm khi lên hạng

    MembershipTier(String displayName, double minSpending, double earnRate, 
                   double shippingDiscount, int monthlyVouchers, 
                   int pointsExpiryMonths, int upgradeBonus) {
        this.displayName = displayName;
        this.minSpending = minSpending;
        this.earnRate = earnRate;
        this.shippingDiscount = shippingDiscount;
        this.monthlyVouchers = monthlyVouchers;
        this.pointsExpiryMonths = pointsExpiryMonths;
        this.upgradeBonus = upgradeBonus;
    }

    // ==================== GETTERS ====================
    
    public String getDisplayName() {
        return displayName;
    }

    public double getMinSpending() {
        return minSpending;
    }

    public double getEarnRate() {
        return earnRate;
    }
    
    /**
     * Get earn rate as percentage (e.g., 1, 1.5, 2, 3)
     */
    public double getEarnRatePercent() {
        return earnRate * 100;
    }

    public double getShippingDiscount() {
        return shippingDiscount;
    }

    public int getMonthlyVouchers() {
        return monthlyVouchers;
    }

    public int getPointsExpiryMonths() {
        return pointsExpiryMonths;
    }

    public int getUpgradeBonus() {
        return upgradeBonus;
    }

    // ==================== UTILITY METHODS ====================
    
    /**
     * Lấy emoji icon của tier
     */
    public String getIcon() {
        return displayName.substring(0, 2); // Lấy emoji đầu tiên
    }
    
    /**
     * Lấy màu sắc của tier
     */
    public String getColor() {
        switch (this) {
            case SILVER: return "#C0C0C0";
            case GOLD: return "#FFD700";
            case PLATINUM: return "#E5E4E2";
            case DIAMOND: return "#B9F2FF";
            default: return "#808080";
        }
    }
    
    /**
     * Lấy tên ngắn (không có emoji)
     */
    public String getShortName() {
        return displayName.substring(3); // Bỏ emoji và space
    }
    
    /**
     * Check xem có phải free ship không
     */
    public boolean isFreeShipping() {
        return shippingDiscount == 0;
    }
    
    /**
     * Check xem điểm có hết hạn không
     */
    public boolean hasPointsExpiry() {
        return pointsExpiryMonths < 999;
    }
    
    // ==================== BUSINESS LOGIC ====================
    
    /**
     * Kiểm tra user có đủ điều kiện cho hạng này không
     */
    public boolean isEligible(double totalSpending) {
        return totalSpending >= minSpending;
    }
    
    /**
     * Tính hạng phù hợp dựa trên tổng chi tiêu
     * Logic: Tìm hạng cao nhất mà user đủ điều kiện
     */
    public static MembershipTier calculateTier(double totalSpending) {
        if (totalSpending >= DIAMOND.minSpending) return DIAMOND;
        if (totalSpending >= PLATINUM.minSpending) return PLATINUM;
        if (totalSpending >= GOLD.minSpending) return GOLD;
        return SILVER;
    }
    
    /**
     * Lấy hạng tiếp theo (để hiển thị progress)
     * @return null nếu đã max tier (DIAMOND)
     */
    public MembershipTier getNextTier() {
        switch (this) {
            case SILVER: return GOLD;
            case GOLD: return PLATINUM;
            case PLATINUM: return DIAMOND;
            case DIAMOND: return null; // Đã max
            default: return null;
        }
    }
    
    /**
     * Tính số tiền còn thiếu để lên hạng tiếp theo
     * @param currentSpending Tổng chi tiêu hiện tại của user
     * @return Số tiền còn thiếu, 0 nếu đã max tier
     */
    public double getRemainingToNextTier(double currentSpending) {
        MembershipTier next = getNextTier();
        if (next == null) return 0; // Đã max tier
        return Math.max(0, next.minSpending - currentSpending);
    }
    
    /**
     * Tính % progress đến hạng tiếp theo
     * @param currentSpending Tổng chi tiêu hiện tại
     * @return 0-100, trả về 100 nếu đã max tier
     */
    public int getProgressToNextTier(double currentSpending) {
        MembershipTier next = getNextTier();
        if (next == null) return 100; // Đã max tier
        
        double progress = (currentSpending - this.minSpending) / (next.minSpending - this.minSpending);
        return (int) Math.min(100, Math.max(0, progress * 100));
    }
    
    /**
     * Tính điểm earned cho một đơn hàng
     * Formula: (orderAmount / 1000) * earnRate
     * @param orderAmount Giá trị đơn hàng (sau khi trừ điểm nếu có)
     * @return Số điểm được tích
     */
    public int calculatePointsEarned(double orderAmount) {
        double points = (orderAmount / 1000.0) * earnRate;
        return (int) Math.floor(points);
    }
    
    /**
     * Quy đổi điểm sang tiền
     * @param points Số điểm
     * @return Giá trị tiền (VNĐ)
     */
    public static double pointsToMoney(int points) {
        return points * 1000.0;
    }
    
    /**
     * Quy đổi tiền sang điểm
     * @param money Số tiền (VNĐ)
     * @return Số điểm
     */
    public static int moneyToPoints(double money) {
        return (int) Math.floor(money / 1000.0);
    }
    
    /**
     * Validate số điểm có thể redeem cho một đơn hàng
     * Rule: Tối đa 50% giá trị đơn hàng
     */
    public static int getMaxRedeemablePoints(double orderAmount) {
        double maxValue = orderAmount * 0.5; // 50%
        return moneyToPoints(maxValue);
    }
    
    /**
     * Check xem số điểm có thể dùng cho đơn hàng không
     */
    public static boolean canRedeemPoints(int points, double orderAmount, int userPoints) {
        // Check 1: User có đủ điểm không
        if (points > userPoints) return false;
        
        // Check 2: Đơn tối thiểu 100k
        if (orderAmount < 100_000) return false;
        
        // Check 3: Không vượt quá 50% giá trị đơn
        int maxPoints = getMaxRedeemablePoints(orderAmount);
        return points <= maxPoints;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
