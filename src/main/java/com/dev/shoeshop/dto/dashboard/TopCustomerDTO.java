package com.dev.shoeshop.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho top khách hàng mua nhiều nhất
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDTO {
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Long totalOrders;        // Tổng số đơn hàng
    private Long totalProducts;      // Tổng số sản phẩm đã mua
    private Double totalSpent;       // Tổng số tiền đã chi
}
