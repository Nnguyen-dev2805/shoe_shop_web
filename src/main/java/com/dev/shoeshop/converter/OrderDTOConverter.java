package com.dev.shoeshop.converter;

import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Users;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class OrderDTOConverter {
    @Autowired
    private ModelMapper modelMapper;

    public OrderDTO toOrderDTO(Order order) {
        // Map cơ bản bằng ModelMapper
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        // Custom mapping: userName
        Users user = order.getUser();
        if (user != null) {
            dto.setUserName(user.getFullname());
        } else {
            dto.setUserName("Unknown");
        }
        return dto;
    }
}
