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
@RequiredArgsConstructor
public class UserCartController {
    
    /**
     * View cart page
     */
    @GetMapping("/cart/view")
    public String viewCart(HttpSession session){
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user != null) {
            return "user/cart";
        } else {
            return "redirect:/login";
        }
    }
    
    /**
     * Alternative URL for cart
     */
    @GetMapping("/user/cart")
    public String viewCartAlt(HttpSession session){
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user != null) {
            return "user/cart";
        } else {
            return "redirect:/login";
        }
    }
    
    /**
     * Step 1: Select items for checkout
     */
    @GetMapping("/user/select-items")
    public String selectItems(HttpSession session){
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user != null) {
            return "user/select-items";
        } else {
            return "redirect:/login";
        }
    }
    
    /**
     * Step 2: Payment confirmation
     */
    @GetMapping("/user/payment")
    public String payment(HttpSession session){
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user != null) {
            return "user/payment";
        } else {
            return "redirect:/login";
        }
    }
}
