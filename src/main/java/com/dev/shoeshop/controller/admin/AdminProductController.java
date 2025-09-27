package com.dev.shoeshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/product")
public class AdminProductController {

    @GetMapping("/insert")
    public String insertProductPage() {
        return "/admin/products/product-add";
    }

    @GetMapping("")
    public String getAllProduct() {
        return "/admin/products/product-list";
    }

}
