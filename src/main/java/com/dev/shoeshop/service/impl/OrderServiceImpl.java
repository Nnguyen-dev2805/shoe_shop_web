package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.converter.OrderDTOConverter;
import com.dev.shoeshop.dto.CartDTO;
import com.dev.shoeshop.dto.CartDetailDTO;
import com.dev.shoeshop.dto.CartProductDTO;
import com.dev.shoeshop.dto.InventoryUpdateDTO;
import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderNotificationDTO;
import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderResultDTO;
import com.dev.shoeshop.dto.OrderStaticDTO;
import com.dev.shoeshop.dto.SoldQuantityUpdateDTO;
import com.dev.shoeshop.entity.*;
import com.dev.shoeshop.enums.PayOption;
import com.dev.shoeshop.enums.ShipmentStatus;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import com.dev.shoeshop.repository.AddressRepository;
import com.dev.shoeshop.repository.CartDetailRepository;
import com.dev.shoeshop.repository.CartRepository;
import com.dev.shoeshop.repository.DiscountRepository;
import com.dev.shoeshop.repository.DiscountUsedRepository;
import com.dev.shoeshop.repository.FlashSaleRepository;
import com.dev.shoeshop.repository.InventoryRepository;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.repository.ProductDetailRepository;
import com.dev.shoeshop.repository.ProductRepository;
import com.dev.shoeshop.repository.ShippingCompanyRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.EmailService;
import com.dev.shoeshop.service.NotificationService;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    
    @Autowired
    private DiscountRepository discountRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private RatingService ratingService;
    
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private DiscountUsedRepository discountUsedRepository;

    @Autowired
    private EmailService emailService;

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
        // Sắp xếp theo ngày tạo mới nhất (giống shipper)
        return orderRepository.findByStatusOrderByCreatedDateDesc(status)
                .stream()
                .map(orderDTOConverter::toOrderDTO)
                .toList();
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        // Sắp xếp theo ngày tạo mới nhất (giống shipper)
        return orderRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .map(orderDTOConverter::toOrderDTO)
                .toList();
    }
    
    // ✅ Pagination methods implementation
    @Override
    public Page<OrderDTO> getAllOrdersWithPagination(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(orderDTOConverter::toOrderDTO);
    }
    
    @Override
    public Page<OrderDTO> getOrderByStatusWithPagination(ShipmentStatus status, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByStatus(status, pageable);
        return orderPage.map(orderDTOConverter::toOrderDTO);
    }
    
    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedDateDesc(userId)
                .stream()
                .map(orderDTOConverter::toOrderDTO)
                .toList();
    }
    
    @Override
    public List<OrderDTO> getOrdersByUserIdAndStatus(Long userId, ShipmentStatus status) {
        return orderRepository.findByUserIdAndStatusOrderByCreatedDateDesc(userId, status)
                .stream()
                .map(orderDTOConverter::toOrderDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderDetailById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Kiểm tra order thuộc về user
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        // Force load OrderDetails (vì mặc định là LAZY loading)
        if (order.getOrderDetailSet() != null) {
            order.getOrderDetailSet().size(); // Trigger lazy loading
        }
        
        return convertToOrderDetailDTO(order);
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
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = findById(orderId);

        // ✅ Nếu order có flash sale, giảm totalSold
        if (order.getAppliedFlashSale() != null) {
            FlashSale flashSale = order.getAppliedFlashSale();
            int totalQuantity = order.getOrderDetailSet().stream()
                .mapToInt(detail -> detail.getQuantity())
                .sum();

            flashSale.decrementSold(totalQuantity);
            flashSaleRepository.save(flashSale);
            System.out.println("🔥 Decreased Flash Sale totalSold by " + totalQuantity + " (Order CANCEL)");
        }

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
                                        Long shippingCompanyId, Long orderDiscountId, Long shippingDiscountId,
                                        Long flashSaleId,
                                        java.util.List<Integer> selectedItemIds,
                                        java.util.Map<Integer, Integer> itemQuantities,
                                        Double subtotal, Double shippingFee, Double orderDiscountAmount, Double shippingDiscountAmount) {
        try {
            System.out.println("=== Processing checkout in service ===");
            System.out.println("Cart ID: " + cartId);
            System.out.println("User ID: " + userId);
            System.out.println("✅ Order Discount ID: " + orderDiscountId);
            System.out.println("✅ Shipping Discount ID: " + shippingDiscountId);
            System.out.println("🔥 Flash Sale ID: " + flashSaleId);
            System.out.println("Selected Item IDs: " + selectedItemIds);
            
            // Get address
            Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
            
            Order savedOrder;
            
            // Check if this is Buy Now mode (cartId null) or Cart mode
            if (cartId == null) {
                // BUY NOW MODE: Create order directly from selectedItemIds
                System.out.println("Processing BUY NOW order (no cart)");
                
                // Get user
                Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                
                // Create order directly from product details
                Order order = createOrderFromProductDetails(user, address, finalTotalPrice, payOption, 
                                                          shippingCompanyId, orderDiscountId, shippingDiscountId,
                                                          flashSaleId, selectedItemIds, itemQuantities,
                                                          subtotal, shippingFee, orderDiscountAmount, shippingDiscountAmount);
                savedOrder = orderRepository.save(order);
                
                System.out.println("Buy Now order created with ID: " + savedOrder.getId());
                
                // ✅ REFRESH order để lấy orderDetailSet từ DB (sau khi cascade insert)
                savedOrder = orderRepository.findById(savedOrder.getId())
                        .orElse(savedOrder);
                System.out.println("Order details count after refresh: " + 
                                 (savedOrder.getOrderDetailSet() != null ? savedOrder.getOrderDetailSet().size() : 0));
                
                // 🔥 Tăng Flash Sale totalSold khi tạo order (Buy Now mode)
                incrementFlashSaleTotalSold(savedOrder);

            } else {
                // CART MODE: Process from cart
                System.out.println("Processing CART order");
                
                // Validate cart belongs to user
                Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
                
                if (!cart.getUser().getId().equals(userId)) {
                    throw new RuntimeException("Unauthorized access to cart");
                }
                

                
                // Create order from selected cart items
                Order order = createOrderFromCart(cart, address, finalTotalPrice, payOption, 
                                                orderDiscountId, shippingDiscountId, flashSaleId, selectedItemIds,
                                                subtotal, shippingFee, orderDiscountAmount, shippingDiscountAmount);
                
                // Save order (sẽ cascade insert order_detail)
                // Lúc này cart items đã bị xóa rồi
                savedOrder = orderRepository.save(order);
                                // XÓA CART ITEMS TRƯỚC KHI INSERT ORDER_DETAIL
                // (để đảm bảo cart được clear ngay khi order_detail sắp được insert)
                System.out.println("Removing cart items BEFORE creating order...");
                removeSelectedItemsFromCart(cart, selectedItemIds);
                
                System.out.println("Cart order created with ID: " + savedOrder.getId());

                // 🔥 Tăng Flash Sale totalSold khi tạo order (Cart mode)
                incrementFlashSaleTotalSold(savedOrder);
            }
            
            // ✅ REFRESH order để lấy orderDetailSet từ DB (sau khi cascade insert)
            savedOrder = orderRepository.findById(savedOrder.getId())
                    .orElse(savedOrder);
            System.out.println("Order details count after refresh: " + 
                             (savedOrder.getOrderDetailSet() != null ? savedOrder.getOrderDetailSet().size() : 0));
            
            // ✅ BROADCAST INVENTORY UPDATE (sau khi trigger trừ kho)
            System.out.println("Broadcasting inventory update after order creation...");
            broadcastInventoryAfterOrderCreation(savedOrder);
            
            // GỬI THÔNG BÁO WEBSOCKET ĐẾN ADMIN
            System.out.println("Sending WebSocket notification to admin...");
            sendOrderNotificationToAdmin(savedOrder);
            
            // ✅ TRACK DISCOUNT USAGE - Tạo DiscountUsed records
            trackDiscountUsage(savedOrder, orderDiscountId, shippingDiscountId);

            // Handle payment processing
            // Return order result
            // Note: PayOS payment link is now created separately in ApiCartController
            // This method is only called for COD or after PayOS payment confirmation
            OrderResultDTO orderResult = OrderResultDTO.builder()
                .orderId(savedOrder.getId())
                .status("SUCCESS")
                .message("Order created successfully")
                .build();

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
                detailDTO.setId(detail.getId());
                detailDTO.setCartId(cart.getId());
                detailDTO.setQuantity(Long.valueOf(detail.getQuantity()));
                detailDTO.setPricePerUnit(detail.getPricePerUnit());
                
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
    
    /**
     * Create order from product details directly (Buy Now mode)
     */
    private Order createOrderFromProductDetails(Users user, Address address, Double finalTotalPrice, 
                                               String payOption, Long shippingCompanyId, Long orderDiscountId, Long shippingDiscountId,
                                               Long flashSaleId,
                                               java.util.List<Integer> selectedItemIds,
                                               java.util.Map<Integer, Integer> itemQuantities,
                                               Double subtotal, Double shippingFee, Double orderDiscountAmount, Double shippingDiscountAmount) {
        System.out.println("Creating order from product details (Buy Now)...");
        
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setTotalPrice(finalTotalPrice);
        order.setCreatedDate(new Date());
        order.setStatus(ShipmentStatus.IN_STOCK);
        order.setPayOption(PayOption.valueOf(payOption));
        
        // ✅ Set order discount if applied
        if (orderDiscountId != null) {
            Discount orderDiscount = discountRepository.findById(orderDiscountId)
                .orElse(null);
            if (orderDiscount != null) {
                order.setAppliedDiscount(orderDiscount);
                System.out.println("✅ Applied order discount: " + orderDiscount.getName());
            }
        }
        
        // ✅ Set shipping discount if applied
        if (shippingDiscountId != null) {
            Discount shippingDiscount = discountRepository.findById(shippingDiscountId)
                .orElse(null);
            if (shippingDiscount != null) {
                order.setShippingDiscount(shippingDiscount);
                System.out.println("✅ Applied shipping discount: " + shippingDiscount.getName());
            }
        }
        
        // 🔥 Set flash sale if applied
        System.out.println("🔍 DEBUG (Buy Now): flashSaleId = " + flashSaleId);
        if (flashSaleId != null) {
            System.out.println("🔍 DEBUG: Querying flash sale with ID: " + flashSaleId);
            FlashSale flashSale = flashSaleRepository.findById(flashSaleId)
                .orElse(null);
            System.out.println("🔍 DEBUG: FlashSale found = " + (flashSale != null));
            if (flashSale != null) {
                System.out.println("🔍 DEBUG: FlashSale.isActive() = " + flashSale.isActive());
            }

            if (flashSale != null && flashSale.isActive()) {
                order.setAppliedFlashSale(flashSale);
                System.out.println("🔥 Applied Flash Sale: " + flashSale.getName() + " (ID: " + flashSale.getId() + ")");
            } else {
                System.out.println("⚠️ Flash Sale ID " + flashSaleId + " not found or not active");
            }
        } else {
            System.out.println("⚠️ No flash sale ID provided (Buy Now mode)");
        }

        // Create order details from product detail IDs
        Set<OrderDetail> orderDetails = new HashSet<>();
        
        if (selectedItemIds != null && !selectedItemIds.isEmpty()) {
            for (Integer productDetailId : selectedItemIds) {
                ProductDetail productDetail = productDetailRepository.findById(Long.valueOf(productDetailId))
                    .orElseThrow(() -> new RuntimeException("Product detail not found: " + productDetailId));
                
                // Get quantity from map or default to 1
                Integer quantity = itemQuantities != null && itemQuantities.containsKey(productDetailId) ? 
                                 itemQuantities.get(productDetailId) : 1;
                
                // Calculate price (base price + size fee)
                Double unitPrice = productDetail.getProduct().getPrice() + productDetail.getPriceadd();
                
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(productDetail);
                orderDetail.setQuantity(quantity);
                orderDetail.setPrice(unitPrice);
                
                orderDetails.add(orderDetail);
                System.out.println("Added order detail (Buy Now): product=" + productDetail.getId() + 
                                 ", quantity=" + quantity + ", price=" + unitPrice);
            }
        }
        
        order.setOrderDetailSet(orderDetails);

        // ✅ Calculate and set discount amounts, original price
        calculateAndSetOrderPricing(order, subtotal, shippingFee, orderDiscountAmount, shippingDiscountAmount);

        System.out.println("Buy Now order created with " + orderDetails.size() + " items");
        
        return order;
    }
    
    /**
     * Create order from cart (Cart mode)
     */
    private Order createOrderFromCart(Cart cart, Address address, Double finalTotalPrice, 
                                    String payOption, Long orderDiscountId, Long shippingDiscountId,
                                    Long flashSaleId,
                                    java.util.List<Integer> selectedItemIds,
                                    Double subtotal, Double shippingFee, Double orderDiscountAmount, Double shippingDiscountAmount) {
        System.out.println("Creating order from cart...");
        
        // Create order
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setAddress(address);
        order.setTotalPrice(finalTotalPrice);
        order.setCreatedDate(new Date());
        order.setStatus(ShipmentStatus.IN_STOCK);
        order.setPayOption(PayOption.valueOf(payOption));
        
        // ✅ Set order discount if applied
        if (orderDiscountId != null) {
            Discount orderDiscount = discountRepository.findById(orderDiscountId)
                .orElse(null);
            if (orderDiscount != null) {
                order.setAppliedDiscount(orderDiscount);
                System.out.println("✅ Applied order discount: " + orderDiscount.getName());
            }
        }
        
        // ✅ Set shipping discount if applied
        if (shippingDiscountId != null) {
            Discount shippingDiscount = discountRepository.findById(shippingDiscountId)
                .orElse(null);
            if (shippingDiscount != null) {
                order.setShippingDiscount(shippingDiscount);
                System.out.println("✅ Applied shipping discount: " + shippingDiscount.getName());
            }
        }
        
        // Create order details first, then detect flash sale
        Set<OrderDetail> orderDetails = new HashSet<>();
        
        // Convert selectedItemIds từ List<Integer> sang List<Long> để so sánh
        java.util.List<Long> selectedItemIdsLong = null;
        if (selectedItemIds != null) {
            selectedItemIdsLong = selectedItemIds.stream()
                .map(Integer::longValue)
                .collect(java.util.stream.Collectors.toList());
            System.out.println("Selected cart item IDs (converted to Long): " + selectedItemIdsLong);
        }
        
        // 🔥 Auto-detect flash sale from selected cart items
        FlashSale detectedFlashSale = null;
        System.out.println("🔍 DEBUG (Cart): flashSaleId parameter = " + flashSaleId);

        for (CartDetail cartDetail : cart.getCartDetails()) {
            System.out.println("Checking cart detail ID: " + cartDetail.getId() + " (type: Long)");
            
            // Chỉ thêm vào order nếu item được chọn
            if (selectedItemIdsLong == null || selectedItemIdsLong.contains(cartDetail.getId())) {

                // 🔥 Check for flash sale in this cart item
                if (detectedFlashSale == null) {
                    ProductDetail productDetail = cartDetail.getProduct();
                    if (productDetail != null) {
                        try {
                            // Use getActiveFlashSaleItem() method from ProductDetail
                            FlashSaleItem activeItem = productDetail.getActiveFlashSaleItem();
                            if (activeItem != null && activeItem.getFlashSale() != null && activeItem.getFlashSale().isActive()) {
                                detectedFlashSale = activeItem.getFlashSale();
                                System.out.println("🔥 Detected Flash Sale from cart item: " + detectedFlashSale.getName() + " (ID: " + detectedFlashSale.getId() + ")");
                            }
                        } catch (Exception e) {
                            System.out.println("⚠️ Could not load flash sale for product detail: " + e.getMessage());
                        }
                    }
                }
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(cartDetail.getProduct());
                orderDetail.setQuantity(cartDetail.getQuantity());
                orderDetail.setPrice(cartDetail.getPricePerUnit());
                
                orderDetails.add(orderDetail);
                System.out.println("✅ Added order detail: cartDetailId=" + cartDetail.getId() + 
                                 ", productDetailId=" + cartDetail.getProduct().getId() + 
                                 ", quantity=" + cartDetail.getQuantity() + 
                                 ", pricePerUnit=" + cartDetail.getPricePerUnit());
            } else {
                System.out.println("⏭️  Skipped cart detail ID: " + cartDetail.getId() + " (not selected)");
            }
        }
        
        order.setOrderDetailSet(orderDetails);

        // 🔥 Set flash sale if detected
        if (detectedFlashSale != null) {
            order.setAppliedFlashSale(detectedFlashSale);
            System.out.println("🔥 Applied Flash Sale to order: " + detectedFlashSale.getName() + " (ID: " + detectedFlashSale.getId() + ")");
        } else {
            System.out.println("⚠️ No flash sale detected in cart items");
        }

        // ✅ Calculate and set discount amounts, original price
        calculateAndSetOrderPricing(order, subtotal, shippingFee, orderDiscountAmount, shippingDiscountAmount);

        System.out.println("Order created with " + orderDetails.size() + " items");
        
        return order;
    }
    
    private void removeSelectedItemsFromCart(Cart cart, java.util.List<Integer> selectedItemIds) {
        System.out.println("Removing selected items from cart...");
        
        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
            // Nếu không có selectedItemIds, xóa toàn bộ (backward compatibility)
            cart.getCartDetails().clear();
            // totalPrice is now calculated automatically via getTotalPrice() method
        } else {
            // ✅ Convert Integer -> Long để so sánh
            java.util.List<Long> selectedItemIdsLong = selectedItemIds.stream()
                .map(Integer::longValue)
                .collect(java.util.stream.Collectors.toList());
            
            System.out.println("Removing cart item IDs: " + selectedItemIdsLong);
            
            // Tạo list items cần xóa (tránh ConcurrentModificationException)
            java.util.List<CartDetail> toRemove = new java.util.ArrayList<>();
            for (CartDetail detail : cart.getCartDetails()) {
                if (selectedItemIdsLong.contains(detail.getId())) {
                    toRemove.add(detail);
                    System.out.println("  - Marked for removal: cart_detail_id=" + detail.getId());
                }
            }
            
            // Xóa các items
            cart.getCartDetails().removeAll(toRemove);
            
            System.out.println("✅ Removed " + toRemove.size() + " items from cart");
        }
        
        cartRepository.save(cart);
    }
    
    @Autowired
    private PayOS payOS;

    private String generatePayOSUrl(Order order) {
        // Use PayOS directly to create payment link
        try {
                String productName = "Đơn hàng #" + order.getId();
                String description = "Thanh toán đơn hàng từ DeeG Shop";
                String returnUrl = "http://localhost:8081/user/order/view";
                String cancelUrl = "http://localhost:8081/cart/view";
                long price = order.getTotalPrice().longValue();
                long orderCode = System.currentTimeMillis() / 1000;

            PaymentLinkItem item = PaymentLinkItem.builder()
                .name(productName)
                .quantity(1)
                .price(price)
                .build();

            CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .description(description)
                .amount(price)
                .item(item)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();

                CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentData);

                // Return checkout URL or QR code
                if (response.getCheckoutUrl() != null) {
                    return response.getCheckoutUrl();
                } else if (response.getQrCode() != null) {
                    return response.getQrCode();
                }

                return null;
        } catch (Exception e) {
            System.out.println("PayOS Error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Gửi thông báo real-time đến admin khi có đơn hàng mới
     * Sử dụng WebSocket để push notification ngay lập tức
     */
    private void sendOrderNotificationToAdmin(Order order) {
        try {
            System.out.println("=== Building notification for order #" + order.getId() + " ===");
            
            // Build notification DTO
            OrderNotificationDTO notification = OrderNotificationDTO.builder()
                .orderId(order.getId())
                .customerName(order.getUser().getFullname())
                .customerEmail(order.getUser().getEmail())
                .totalPrice(order.getTotalPrice())
                .payOption(order.getPayOption() != null ? order.getPayOption().toString() : "UNKNOWN")
                .itemCount(order.getOrderDetailSet() != null ? order.getOrderDetailSet().size() : 0)
                .createdDate(order.getCreatedDate())
                .deliveryAddress(buildDeliveryAddressString(order.getAddress()))
                .build();
            
            // Send notification via WebSocket
            notificationService.sendNewOrderNotification(notification);
            
            System.out.println("✅ WebSocket notification sent successfully to admin");
            
        } catch (Exception e) {
            // Log error but don't throw - notification failure shouldn't break order creation
            System.err.println("⚠️ Failed to send WebSocket notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Build delivery address string from Address entity
     */
    private String buildDeliveryAddressString(Address address) {
        if (address == null) {
            return "N/A";
        }
        
        // Build address string from available fields
        StringBuilder addressBuilder = new StringBuilder();
        
        if (address.getAddress_line() != null && !address.getAddress_line().isEmpty()) {
            addressBuilder.append(address.getAddress_line());
        }
        
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(address.getCity());
        }
        
        if (address.getCountry() != null && !address.getCountry().isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(address.getCountry());
        }
        
        return addressBuilder.length() > 0 ? addressBuilder.toString() : "N/A";
    }
    
    private Cart createNewCartForUser(Long userId) {
        try {
            Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            
            Cart newCart = new Cart();
            newCart.setUser(user);
            // totalPrice is calculated automatically via getTotalPrice() method
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
                    // totalPrice is calculated automatically via getTotalPrice() method
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
            cartDetail.setPricePerUnit(pricePerUnit); // Update price in case it changed
            cartDetailRepository.save(cartDetail);
            System.out.println("Updated existing cart detail, new quantity: " + newQuantity);
        } else {
            // Create new cart detail
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setProduct(productDetail);
            cartDetail.setQuantity(quantity);
            cartDetail.setPricePerUnit(pricePerUnit);
            cartDetailRepository.save(cartDetail);
            System.out.println("Created new cart detail");
        }
        
        // Update cart total price
        updateCartTotalPrice(cart.getId());
    }
    
    /**
     * Update cart - no longer needed since totalPrice is calculated automatically
     * Kept for backward compatibility, but does nothing
     */
    @Transactional
    public void updateCartTotalPrice(Long cartId) {
        // No-op: totalPrice is calculated automatically via getTotalPrice() method
    }
    
    /**
     * Convert Order to OrderDTO with detailed information
     */
    private OrderDTO convertToOrderDetailDTO(Order order) {
        OrderDTO dto = orderDTOConverter.toOrderDTO(order);
        
        // Convert order details
        if (order.getOrderDetailSet() != null) {
            List<OrderDetailDTO> orderDetailDTOs = order.getOrderDetailSet().stream()
                    .map(this::convertOrderDetailToDTO)
                    .collect(Collectors.toList());
            dto.setOrderDetails(orderDetailDTOs);
        }
        
        return dto;
    }
    
    /**
     * Convert OrderDetail to OrderDetailDTO
     */
    private OrderDetailDTO convertOrderDetailToDTO(OrderDetail orderDetail) {
        OrderDetailDTO dto = OrderDetailDTO.builder()
                .id((long) orderDetail.getId())
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .build();
        
        // Convert ProductDetail
        if (orderDetail.getProduct() != null) {
            OrderDetailDTO.ProductDetailDTO productDetailDTO = OrderDetailDTO.ProductDetailDTO.builder()
                    .id(orderDetail.getProduct().getId())
                    .size(orderDetail.getProduct().getSize())
                    .priceadd(orderDetail.getProduct().getPriceadd())
                    .build();
            
            // Convert Product info
            if (orderDetail.getProduct().getProduct() != null) {
                OrderDetailDTO.ProductDetailDTO.ProductInfo productInfo = OrderDetailDTO.ProductDetailDTO.ProductInfo.builder()
                        .id(orderDetail.getProduct().getProduct().getId())
                        .title(orderDetail.getProduct().getProduct().getTitle())
                        .description(orderDetail.getProduct().getProduct().getDescription())
                        .price(orderDetail.getProduct().getProduct().getPrice())
                        .image(orderDetail.getProduct().getProduct().getImage())
                        .build();
                
                // Add brand name
                if (orderDetail.getProduct().getProduct().getBrand() != null) {
                    productInfo.setBrandName(orderDetail.getProduct().getProduct().getBrand().getName());
                }
                
                // Add category name
                if (orderDetail.getProduct().getProduct().getCategory() != null) {
                    productInfo.setCategoryName(orderDetail.getProduct().getProduct().getCategory().getName());
                }
                
                productDetailDTO.setProduct(productInfo);
            }
            
            dto.setProductDetail(productDetailDTO);
        }
        
        // Check if this order detail has been rated
        // Note: We need to get the user from the order context
        // For now, we'll set it to false and handle it in the controller
        dto.setHasRating(false);
        
        return dto;
    }
    
    // ========== MVC Pattern: Methods cho Controller ==========
    
    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, ShipmentStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        ShipmentStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        orderRepository.save(order);
        
        // ✅ Trigger đã chạy tự động sau save()
        // → Broadcast updates qua WebSocket theo LOGIC MỚI
        
        // 📧 GỬI EMAIL KHI CHUYỂN SANG SHIPPED
        if (newStatus == ShipmentStatus.SHIPPED && oldStatus != ShipmentStatus.SHIPPED) {
            try {
                // Force load OrderDetails và User để tránh LazyInitializationException
                if (order.getOrderDetailSet() != null) {
                    order.getOrderDetailSet().size(); // Trigger lazy loading
                }
                // Gửi email async (không block request)
                emailService.sendOrderShippedEmail(order);
                System.out.println("📧 Order shipped email triggered for order #" + orderId);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to send order shipped email: " + e.getMessage());
                // Don't throw - email failure shouldn't break order status update
            }
        }

        // CASE 1: DELIVERED → Tăng sold_quantity (trigger đã chạy)
        if (newStatus == ShipmentStatus.DELIVERED && oldStatus != ShipmentStatus.DELIVERED) {
            // Chỉ broadcast sold_quantity (inventory không đổi)
            broadcastSoldQuantityOnly(order, "INCREASE");
        }
        
        // CASE 2: CANCEL → Hoàn kho (trigger đã chạy)
        else if (newStatus == ShipmentStatus.CANCEL && oldStatus != ShipmentStatus.CANCEL) {
            // Chỉ broadcast inventory (sold_quantity chưa tăng nên không giảm)
            broadcastInventoryOnly(order, "INCREASE");

            // ✅ Giảm Flash Sale totalSold khi CANCEL
            decrementFlashSaleTotalSold(order);
        }
        
        // CASE 3: RETURN (from DELIVERED) → Hoàn kho + Giảm sold
        else if (newStatus == ShipmentStatus.RETURN && oldStatus == ShipmentStatus.DELIVERED) {
            // Broadcast cả inventory và sold_quantity
            broadcastInventoryOnly(order, "INCREASE");
            broadcastSoldQuantityOnly(order, "DECREASE");

            // ✅ Giảm Flash Sale totalSold khi RETURN
            decrementFlashSaleTotalSold(order);
        }
    }
    
    @Override
    @Transactional
    public void markOrderAsDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        ShipmentStatus oldStatus = order.getStatus();
        order.setStatus(ShipmentStatus.DELIVERED);
        orderRepository.save(order);
        
        // ✅ Update sold_quantity when marking as delivered
        if (oldStatus != ShipmentStatus.DELIVERED) {
            updateProductSoldQuantity(order, true);
            System.out.println("✅ Updated sold_quantity for delivered order ID: " + orderId);
        }
    }
    
    @Override
    @Transactional
    public void markOrderAsReturn(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        order.setStatus(ShipmentStatus.RETURN);
        orderRepository.save(order);
    }
    
    // ========== HELPER METHOD: Update Product Sold Quantity ==========
    
    /**
     * Update sold_quantity for all products in the order
     * @param order The order containing products
     * @param increase true to increase, false to decrease
     */
    private void updateProductSoldQuantity(Order order, boolean increase) {
        if (order.getOrderDetailSet() == null || order.getOrderDetailSet().isEmpty()) {
            return;
        }
        
        System.out.println("=== Updating Product Sold Quantity ===");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Action: " + (increase ? "INCREASE" : "DECREASE"));
        
        // Group order details by product to sum quantities
        Map<Long, Integer> productQuantities = new HashMap<>();
        
        for (OrderDetail orderDetail : order.getOrderDetailSet()) {
            ProductDetail productDetail = orderDetail.getProduct();
            if (productDetail != null && productDetail.getProduct() != null) {
                Long productId = productDetail.getProduct().getId();
                int quantity = orderDetail.getQuantity();
                
                productQuantities.put(
                    productId, 
                    productQuantities.getOrDefault(productId, 0) + quantity
                );
            }
        }
        
        // Update each product's sold_quantity
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            int totalQuantity = entry.getValue();
            
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                long currentSold = product.getSoldQuantity() != null ? product.getSoldQuantity() : 0L;
                long newSold;
                
                if (increase) {
                    newSold = currentSold + totalQuantity;
                } else {
                    newSold = Math.max(0, currentSold - totalQuantity); // Don't go below 0
                }
                
                product.setSoldQuantity(newSold);
                productRepository.save(product);
                
                System.out.println("  Product ID " + productId + ": " + 
                                 currentSold + " -> " + newSold + 
                                 " (qty: " + totalQuantity + ")");
            }
        }
        
        System.out.println("=== Sold Quantity Update Complete ===");
    }
    
    /**
     * Broadcast CHỈ inventory updates qua WebSocket
     * Được gọi khi CANCEL hoặc RETURN (hoàn kho)
     * 
     * @param order Order đã được update status
     * @param updateType "INCREASE" (hoàn kho)
     */
    private void broadcastInventoryOnly(Order order, String updateType) {
        System.out.println("=== Broadcasting INVENTORY Updates via WebSocket ===");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Update Type: " + updateType);
        
        for (OrderDetail detail : order.getOrderDetailSet()) {
            ProductDetail productDetail = detail.getProduct();
            Product product = productDetail.getProduct();
            
            // Fetch inventory mới từ DB (sau khi trigger đã chạy)
            int newInventory = inventoryRepository.getTotalQuantityByProductDetail(productDetail);
            
            // Broadcast inventory update
            InventoryUpdateDTO inventoryUpdate = InventoryUpdateDTO.builder()
                    .productDetailId(productDetail.getId())
                    .productId(product.getId())
                    .productTitle(product.getTitle())
                    .size(productDetail.getSize())
                    .newQuantity(newInventory)
                    .updateType(updateType)
                    .orderId(order.getId())
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            notificationService.sendInventoryUpdate(inventoryUpdate);
        }
        
        System.out.println("✅ Inventory broadcast complete");
    }
    
    /**
     * Broadcast CHỈ sold_quantity updates qua WebSocket
     * Được gọi khi DELIVERED (tăng sold) hoặc RETURN (giảm sold)
     * 
     * @param order Order đã được update status
     * @param updateType "INCREASE" (DELIVERED) hoặc "DECREASE" (RETURN)
     */
    private void broadcastSoldQuantityOnly(Order order, String updateType) {
        System.out.println("=== Broadcasting SOLD QUANTITY Updates via WebSocket ===");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Update Type: " + updateType);
        
        for (OrderDetail detail : order.getOrderDetailSet()) {
            ProductDetail productDetail = detail.getProduct();
            Product product = productDetail.getProduct();
            
            // Fetch sold_quantity mới từ DB (sau khi trigger đã chạy)
            Product updatedProduct = productRepository.findById(product.getId())
                    .orElse(product);
            
            // Broadcast sold_quantity update
            SoldQuantityUpdateDTO soldQuantityUpdate = SoldQuantityUpdateDTO.builder()
                    .productId(updatedProduct.getId())
                    .productTitle(updatedProduct.getTitle())
                    .soldQuantity(updatedProduct.getSoldQuantity())
                    .updateType(updateType)
                    .orderId(order.getId())
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            notificationService.sendSoldQuantityUpdate(soldQuantityUpdate);
        }
        
        System.out.println("✅ Sold quantity broadcast complete");
    }
    
    /**
     * Broadcast inventory update SAU KHI ĐẶT HÀNG MỚI
     * Được gọi sau khi trigger after_order_detail_insert đã trừ kho
     * 
     * @param order Order vừa được tạo
     */
    private void broadcastInventoryAfterOrderCreation(Order order) {
        System.out.println("\n=== Broadcasting INVENTORY After Order Creation ===");
        System.out.println("Order ID: " + order.getId());
        
        if (order.getOrderDetailSet() == null || order.getOrderDetailSet().isEmpty()) {
            System.err.println("❌ ERROR: Order has NO order details! Cannot broadcast.");
            return;
        }
        
        System.out.println("Processing " + order.getOrderDetailSet().size() + " order details...");
        
        for (OrderDetail detail : order.getOrderDetailSet()) {
            ProductDetail productDetail = detail.getProduct();
            Product product = productDetail.getProduct();
            
            // Fetch inventory mới từ DB (sau khi trigger đã trừ)
            int newInventory = inventoryRepository.getTotalQuantityByProductDetail(productDetail);
            
            // Broadcast inventory update với updateType = "DECREASE" (đã trừ kho)
            InventoryUpdateDTO inventoryUpdate = InventoryUpdateDTO.builder()
                    .productDetailId(productDetail.getId())
                    .productId(product.getId())
                    .productTitle(product.getTitle())
                    .size(productDetail.getSize())
                    .newQuantity(newInventory)
                    .updateType("DECREASE") // Đặt hàng → trừ kho
                    .orderId(order.getId())
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            notificationService.sendInventoryUpdate(inventoryUpdate);
            
            System.out.println("  Broadcasted: " + product.getTitle() + " (Size " + productDetail.getSize() + ") → " + newInventory);
        }
        
        System.out.println("✅ Inventory broadcast after order creation complete");
    }

    /**
     * Calculate and set order pricing (discounts, original price)
     * Use values from frontend for accuracy
     */
    private void calculateAndSetOrderPricing(Order order, Double subtotal, Double shippingFee,
                                             Double orderDiscountAmount, Double shippingDiscountAmount) {
        System.out.println("=== Setting Order Pricing from Frontend ===");

        // Use values from frontend (already calculated accurately)
        if (subtotal != null && shippingFee != null) {
            // Calculate original total (before any discounts)
            double originalTotalPrice = subtotal + shippingFee;
            order.setOriginalTotalPrice(originalTotalPrice);
            System.out.println("Original total price: " + originalTotalPrice);
            System.out.println("  = Subtotal: " + subtotal + " + Shipping: " + shippingFee);
        }

        // Set order discount amount
        if (orderDiscountAmount != null && orderDiscountAmount > 0) {
            order.setDiscountAmount(orderDiscountAmount);
            System.out.println("Order discount amount: " + orderDiscountAmount);
        }

        // Set shipping discount amount
        if (shippingDiscountAmount != null && shippingDiscountAmount > 0) {
            order.setShippingDiscountAmount(shippingDiscountAmount);
            System.out.println("Shipping discount amount: " + shippingDiscountAmount);
        }

        // Verify final total
        System.out.println("Final total (from frontend): " + order.getTotalPrice());

        System.out.println("=== Order Pricing Set Complete ===");
    }

    /**
     * Track discount usage - Tạo DiscountUsed records khi user apply voucher
     */
    private void trackDiscountUsage(Order order, Long orderDiscountId, Long shippingDiscountId) {
        System.out.println("=== Tracking Discount Usage ===");

        Users user = order.getUser();
        Long orderId = order.getId();

        // Track order discount usage
        if (orderDiscountId != null && order.getAppliedDiscount() != null) {
            try {
                Discount orderDiscount = order.getAppliedDiscount();
                Double discountAmount = order.getDiscountAmount() != null ? order.getDiscountAmount() : 0.0;

                DiscountUsed discountUsed = new DiscountUsed(user, orderDiscount, orderId, discountAmount);
                discountUsedRepository.save(discountUsed);

                System.out.println("✅ Tracked order discount usage: " + orderDiscount.getName() +
                                 " by user " + user.getId() + " for order " + orderId);
            } catch (Exception e) {
                System.err.println("❌ Error tracking order discount usage: " + e.getMessage());
            }
        }

        // Track shipping discount usage
        if (shippingDiscountId != null && order.getShippingDiscount() != null) {
            try {
                Discount shippingDiscount = order.getShippingDiscount();
                Double discountAmount = order.getShippingDiscountAmount() != null ? order.getShippingDiscountAmount() : 0.0;

                DiscountUsed discountUsed = new DiscountUsed(user, shippingDiscount, orderId, discountAmount);
                discountUsedRepository.save(discountUsed);

                System.out.println("✅ Tracked shipping discount usage: " + shippingDiscount.getName() +
                                 " by user " + user.getId() + " for order " + orderId);
            } catch (Exception e) {
                System.err.println("❌ Error tracking shipping discount usage: " + e.getMessage());
            }
        }

        System.out.println("=== Discount Usage Tracking Complete ===");
    }

    /**
     * Tăng Flash Sale totalSold khi tạo order mới
     */
    private void incrementFlashSaleTotalSold(Order order) {
        if (order.getAppliedFlashSale() != null) {
            FlashSale flashSale = order.getAppliedFlashSale();

            // Tính tổng số lượng sản phẩm trong order
            int totalQuantity = 0;
            if (order.getOrderDetailSet() != null) {
                totalQuantity = order.getOrderDetailSet().stream()
                    .mapToInt(detail -> detail.getQuantity())
                    .sum();
            }

            if (totalQuantity > 0) {
                flashSale.incrementSold(totalQuantity);
                flashSaleRepository.save(flashSale);
                System.out.println("🔥 Increased Flash Sale '" + flashSale.getName() + "' totalSold by " + totalQuantity +
                                 " (New Order #" + order.getId() + ")");
            }
        }
    }

    /**
     * Giảm Flash Sale totalSold khi order bị CANCEL hoặc RETURN
     */
    private void decrementFlashSaleTotalSold(Order order) {
        if (order.getAppliedFlashSale() != null) {
            FlashSale flashSale = order.getAppliedFlashSale();

            // Tính tổng số lượng sản phẩm trong order
            int totalQuantity = 0;
            if (order.getOrderDetailSet() != null) {
                totalQuantity = order.getOrderDetailSet().stream()
                    .mapToInt(detail -> detail.getQuantity())
                    .sum();
            }

            if (totalQuantity > 0) {
                flashSale.decrementSold(totalQuantity);
                flashSaleRepository.save(flashSale);
                System.out.println("🔥 Decreased Flash Sale '" + flashSale.getName() + "' totalSold by " + totalQuantity +
                                 " (Order #" + order.getId() + " CANCELLED/RETURNED)");
            }
        }
    }

    @Override
    @Transactional
    public void updatePayOSPaymentInfo(Long orderId, Long payosOrderCode, String paymentStatus, Date paidAt) {
        System.out.println("=== Updating PayOS payment info for order: " + orderId + " ===");

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setPayosOrderCode(payosOrderCode);
        order.setPaymentStatus(paymentStatus);
        order.setPaidAt(paidAt);

        orderRepository.save(order);

        System.out.println("✅ PayOS payment info updated:");
        System.out.println("  - PayOS Order Code: " + payosOrderCode);
        System.out.println("  - Payment Status: " + paymentStatus);
        System.out.println("  - Paid At: " + paidAt);
    }
}
