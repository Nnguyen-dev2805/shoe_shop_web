package com.dev.shoeshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration for Shoe Shop API Documentation
 * 
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access API Docs JSON at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API Information
                .info(new Info()
                        .title("🛍️ Shoe Shop API Documentation")
                        .version("1.0.0")
                        .description("""
                                RESTful API documentation for Shoe Shop Web Application.
                                
                                **Features:**
                                - 🔐 Authentication & Authorization (Form Login + OAuth2 + JWT)
                                - 🛒 Shopping Cart Management
                                - 📦 Product & Inventory Management
                                - 💳 Payment Integration (PayOS)
                                - 🎫 Voucher & Flash Sale System
                                - 🚚 Shipping & Order Tracking
                                - ⭐ Product Rating & Reviews
                                - 💬 Real-time Chat (WebSocket)
                                - 🏆 Membership & Loyalty Points
                                
                                **Authentication Methods:**
                                1. **Form Login** (Web): Session-based authentication
                                2. **OAuth2** (Google): Social login
                                3. **JWT** (API/Mobile): Bearer token authentication
                                """)
                        .contact(new Contact()
                                .name("Shoe Shop Dev Team")
                                .email("dev@shoeshop.com")
                                .url("https://github.com/Nnguyen-dev2805/shoe_shop_web"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                
                // Servers
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://shoeshop-production.up.railway.app")
                                .description("Production Server")
                ))
                
                // Security Schemes
                .components(new Components()
                        // JWT Bearer Token Authentication
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT Bearer token for API authentication"))
                        
                        // Session Cookie Authentication (for web endpoints)
                        .addSecuritySchemes("session-cookie", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")
                                .description("Session-based authentication via JSESSIONID cookie"))
                )
                
                // Global Security Requirement (áp dụng cho tất cả endpoints)
                // Có thể override ở từng controller/method bằng @SecurityRequirement
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt")
                        .addList("session-cookie"));
    }
}
