package com.dev.shoeshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @GetMapping("")
    public String categoryList() {
        return "admin/categories/category-list2";
    }

    @GetMapping("/insert")
    public String insertProductPage() {
        return "admin/categories/category-add2";
    }

}
