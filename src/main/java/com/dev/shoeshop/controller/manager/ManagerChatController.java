package com.dev.shoeshop.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View Controller cho manager chat
 */
@Controller
@RequestMapping("/manager/chat")
public class ManagerChatController {

    /**
     * Hiển thị trang quản lý chat cho manager
     */
    @GetMapping
    public String showChatManagement() {
        return "manager/chat";
    }
}
