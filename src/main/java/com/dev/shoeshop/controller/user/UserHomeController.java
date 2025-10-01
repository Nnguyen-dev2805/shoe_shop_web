package com.dev.shoeshop.controller.user;

import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.UserService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User Home Controller - Handles view rendering only
 * API endpoints are in ApiHomeController (/api/shop/*)
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserHomeController {

    private final UserService userService;

    /**
     * Display shop page
     * Data will be loaded via AJAX from /api/shop/* endpoints
     */
    @GetMapping("/shop")
    public String userShop(HttpSession session, Model model) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
            model.addAttribute("user", u);
            return "user/shop";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/blog")
    public String blog(){
        return "/user/blog";
    }

    @GetMapping("/shipper/all")
    public ResponseEntity<?> getAllShipper(){
        List<UserDTO> listShipper = userService.getAllShipper(4L);
        Map<String, Object> response = new HashMap<>();

        response.put("listShipper", listShipper);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shipper/search")
    public ResponseEntity<?> getShipperByName(@RequestParam(value = "name") String name){
        List<UserDTO> listShipper = userService.findByFullnameAndRole(name,4L);
        Map<String, Object> response = new HashMap<>();

        response.put("listShipper", listShipper);
        return ResponseEntity.ok(response);
    }

}
