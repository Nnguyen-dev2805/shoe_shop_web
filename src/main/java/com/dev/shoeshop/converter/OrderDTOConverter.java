package com.dev.shoeshop.converter;

import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.Users;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class OrderDTOConverter {

    public OrderDTO toOrderDTO(Order order) {
        // Manual mapping to avoid lazy loading issues with ModelMapper
        OrderDTO dto = new OrderDTO();
        
        // Basic fields
        dto.setId(order.getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedDate(order.getCreatedDate());
        dto.setStatus(order.getStatus());
        dto.setPayOption(order.getPayOption());
        
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
