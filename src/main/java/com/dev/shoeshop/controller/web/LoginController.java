package com.dev.shoeshop.controller.web;

import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(HttpServletRequest req, Model model) {
        Cookie[] cookies = req.getCookies();
        String username = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Constant.COOKIE_REMEMBER)) {
                    username = cookie.getValue();
                    break;
                }
            }
        }
        if (username != null) {
            model.addAttribute("username", username);
        }
        return "web/login";
    }
}
