package com.dev.shoeshop.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResetPasswordViewController {

    @GetMapping("/reset-password")
    public String showResetPasswordPage() {
        return "web/reset_password";
    }
}
