package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderPaymentDTO;
import com.dev.shoeshop.entity.Cart;
import com.dev.shoeshop.entity.CartDetail;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.repository.CartDetailRepository;
import com.dev.shoeshop.repository.CartRepository;
import com.dev.shoeshop.repository.InventoryRepository;
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
    
    @Autowired
    InventoryRepository inventoryRepository;

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
        
        // Validate quantity against product stock from Inventory
        int availableStock = inventoryRepository.getTotalQuantityByProductDetail(cartDetail.getProduct());
        System.out.println("Available stock from Inventory: " + availableStock);
        
        if (quantity > availableStock) {
            throw new RuntimeException("Số lượng vượt quá tồn kho. Chỉ còn " + availableStock + " sản phẩm");
        }
        
        if (quantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        
        // Update quantity
        cartDetail.setQuantity(quantity.intValue());
        cartDetailRepository.save(cartDetail);
        
        System.out.println("Quantity updated successfully. Total: " + cartDetail.getCart().getTotalPrice());
    }
    
    @Override
    @Transactional
    public void editCartItem(Long detailId, Long quantity, Long userId) {
        CartDetail cartDetail = cartDetailRepository.findByIdAndUserId(detailId, userId)
            .orElseThrow(() -> new RuntimeException("Cart item not found or unauthorized access"));
        
        // Validate quantity against product stock from Inventory
        int availableStock = inventoryRepository.getTotalQuantityByProductDetail(cartDetail.getProduct());
        
        if (quantity > availableStock) {
            throw new RuntimeException("Số lượng vượt quá tồn kho. Chỉ còn " + availableStock + " sản phẩm");
        }
        
        if (quantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        
        cartDetail.setQuantity(quantity.intValue());
        cartDetailRepository.save(cartDetail);
        
        System.out.println("Edited cart item successfully. Total: " + cartDetail.getCart().getTotalPrice());
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
        
        // Get cart before deleting detail (for updating timestamp)
        Cart cart = cartDetail.getCart();
        
        // ✅ IMPORTANT: Remove from cart's collection first to prevent cascade restore
        cart.getCartDetails().remove(cartDetail);
        
        // Delete cart detail from database
        cartDetailRepository.delete(cartDetail);
        
        // Flush to ensure delete is executed before saving cart
        cartDetailRepository.flush();
        
        // Save cart to persist the collection change
        cartRepository.save(cart);
        
        System.out.println("Cart item deleted successfully. Remaining total: " + cart.getTotalPrice());
    }
    
    // REMOVED: updateCartTotalPrice() method is no longer needed
    // Cart.getTotalPrice() now calculates the total automatically from cartDetails
    // No need to manually update totalPrice field since it was removed from entity
}
