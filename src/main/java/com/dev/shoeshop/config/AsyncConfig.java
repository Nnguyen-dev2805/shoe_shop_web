package com.dev.shoeshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Enable @Async annotation for asynchronous method execution
    // Email sending will be non-blocking
}
