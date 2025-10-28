package com.dev.shoeshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.dev-url:http://localhost:8080}")
    private String devUrl;

    @Value("${openapi.prod-url:https://your-production-url.com}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("dev@shoeshop.com");
        contact.setName("Shoe Shop Development Team");
        contact.setUrl("https://github.com/Nnguyen-dev2805/shoe_shop_web");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("DeeG Shoe Shop API Documentation")
                .version("1.0.0")
                .contact(contact)
                .description("""
                    # DeeG Shoe Shop RESTful API
                    
                    Comprehensive API documentation for DeeG Shoe Shop E-commerce Platform.
                    
                    ## Features
                    - 🛒 **Shopping Cart Management**: Add, update, remove items from cart
                    - 💳 **Payment Integration**: PayOS and COD payment methods
                    - ⚡ **Flash Sales**: Time-limited special offers with real-time stock tracking
                    - 🎟️ **Vouchers & Discounts**: Order and shipping vouchers
                    - ⭐ **Product Ratings**: Customer reviews and ratings system
                    - 📦 **Order Management**: Complete order lifecycle management
                    
                    ## Authentication
                    Most endpoints require user authentication via session.
                    Use the login page to authenticate before accessing protected endpoints.
                    
                    ## Rate Limiting
                    Please limit your requests to avoid server overload.
                    Flash sale endpoints are optimized for high concurrency.
                    """)
                .termsOfService("https://www.shoeshop.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
