package com.dev.shoeshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=25",
    "spring.mail.username=test",
    "spring.mail.password=test"
})
public class PasswordResetIntegrationTest {

    @Test
    public void contextLoads() {
        // This test will pass if the Spring context loads successfully
        // It validates that all our password reset components are properly configured
    }
}
