package com.dev.shoeshop.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "products",       // Cache cho product list (trang chủ, search, filter)
            "productDetail",  // Cache cho product detail page
            "categories"      // Cache cho categories dropdown
        );
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
            // Cache expiration: 5 phút
            // Sau 5 phút, cache tự động xóa và query DB lại để get fresh data
            .expireAfterWrite(10, TimeUnit.MINUTES)

            .maximumSize(100)

            .recordStats());
        
        return cacheManager;
    }
}
