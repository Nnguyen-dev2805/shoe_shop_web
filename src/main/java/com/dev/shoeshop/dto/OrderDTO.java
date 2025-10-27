package com.dev.shoeshop.dto;


import com.dev.shoeshop.entity.*;
import com.dev.shoeshop.enums.PayOption;
import com.dev.shoeshop.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;

    private String userName;

    private Double totalPrice;

    private Date createdDate;

    private ShipmentStatus status;

    private PayOption payOption;

    private UserDTO user;
    
    // Address (simple DTO)
    private AddressDTO address;
    
    // Danh sách chi tiết đơn hàng
    private List<OrderDetailDTO> orderDetails;
    
    // Discount & Voucher (simple data)
    private String discountName;
    private Double discountAmount;
    private String shippingDiscountName;
    private Double shippingFee; // Phí vận chuyển gốc
    private Double shippingDiscountAmount;
    private Double originalTotalPrice;
    
    // Payment
    private Date paidAt;
    private String paymentStatus;
    
    // Shipment (simple data)
    private Date shipmentUpdatedDate;
    private String shipmentStatus;
    
    // Loyalty Points
    private Integer pointsRedeemed;
    private Integer pointsEarned;
    
    // ========== NESTED DTOs ==========
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        private Long id;
        private String addressLine;
        private String city;
        private String country;
        
        // Thông tin người nhận từ UserAddress
        private String recipientName;
        private String recipientPhone;
    }
    
    /**
     * Tính giá trị giảm từ điểm
     */
    public double calculatePointsDiscount() {
        if (pointsRedeemed == null || pointsRedeemed == 0) {
            return 0;
        }
        return pointsRedeemed * 1000.0; // 1 điểm = 1,000 VNĐ
    }
}
