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
    private Long flashSaleId; // Flash Sale ID
    private Double finalTotalPrice;
    private Double subtotal; // Subtotal before discounts and shipping
    private Double shippingFee; // Shipping fee
    private Double orderDiscountAmount; // Order discount amount applied
    private Double shippingDiscountAmount; // Shipping discount amount applied
    private List<Integer> selectedItemIds;
    private Map<Integer, Integer> itemQuantities;
    private Long createdAt; // Timestamp để expire pending payments
}
