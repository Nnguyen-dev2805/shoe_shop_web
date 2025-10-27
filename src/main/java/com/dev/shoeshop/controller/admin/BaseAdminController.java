package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Base Admin Controller
 * Tự động set session.admin cho tất cả admin controllers
 */
public abstract class BaseAdminController {
    
    /**
     * Method này chạy TRƯỚC MỌI request trong admin controllers
     * Tự động set session.admin và model.admin
     */
    @ModelAttribute
    public void setAdminSession(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        
        if (user != null) {
            // Set admin in session for chat widget
            session.setAttribute("admin", user);
            model.addAttribute("admin", user);
        }
    }
    
    /**
     * Get current admin user from session
     */
    protected Users getCurrentAdmin(HttpSession session) {
        return (Users) session.getAttribute(Constant.SESSION_USER);
    }
}
