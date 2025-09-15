package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // Constructor injection (final): rõ ràng, testable, clean code → được khuyến nghị.
    private final UserRepository userRepository;

    @Override
    public Users findUserByEmail(String email) {
        System.out.println(email);
        return userRepository.findByEmail(email);
    }
}
