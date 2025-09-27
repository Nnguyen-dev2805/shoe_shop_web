package com.dev.shoeshop.config;

import com.dev.shoeshop.security.CustomAuthenticationSuccessHandler;
import com.dev.shoeshop.security.CustomAuthenticationProvider;
import com.dev.shoeshop.service.impl.CustomUserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomUserDetailServiceImpl userDetailService; // service bạn đã viết

    private final String[] PUBLIC_ENDPOINT = {"/", "/login", "/register", "/product/**", "/category/**", "/send-code", "/reset_password", "/sendcode",
            "/verifycode", "/resetPassword", "/api/**"};
    private final String[] PUBLIC_CSS = {"/assets/**", "/css/**", "/fonts/**", "/img/**", "/js/**", "/lib/**",
            "/style.css", "/uploads/**"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/admin/**").hasRole("admin")
                        .requestMatchers("/manager/**").hasAnyRole("manager", "admin")
                        .requestMatchers("/shipper/**").hasRole("shipper")
                        .requestMatchers(PUBLIC_ENDPOINT).permitAll()
                        .requestMatchers(PUBLIC_CSS).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin.loginPage("/login")
                                .successHandler(successHandler)
                                .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error=true"))
                                .permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout").permitAll())
                .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler()))
                .csrf(AbstractHttpConfigurer::disable);

        // ⚡ quan trọng: đăng ký CustomAuthenticationProvider
        httpSecurity.authenticationProvider(authenticationProvider());

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(userDetailService, passwordEncoder());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> response.sendRedirect("/access-denied");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

