package com.dev.shoeshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @GetMapping("")
    public String categoryList() {
        return "admin/categories/category-list";
    }

    @GetMapping("/insert")
    public String insertCategoryPage() {
        return "admin/categories/category-add";
    }

    @GetMapping("/update/{id}")
    public String updateCategoryPage(@PathVariable Long id) {
        return "admin/categories/category-edit";
    }

}
