package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @GetMapping
    public String adminHome(RedirectAttributes redirectAttributes, HttpSession session, Model model) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if(u == null) {
            return "redirect:/login";
        }
        
        // Render dashboard view - data will be loaded via AJAX
        return "admin/dashboard";
    }
}
