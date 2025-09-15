package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// cầu nối giữa database và Spring Security
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserServiceImpl userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userService.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Can not find email");
        }
        // User này chính là một implementation mặc định của UserDetails
        // Một User mà Spring sẽ dùng trong quá trình xác thực
        // Đoạn này nếu hay thì dùng Mapper
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getRoleName())
                .build();
    }
}
