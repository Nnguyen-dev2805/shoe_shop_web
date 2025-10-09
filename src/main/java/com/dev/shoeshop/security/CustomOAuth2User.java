package com.dev.shoeshop.security;

import com.dev.shoeshop.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    
    private final OAuth2User oauth2User;
    private final Users user;
    
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName().toUpperCase())
        );
    }
    
    @Override
    public String getName() {
        return user.getEmail();
    }
    
    public Users getUser() {
        return user;
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public String getFullName() {
        return user.getFullname();
    }
    
    public String getProfilePicture() {
        return user.getProfilePicture();
    }
}
