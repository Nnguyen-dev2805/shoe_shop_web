package com.dev.shoeshop.utils;

import com.dev.shoeshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestDB implements CommandLineRunner {

    private final UserRepository userRepository;

    public TestDB(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== LIST OF USERS ===");
        userRepository.findAll().forEach(user ->
                System.out.println(
                        "ID: " + user.getId() +
                                ", Email: " + user.getEmail() +
                                ", Full Name: " + user.getFullname() +
                                ", Address: " + user.getAddress() +
                                ", Phone: " + user.getPhone() +
                                ", Role: " + (user.getRole() != null ? user.getRole().getRoleName() : "null")
                )
        );
        System.out.println("=====================");
    }
}
