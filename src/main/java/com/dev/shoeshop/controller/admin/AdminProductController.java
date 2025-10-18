package com.dev.shoeshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/product")
public class AdminProductController {

    @GetMapping("/insert")
    public String insertProductPage() {
        return "admin/products/product-add";
    }

    @GetMapping("")
    public String getAllProduct() {
        return "admin/products/product-list";
    }

    /**
     * EDIT PRODUCT PAGE
     * 
     * Method: GET
     * URL: /admin/product/edit/{id}
     * 
     * @param id Product ID
     * @param model Model
     * @return View name
     */
    @GetMapping("/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model) {
        model.addAttribute("productId", id);
        return "admin/products/product-edit";
    }

}
