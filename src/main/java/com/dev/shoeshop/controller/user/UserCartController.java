package com.dev.shoeshop.controller.user;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User Cart Controller - Handles view rendering only
 * API endpoints are in ApiCartController (/api/*)
 */
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class UserCartController {
    
    @GetMapping("/view")
    public String viewCart(HttpSession session){
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user != null) {
            return "user/cart";
        } else {
            return "redirect:/login";
        }
    }
}
