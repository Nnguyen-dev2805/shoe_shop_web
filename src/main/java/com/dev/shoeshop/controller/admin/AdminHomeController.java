package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.entity.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminHomeController extends BaseAdminController {

    @GetMapping
    public String adminHome(RedirectAttributes redirectAttributes, HttpSession session, Model model) {
        Users u = getCurrentAdmin(session);
        if(u == null) {
            return "redirect:/login";
        }
        
        // session.admin already set by BaseAdminController @ModelAttribute
        // Render dashboard view - data will be loaded via AJAX
        return "admin/dashboard";
    }
}
