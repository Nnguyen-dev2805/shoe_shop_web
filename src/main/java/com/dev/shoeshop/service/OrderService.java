package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.CartDTO;
import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderResultDTO;
import com.dev.shoeshop.dto.OrderStaticDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    public OrderStaticDTO getStatic();

    public List<OrderDTO> getOrderByStatus(ShipmentStatus status);

    public List<OrderDTO> getAllOrders();
    
    // ✅ Pagination methods
    Page<OrderDTO> getAllOrdersWithPagination(Pageable pageable);
    
    Page<OrderDTO> getOrderByStatusWithPagination(ShipmentStatus status, Pageable pageable);
    
    // Lấy danh sách orders của user
    public List<OrderDTO> getOrdersByUserId(Long userId);
    
    // Lấy danh sách orders của user theo status
    public List<OrderDTO> getOrdersByUserIdAndStatus(Long userId, ShipmentStatus status);
    
    // Lấy order detail với thông tin sản phẩm
    public OrderDTO getOrderDetailById(Long orderId, Long userId);

    public Order findById(Long id);
    public Order findByOrderId(Long id);

    void cancelOrder(Long orderId);
    
    /**
     * Cập nhật trạng thái order (MVC Pattern)
     */
    void updateOrderStatus(Long orderId, ShipmentStatus newStatus);
    
    // Cart related methods
    CartDTO getCartByUserId(Long userId);
    
    void addToCart(Long userId, Long productDetailId, Integer quantity, Double pricePerUnit);
    
    OrderResultDTO processCheckout(Long cartId, Long userId, Long addressId, 
                                 Double finalTotalPrice, String payOption, 
                                 Long shippingCompanyId, Long orderDiscountId, Long shippingDiscountId,
                                 Long flashSaleId,
                                 java.util.List<Integer> selectedItemIds,
                                 java.util.Map<Integer, Integer> itemQuantities,
                                 Double subtotal, Double shippingFee, Double orderDiscountAmount, Double shippingDiscountAmount,
                                 Integer pointsRedeemed);
    
    /**
     * Đánh dấu đơn hàng đã giao thành công
     */
    void markOrderAsDelivered(Long orderId);
    
    /**
     * Đánh dấu đơn hàng bị hoàn trả
     */
    void markOrderAsReturn(Long orderId);
    
    /**
     * Update PayOS payment information after webhook confirmation
     */
    void updatePayOSPaymentInfo(Long orderId, Long payosOrderCode, String paymentStatus, java.util.Date paidAt);
}
