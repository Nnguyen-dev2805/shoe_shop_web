package com.dev.shoeshop.controller.web;

import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WaitingController {
    @GetMapping("/waiting")
    public String waitingPage(HttpSession session) {
        Users loggedInUser = (Users) session.getAttribute(Constant.SESSION_USER); // lấy thông tin từ session
        System.out.println(loggedInUser.getFullname() + " " + loggedInUser.getId());
        if (loggedInUser != null) {
            long roleId = loggedInUser.getRole().getRoleId();
            if (roleId == 1) {
                return "redirect:/admin";
            } else if (roleId == 2) {
                return "redirect:/manager";
            } else if (roleId == 3) {
                return "redirect:/user/shop";
            } else {
                return "redirect:/shipper/order/list";
            }
        } else {
            System.out.println("No account");
            return "redirect:/login";
        }
    }
}
