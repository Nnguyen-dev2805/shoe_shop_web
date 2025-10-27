package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.entity.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin Chat Controller
 * Admin có quyền y hệt Manager về chat
 */
@Controller
@RequestMapping("/admin/chat")
public class AdminChatController extends BaseAdminController {

    /**
     * Admin chat management page (same as Manager)
     * GET /admin/chat
     */
    @GetMapping
    public String showChatManagement(HttpSession session, Model model) {
        // Get admin from session (already set by BaseAdminController)
        Users admin = getCurrentAdmin(session);
        
        if (admin == null) {
            return "redirect:/login";
        }
        
        // session.admin already set by BaseAdminController
        // Also set as manager for manager/chat.html compatibility
        session.setAttribute("manager", admin);
        model.addAttribute("manager", admin);
        
        // Use manager chat page (admin has same permissions)
        return "manager/chat";
    }
}
