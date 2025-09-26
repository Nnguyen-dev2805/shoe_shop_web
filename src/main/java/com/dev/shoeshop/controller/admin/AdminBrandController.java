package com.dev.shoeshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/brand")
public class AdminBrandController {
    @GetMapping("")
    public String categoryList() {
        return "admin/brand/brand-list";
    }

    @GetMapping("/insert")
    public String insertProductPage() {
        return "admin/brand/brand-add";
    }
}
