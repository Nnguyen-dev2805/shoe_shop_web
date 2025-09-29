package com.dev.shoeshop.converter;

import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Users;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTOConverter {

    @Autowired
    private ModelMapper modelMapper;

    public UserDTO toUserDTO(Users user) {
        if (user == null) return null;
        return modelMapper.map(user, UserDTO.class);
    }
}
