package com.dev.shoeshop.converter;

import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Users;
import org.springframework.stereotype.Component;


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
        
        return dto;
    }
}
