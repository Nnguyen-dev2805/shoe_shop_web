package com.dev.shoeshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/brand")
public class AdminBrandController {
    
    @GetMapping("")
    public String brandList() {
        return "admin/brand/brand-list";
    }

    @GetMapping("/insert")
    public String insertBrandPage() {
        return "admin/brand/brand-add";
    }
    
    @GetMapping("/edit/{id}")
    public String editBrandPage(@PathVariable Long id) {
        return "admin/brand/brand-edit";
    }
}
