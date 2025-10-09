package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.Role;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.RoleRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");
        
        log.info("Processing OAuth2 user: {}", email);
        
        Users user = userRepository.findByEmail(email);
        
        if (user == null) {
            // Tạo user mới từ Google account
            user = createNewUser(email, name, picture);
            log.info("Created new user from Google: {}", email);
        } else {
            // Cập nhật thông tin user nếu cần
            updateExistingUser(user, name, picture);
            log.info("Updated existing user: {}", email);
        }
        
        return new CustomOAuth2User(oauth2User, user);
    }
    
    private Users createNewUser(String email, String name, String picture) {
        // Lấy role mặc định cho user (role_id = 3 hoặc role_name = "user")
        Role userRole = roleRepository.findByRoleName("user")
                .orElseThrow(() -> new RuntimeException("User role not found"));
        
        Users newUser = Users.builder()
                .email(email)
                .fullname(name != null ? name : "Google User")
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Random password
                .role(userRole)
                .isActive(true)
                .profilePicture(picture)
                .provider("GOOGLE")
                .build();
        
        return userRepository.save(newUser);
    }
    
    private void updateExistingUser(Users user, String name, String picture) {
        boolean updated = false;
        
        if (name != null && !name.equals(user.getFullname())) {
            user.setFullname(name);
            updated = true;
        }
        
        if (picture != null && !picture.equals(user.getProfilePicture())) {
            user.setProfilePicture(picture);
            updated = true;
        }
        
        if (updated) {
            userRepository.save(user);
        }
    }
}
