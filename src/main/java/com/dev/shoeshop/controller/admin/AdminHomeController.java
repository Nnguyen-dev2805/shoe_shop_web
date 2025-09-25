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
//    @GetMapping("/admin")
    public String adminHome(RedirectAttributes redirectAttributes, HttpSession session, Model model) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if(u == null) {
            return "redirect:/login";
        }
//        redirectAttributes.addFlashAttribute("user", u);
//        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());
//        Page<Order> orderPage = orderService.findAll(pageable);
//        model.addAttribute("listOrder", orderPage);
//
//        long totalOrder = orderService.countOrder();
//        model.addAttribute("totalOrder", totalOrder);
//
//        double totalPrice = orderService.totalPrice().orElse(0.0);
//        model.addAttribute("totalPrice", totalPrice);

        return "admin/index";
    }
}
