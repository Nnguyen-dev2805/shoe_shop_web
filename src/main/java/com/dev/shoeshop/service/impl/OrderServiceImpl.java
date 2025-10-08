package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.converter.OrderDTOConverter;
import com.dev.shoeshop.dto.CartDTO;
import com.dev.shoeshop.dto.CartDetailDTO;
import com.dev.shoeshop.dto.CartProductDTO;
import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderResultDTO;
import com.dev.shoeshop.dto.OrderStaticDTO;
import com.dev.shoeshop.entity.Address;
import com.dev.shoeshop.entity.Cart;
import com.dev.shoeshop.entity.CartDetail;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.PayOption;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.AddressRepository;
import com.dev.shoeshop.repository.CartDetailRepository;
import com.dev.shoeshop.repository.CartRepository;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.repository.ProductDetailRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDTOConverter orderDTOConverter;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartDetailRepository cartDetailRepository;
    
    @Autowired
    private ProductDetailRepository productDetailRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public OrderStaticDTO getStatic() {
        OrderStaticDTO orderStaticDto = new OrderStaticDTO();
        orderStaticDto.setShipping(orderRepository.countByStatus(ShipmentStatus.SHIPPED));
        orderStaticDto.setCancel(orderRepository.countByStatus(ShipmentStatus.CANCEL));
        orderStaticDto.setInStock(orderRepository.countByStatus(ShipmentStatus.IN_STOCK));
        orderStaticDto.setPreturn(orderRepository.countByStatus(ShipmentStatus.RETURN));
        orderStaticDto.setDelivered(orderRepository.countByStatus(ShipmentStatus.DELIVERED));

        return orderStaticDto;
    }

    @Override
    public List<OrderDTO> getOrderByStatus(ShipmentStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(orderDTOConverter::toOrderDTO)
                .toList();
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderDTOConverter::toOrderDTO)
                .toList();

    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("can not find order"));
    }

    @Override
    public Order findByOrderId(Long id) {
        return orderRepository.findOrderById(id);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = findById(orderId);
        order.setStatus(ShipmentStatus.CANCEL);
        orderRepository.save(order);
        System.out.println("Service save");
    }
    
    @Override
    public CartDTO getCartByUserId(Long userId) {
        System.out.println("Looking for cart with userId: " + userId);
        
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        
        if (cartOptional.isEmpty()) {
            System.out.println("No cart found for user: " + userId + ", creating new cart");
            // Create a new cart for the user
            Cart newCart = createNewCartForUser(userId);
            return convertToCartDTO(newCart);
        }
        
        Cart cart = cartOptional.get();
        System.out.println("Found cart with ID: " + cart.getId() + " for user: " + userId);
        
        return convertToCartDTO(cart);
    }
    
    @Override
    @Transactional
    public OrderResultDTO processCheckout(Long cartId, Long userId, Long addressId, 
                                        Double finalTotalPrice, String payOption, 
                                        Long shippingCompanyId, Long discountId, 
                                        java.util.List<Integer> selectedItemIds) {
        try {
            System.out.println("=== Processing checkout in service ===");
            System.out.println("Cart ID: " + cartId);
            System.out.println("User ID: " + userId);
            System.out.println("Selected Item IDs: " + selectedItemIds);
            
            // Validate cart belongs to user
            Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
            
            if (!cart.getUser().getId().equals(userId)) {
                throw new RuntimeException("Unauthorized access to cart");
            }
            
            // Get address
            Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
            
            // Create order from selected cart items
            Order order = createOrderFromCart(cart, address, finalTotalPrice, payOption, selectedItemIds);
            Order savedOrder = orderRepository.save(order);
            
            System.out.println("Order created with ID: " + savedOrder.getId());
            
            // Remove selected items from cart (không xóa toàn bộ cart)
            removeSelectedItemsFromCart(cart, selectedItemIds);
            
            // Handle payment processing
            OrderResultDTO orderResult = OrderResultDTO.builder()
                .orderId(savedOrder.getId())
                .status("SUCCESS")
                .message("Order created successfully")
                .build();
            
            // If VNPay, generate payment URL
            if ("VNPAY".equals(payOption)) {
                String paymentUrl = generateVNPayUrl(savedOrder);
                orderResult.setPaymentUrl(paymentUrl);
            }
            
            return orderResult;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing checkout: " + e.getMessage(), e);
        }
    }
    
    private CartDTO convertToCartDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserId(cart.getUser().getId());
        cartDTO.setTotalPrice(cart.getTotalPrice());
        cartDTO.setCreatedDate(cart.getCreatedDate());
        
        // Convert cart details
        Set<CartDetailDTO> cartDetailDTOs = new HashSet<>();
        if (cart.getCartDetails() != null) {
            for (CartDetail detail : cart.getCartDetails()) {
                CartDetailDTO detailDTO = new CartDetailDTO();
                detailDTO.setId(Long.valueOf(detail.getId()));
                detailDTO.setCartId(cart.getId());
                detailDTO.setQuantity(Long.valueOf(detail.getQuantity()));
                detailDTO.setPrice(detail.getPrice());
                
                // Convert ProductDetail to CartProductDTO
                if (detail.getProduct() != null) {
                    CartProductDTO productDTO = convertToCartProductDTO(detail.getProduct());
                    detailDTO.setProduct(productDTO);
                }
                
                cartDetailDTOs.add(detailDTO);
            }
        }
        cartDTO.setCartDetails(cartDetailDTOs);
        
        return cartDTO;
    }
    
    private Order createOrderFromCart(Cart cart, Address address, Double finalTotalPrice, 
                                    String payOption, java.util.List<Integer> selectedItemIds) {
        System.out.println("Creating order from cart...");
        
        // Create order
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setAddress(address);
        order.setTotalPrice(finalTotalPrice);
        order.setCreatedDate(new Date());
        order.setStatus(ShipmentStatus.IN_STOCK);
        order.setPayOption(PayOption.valueOf(payOption));
        
        // Create order details
        Set<OrderDetail> orderDetails = new HashSet<>();
        
        for (CartDetail cartDetail : cart.getCartDetails()) {
            // Chỉ thêm vào order nếu item được chọn
            if (selectedItemIds == null || selectedItemIds.contains(cartDetail.getId())) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(cartDetail.getProduct());
                orderDetail.setQuantity(cartDetail.getQuantity());
                orderDetail.setPrice(cartDetail.getPrice());
                
                orderDetails.add(orderDetail);
                System.out.println("Added order detail: product=" + cartDetail.getProduct().getId() + 
                                 ", quantity=" + cartDetail.getQuantity() + 
                                 ", price=" + cartDetail.getPrice());
            }
        }
        
        order.setOrderDetailSet(orderDetails);
        System.out.println("Order created with " + orderDetails.size() + " items");
        
        return order;
    }
    
    private void removeSelectedItemsFromCart(Cart cart, java.util.List<Integer> selectedItemIds) {
        System.out.println("Removing selected items from cart...");
        
        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
            // Nếu không có selectedItemIds, xóa toàn bộ (backward compatibility)
            cart.getCartDetails().clear();
            cart.setTotalPrice(0.0);
        } else {
            // Tạo list items cần xóa (tránh ConcurrentModificationException)
            java.util.List<CartDetail> toRemove = new java.util.ArrayList<>();
            for (CartDetail detail : cart.getCartDetails()) {
                if (selectedItemIds.contains(detail.getId())) {
                    toRemove.add(detail);
                }
            }
            
            // Xóa các items
            cart.getCartDetails().removeAll(toRemove);
            
            // Tính lại tổng tiền
            Double newTotal = cart.getCartDetails().stream()
                .mapToDouble(cd -> cd.getPrice() * cd.getQuantity())
                .sum();
            cart.setTotalPrice(newTotal);
            
            System.out.println("Removed " + toRemove.size() + " items from cart");
            System.out.println("New cart total: " + newTotal);
        }
        
        cartRepository.save(cart);
    }
    
    private String generateVNPayUrl(Order order) {
        // Implement VNPay URL generation based on your VNPay integration
        // This is a placeholder
        return "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?...";
    }
    
    private Cart createNewCartForUser(Long userId) {
        try {
            Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalPrice(0.0);
            newCart.setCartDetails(new HashSet<>());
            
            Cart savedCart = cartRepository.save(newCart);
            System.out.println("Created new cart with ID: " + savedCart.getId() + " for user: " + userId);
            
            return savedCart;
        } catch (Exception e) {
            System.out.println("Error creating cart for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to create cart for user: " + userId, e);
        }
    }
    
    private CartProductDTO convertToCartProductDTO(ProductDetail productDetail) {
        CartProductDTO cartProductDTO = new CartProductDTO();
        cartProductDTO.setId(productDetail.getId());
        cartProductDTO.setSize(productDetail.getSize());
        cartProductDTO.setPriceadd(productDetail.getPriceadd());
        
        // Create nested product info
        CartProductDTO.ProductInfo productInfo = new CartProductDTO.ProductInfo();
        if (productDetail.getProduct() != null) {
            productInfo.setId(productDetail.getProduct().getId());
            productInfo.setTitle(productDetail.getProduct().getTitle());
            productInfo.setImage(productDetail.getProduct().getImage());
            productInfo.setPrice(productDetail.getProduct().getPrice());
        }
        cartProductDTO.setProduct(productInfo);
        
        return cartProductDTO;
    }
    
    @Override
    @Transactional
    public void addToCart(Long userId, Long productDetailId, Integer quantity, Double pricePerUnit) {
        System.out.println("=== OrderServiceImpl.addToCart ===");
        
        // Find or create cart for user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    Users user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    newCart.setUser(user);
                    newCart.setTotalPrice(0.0);
                    return cartRepository.save(newCart);
                });
        
        System.out.println("Cart ID: " + cart.getId());
        
        // Get ProductDetail
        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new RuntimeException("Product detail not found"));
        
        // Check if product already exists in cart
        Optional<CartDetail> existingCartDetail = cart.getCartDetails().stream()
                .filter(cd -> cd.getProduct().getId().equals(productDetailId))
                .findFirst();
        
        if (existingCartDetail.isPresent()) {
            // Update existing cart detail
            CartDetail cartDetail = existingCartDetail.get();
            int newQuantity = cartDetail.getQuantity() + quantity;
            cartDetail.setQuantity(newQuantity);
            cartDetail.setPrice(pricePerUnit); // Update price in case it changed
            cartDetailRepository.save(cartDetail);
            System.out.println("Updated existing cart detail, new quantity: " + newQuantity);
        } else {
            // Create new cart detail
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setProduct(productDetail);
            cartDetail.setQuantity(quantity);
            cartDetail.setPrice(pricePerUnit);
            cartDetailRepository.save(cartDetail);
            System.out.println("Created new cart detail");
        }
        
        // Update cart total price
        updateCartTotalPrice(cart.getId());
    }
    
    /**
     * Update cart total price by summing all cart details
     */
    @Transactional
    public void updateCartTotalPrice(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        Double totalPrice = cart.getCartDetails().stream()
                .mapToDouble(cd -> cd.getPrice() * cd.getQuantity())
                .sum();
        
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
        
        System.out.println("Updated cart total price: " + totalPrice);
    }
}
