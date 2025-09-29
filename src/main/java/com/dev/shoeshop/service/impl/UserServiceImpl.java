package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.converter.UserDTOConverter;
import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // Constructor injection (final): rõ ràng, testable, clean code → được khuyến nghị.
    private final UserRepository userRepository;
    @Autowired
    private UserRepository userRepository2;
    @Autowired
    private UserDTOConverter userDTOConverter;

    @Override
    public Users findUserByEmail(String email) {
        System.out.println(email);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDTO> getAllShipper(Long i) {
        List<Users> listUser = userRepository2.findByRoleRoleId(i);
        List<UserDTO> listUserDTO = new ArrayList<>();

        return listUser.stream()
                .map(userDTOConverter::toUserDTO)
                .toList(); // Java 16+
    }

    @Override
    public List<UserDTO> findByFullnameAndRole(String name, long roleId) {
        List<Users> listUser = userRepository2.findByRoleRoleIdAndFullnameContaining(roleId,name);
        return listUser.stream()
                .map(userDTOConverter::toUserDTO)
                .toList(); // Java 16+
    }
}
