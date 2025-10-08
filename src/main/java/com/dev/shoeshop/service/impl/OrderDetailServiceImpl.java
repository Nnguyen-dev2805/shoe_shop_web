package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderPaymentDTO;
import com.dev.shoeshop.entity.Cart;
import com.dev.shoeshop.entity.CartDetail;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.repository.CartDetailRepository;
import com.dev.shoeshop.repository.CartRepository;
import com.dev.shoeshop.repository.OrderDetailRepository;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    OrderRepository orderRepository;
    
    @Autowired
    CartDetailRepository cartDetailRepository;
    
    @Autowired
    CartRepository cartRepository;

    @Override
    public List<OrderDetailDTO> findAllOrderDetailById(Long id) {
        List<OrderDetail> optionalOrderDetail = orderDetailRepository.findOrderDetailsByOrderId(id);
        List<OrderDetailDTO> listDetailRes = new ArrayList<>();
        for (var item: optionalOrderDetail.stream().toList()){
            OrderDetailDTO detailDto = new OrderDetailDTO();
            //map data from OrderDetail to OrderDetailDTO
            detailDto.setSize(item.getProduct().getSize());
            detailDto.setProduct_name(item.getProduct().getProduct().getTitle());
            detailDto.setImage(item.getProduct().getProduct().getImage());
            detailDto.setPrice(item.getPrice());
            detailDto.setQuantity(item.getQuantity());
            detailDto.setAmount(item.getPrice() * item.getQuantity());

            listDetailRes.add(detailDto);
        }
        return listDetailRes;
    }

    @Override
    public OrderPaymentDTO getOrderPayment(Long id) {
        List<OrderDetail> optionalOrderDetail = orderDetailRepository.findOrderDetailsByOrderId(id);
        OrderPaymentDTO orderPaymentDto = new OrderPaymentDTO();
        double total = 0, discount = 0, payment = 0;
        for (var item: optionalOrderDetail.stream().toList()){
            total += (item.getProduct().getPriceadd() + item.getProduct().getProduct().getPrice()) * item.getQuantity();
        }
//        total += 5;
        Order order = orderRepository.findOrderById(id);
        payment = order.getTotalPrice();
        discount = total - payment;

        orderPaymentDto.setDiscount(discount);
        orderPaymentDto.setTotalpay(payment);
        orderPaymentDto.setSubtotal(total);
        return orderPaymentDto;
    }
    
    @Override
    @Transactional
    public void updateQuantity(Long detailId, Long quantity, Long userId) {
        System.out.println("=== OrderDetailServiceImpl.updateQuantity ===");
        System.out.println("Detail ID: " + detailId);
        System.out.println("Quantity: " + quantity);
        System.out.println("User ID: " + userId);
        
        // First, find and validate cart detail belongs to user
        CartDetail cartDetail = cartDetailRepository.findByIdAndUserId(detailId, userId)
            .orElseThrow(() -> new RuntimeException("Cart item not found or unauthorized access"));
        
        // Validate quantity against product stock
        int availableStock = cartDetail.getProduct().getQuantity();
        System.out.println("Available stock: " + availableStock);
        
        if (quantity > availableStock) {
            throw new RuntimeException("Số lượng vượt quá tồn kho. Chỉ còn " + availableStock + " sản phẩm");
        }
        
        if (quantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        
        // Update quantity
        cartDetail.setQuantity(quantity.intValue());
        cartDetailRepository.save(cartDetail);
        
        System.out.println("Quantity updated successfully");
        
        // Note: Cart total price is automatically updated by database trigger
    }
    
    @Override
    @Transactional
    public void editCartItem(Long detailId, Long quantity, Long userId) {
        CartDetail cartDetail = cartDetailRepository.findByIdAndUserId(detailId, userId)
            .orElseThrow(() -> new RuntimeException("Cart item not found or unauthorized access"));
        
        // Validate quantity against product stock
        int availableStock = cartDetail.getProduct().getQuantity();
        
        if (quantity > availableStock) {
            throw new RuntimeException("Số lượng vượt quá tồn kho. Chỉ còn " + availableStock + " sản phẩm");
        }
        
        if (quantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        
        cartDetail.setQuantity(quantity.intValue());
        cartDetailRepository.save(cartDetail);
        
        // Note: Cart total price is automatically updated by database trigger
    }
    
    @Override
    @Transactional
    public void removeCartItem(Long detailId, Long userId) {
        System.out.println("=== OrderDetailServiceImpl.removeCartItem ===");
        System.out.println("Detail ID: " + detailId);
        System.out.println("User ID: " + userId);
        
        // First, find and validate cart detail belongs to user
        CartDetail cartDetail = cartDetailRepository.findByIdAndUserId(detailId, userId)
            .orElseThrow(() -> new RuntimeException("Cart item not found or unauthorized access"));
        
        // Delete using entity (not @Modifying query to avoid trigger conflict)
        cartDetailRepository.delete(cartDetail);
        
        System.out.println("Cart item deleted successfully");
        
        // Note: Cart total price is automatically updated by database trigger
    }
    
    // DEPRECATED: Cart total price is now automatically updated by database trigger
    // No longer needed since database handles this via trigger
    /*
    private void updateCartTotalPrice(Long detailId, Long userId) {
        CartDetail cartDetail = cartDetailRepository.findById(detailId).orElse(null);
        if (cartDetail != null) {
            Cart cart = cartDetail.getCart();
            double totalPrice = cart.getCartDetails().stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();
            cart.setTotalPrice(totalPrice);
            cartRepository.save(cart);
        }
    }
    */
}
