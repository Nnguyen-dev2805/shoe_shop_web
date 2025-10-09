package com.dev.shoeshop.security;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        Users user = oauth2User.getUser();
        
        log.info("OAuth2 authentication success for user: {}", user.getEmail());
        
        // Lưu user vào session như form login
        HttpSession session = request.getSession();
        session.setAttribute(Constant.SESSION_USER, user);
        
        // Chuyển hướng đến waiting page để phân quyền
        response.sendRedirect("/waiting");
    }
}
