package com.dev.shoeshop.security;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.UserService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        String email = authentication.getPrincipal().toString();
        String email = authentication.getName();
        HttpSession session = request.getSession();

        Users user = userService.findUserByEmail(email);

        session.setAttribute(Constant.SESSION_USER, user);

        response.sendRedirect("/waiting");
    }
}
