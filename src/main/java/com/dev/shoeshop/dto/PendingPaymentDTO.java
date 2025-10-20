package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO to store pending payment information before PayOS confirmation
 * This data will be used to create Order after payment is confirmed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingPaymentDTO {
    private Long payosOrderCode; // PayOS order code
    private Long userId;
    private Long cartId; // Nullable for Buy Now mode
    private Long addressId;
    private Long shippingCompanyId;
    private Long orderDiscountId;
    private Long shippingDiscountId;
    private Double finalTotalPrice;
    private List<Integer> selectedItemIds;
    private Map<Integer, Integer> itemQuantities;
    private Long createdAt; // Timestamp để expire pending payments
}
