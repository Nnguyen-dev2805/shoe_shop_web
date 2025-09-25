package com.dev.shoeshop.security;

import com.dev.shoeshop.service.impl.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailService userDetailService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        System.out.println("HAHAHAHHA");
        System.out.println(username);
        System.out.println(password);

        UserDetails user = userDetailService.loadUserByUsername(username);

        // In ra user để kiểm tra
//        System.out.println("UserDetails: " + user);
//        System.out.println("Username: " + user.getUsername());
//        System.out.println("Password (hashed): " + user.getPassword());
//        System.out.println("Authorities: " + user.getAuthorities());
        if(user == null) {
            System.out.println("Hahahahahahah");
        }

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
