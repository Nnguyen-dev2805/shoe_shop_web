package com.dev.shoeshop.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestResetController {

    @GetMapping("/test-reset")
    @ResponseBody
    public String testReset() {
        return "Reset password controller is working!";
    }
    
    @GetMapping("/reset-password-test")
    public String showResetPasswordPageTest() {
        return "web/reset_password";
    }
}
