package com.dev.shoeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShoeShopWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoeShopWebApplication.class, args);
    }

}
