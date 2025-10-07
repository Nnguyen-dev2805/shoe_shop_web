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
        Long updated = cartDetailRepository.updateQuantity(detailId, quantity, userId);
        if (updated == 0) {
            throw new RuntimeException("Cart item not found or unauthorized access");
        }
        
        // Update cart total price
        updateCartTotalPrice(detailId, userId);
    }
    
    @Override
    @Transactional
    public void editCartItem(Long detailId, Long quantity, Long userId) {
        CartDetail cartDetail = cartDetailRepository.findByIdAndUserId(detailId, userId)
            .orElseThrow(() -> new RuntimeException("Cart item not found or unauthorized access"));
        
        cartDetail.setQuantity(quantity.intValue());
        cartDetailRepository.save(cartDetail);
        
        // Update cart total price
        updateCartTotalPrice(detailId, userId);
    }
    
    @Override
    @Transactional
    public void removeCartItem(Long detailId, Long userId) {
        Long deleted = cartDetailRepository.deleteByIdAndUserId(detailId, userId);
        if (deleted == 0) {
            throw new RuntimeException("Cart item not found or unauthorized access");
        }
        
        // Update cart total price
        updateCartTotalPrice(detailId, userId);
    }
    
    private void updateCartTotalPrice(Long detailId, Long userId) {
        // Find the cart through cart detail or user
        CartDetail cartDetail = cartDetailRepository.findById(detailId).orElse(null);
        if (cartDetail != null) {
            Cart cart = cartDetail.getCart();
            
            // Recalculate total price
            double totalPrice = cart.getCartDetails().stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();
            
            cart.setTotalPrice(totalPrice);
            cartRepository.save(cart);
        }
    }
}
