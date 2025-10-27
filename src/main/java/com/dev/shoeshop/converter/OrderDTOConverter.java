package com.dev.shoeshop.converter;

import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.*;
import com.dev.shoeshop.repository.ShipmentRepository;
import com.dev.shoeshop.repository.UserAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class OrderDTOConverter {
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private UserAddressRepository userAddressRepository;

    public OrderDTO toOrderDTO(Order order) {
        // Manual mapping to avoid lazy loading issues with ModelMapper
        OrderDTO dto = new OrderDTO();
        
        // Basic fields
        dto.setId(order.getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedDate(order.getCreatedDate());
        dto.setStatus(order.getStatus());
        dto.setPayOption(order.getPayOption());
        
        // Address (convert to simple DTO) + get recipient info from UserAddress
        if (order.getAddress() != null && order.getUser() != null) {
            OrderDTO.AddressDTO.AddressDTOBuilder addressBuilder = OrderDTO.AddressDTO.builder()
                .id(order.getAddress().getId())
                .addressLine(order.getAddress().getAddress_line())
                .city(order.getAddress().getCity())
                .country(order.getAddress().getCountry());
            
            // Query UserAddress để lấy recipientName và recipientPhone
            try {
                Optional<UserAddress> userAddress = userAddressRepository.findByUserIdAndAddressId(
                    order.getUser().getId(), 
                    order.getAddress().getId()
                );
                
                if (userAddress.isPresent()) {
                    addressBuilder
                        .recipientName(userAddress.get().getRecipientName())
                        .recipientPhone(userAddress.get().getRecipientPhone());
                }
            } catch (Exception e) {
                // UserAddress not found, use default user info
                addressBuilder
                    .recipientName(order.getUser().getFullname())
                    .recipientPhone(order.getUser().getPhone());
            }
            
            dto.setAddress(addressBuilder.build());
        }
        
        // Discount & Voucher (only essential data)
        if (order.getAppliedDiscount() != null) {
            dto.setDiscountName(order.getAppliedDiscount().getName());
        }
        dto.setDiscountAmount(order.getDiscountAmount());
        
        if (order.getShippingDiscount() != null) {
            dto.setShippingDiscountName(order.getShippingDiscount().getName());
        }
        dto.setShippingFee(order.getShippingFee());
        dto.setShippingDiscountAmount(order.getShippingDiscountAmount());
        dto.setOriginalTotalPrice(order.getOriginalTotalPrice());
        
        // Payment
        dto.setPaidAt(order.getPaidAt());
        dto.setPaymentStatus(order.getPaymentStatus());
        
        // Loyalty Points
        dto.setPointsRedeemed(order.getPointsRedeemed());
        dto.setPointsEarned(order.getPointsEarned());
        
        // Shipment (only updatedDate, no entity)
        try {
            Shipment shipment = shipmentRepository.findByOrderId(order.getId());
            if (shipment != null) {
                dto.setShipmentUpdatedDate(shipment.getUpdatedDate());
                dto.setShipmentStatus(shipment.getStatus());
            }
        } catch (Exception e) {
            // Shipment chưa có
            dto.setShipmentUpdatedDate(null);
            dto.setShipmentStatus(null);
        }
        
        // User mapping
        Users user = order.getUser();
        if (user != null) {
            dto.setUserName(user.getFullname());
            
            // Create UserDTO without triggering lazy loads
            UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address("1 VVN") // TODO: fix address mapping
                .build();
            dto.setUser(userDTO);
        } else {
            dto.setUserName("Unknown");
        }
        
        // OrderDetails mapping (note: Order entity uses 'orderDetailSet', not 'orderDetails')
        if (order.getOrderDetailSet() != null && !order.getOrderDetailSet().isEmpty()) {
            List<OrderDetailDTO> orderDetailDTOs = order.getOrderDetailSet().stream()
                .map(this::toOrderDetailDTO)
                .collect(Collectors.toList());
            dto.setOrderDetails(orderDetailDTOs);
        } else {
            dto.setOrderDetails(new ArrayList<>());
        }
        
        return dto;
    }
    
    /**
     * Convert OrderDetail entity to OrderDetailDTO
     */
    private OrderDetailDTO toOrderDetailDTO(OrderDetail orderDetail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        
        // Note: OrderDetail.id is int, need to cast to Long
        dto.setId((long) orderDetail.getId());
        dto.setQuantity(orderDetail.getQuantity());
        dto.setPrice(orderDetail.getPrice());
        dto.setOriginalPrice(orderDetail.getOriginalPrice()); // ✅ Map giá gốc
        // Calculate amount (price * quantity)
        dto.setAmount(orderDetail.getPrice() * orderDetail.getQuantity());
        
        // ProductDetail mapping (note: OrderDetail entity uses 'product' field, not 'productDetail')
        ProductDetail productDetail = orderDetail.getProduct();
        if (productDetail != null) {
            dto.setSize(productDetail.getSize());
            
            // Product mapping
            Product product = productDetail.getProduct();
            if (product != null) {
                dto.setProduct_name(product.getTitle()); // ✅ Product uses 'title', not 'productName'
                dto.setImage(product.getImage());
                
                // Create nested ProductDetailDTO
                OrderDetailDTO.ProductDetailDTO productDetailDTO = OrderDetailDTO.ProductDetailDTO.builder()
                    .id(productDetail.getId())
                    .size(productDetail.getSize())
                    .priceadd(productDetail.getPriceadd())
                    .product(OrderDetailDTO.ProductDetailDTO.ProductInfo.builder()
                        .id(product.getId())
                        .title(product.getTitle()) // ✅ Product uses 'title'
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .image(product.getImage())
                        .brandName(product.getBrand() != null ? product.getBrand().getName() : "")
                        .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                        .build())
                    .build();
                    
                dto.setProductDetail(productDetailDTO);
            }
        }
        
        return dto;
    }
}
