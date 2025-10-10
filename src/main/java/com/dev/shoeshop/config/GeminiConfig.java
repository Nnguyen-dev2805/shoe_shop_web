package com.dev.shoeshop.config;
import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {
    
    @Value("${gemini.api.key:}")
    private String apiKey;
    
    @Bean
    public Client geminiClient() {
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("Gemini API key is required. Set gemini.api.key in application.properties");
        }
        return Client.builder().apiKey(apiKey).build();
    }
}
