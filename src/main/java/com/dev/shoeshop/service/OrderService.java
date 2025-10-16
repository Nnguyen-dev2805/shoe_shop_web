package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.CartDTO;
import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderResultDTO;
import com.dev.shoeshop.dto.OrderStaticDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.enums.ShipmentStatus;

import java.util.List;

public interface OrderService {
    public OrderStaticDTO getStatic();

    public List<OrderDTO> getOrderByStatus(ShipmentStatus status);

    public List<OrderDTO> getAllOrders();

    public Order findById(Long id);
    public Order findByOrderId(Long id);

    void cancelOrder(Long orderId);
    
    // Cart related methods
    CartDTO getCartByUserId(Long userId);
    
    void addToCart(Long userId, Long productDetailId, Integer quantity, Double pricePerUnit);
    
    OrderResultDTO processCheckout(Long cartId, Long userId, Long addressId, 
                                 Double finalTotalPrice, String payOption, 
                                 Long shippingCompanyId, Long discountId, 
                                 java.util.List<Integer> selectedItemIds,
                                 java.util.Map<Integer, Integer> itemQuantities);
}
