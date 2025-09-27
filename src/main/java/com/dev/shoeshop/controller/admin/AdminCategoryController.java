package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.entity.Category;
import com.dev.shoeshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping("")
    public String categoryList() {
        return "admin/categories/category-list";
    }

    @GetMapping("/insert")
    public String insertCategoryPage() {
        return "admin/categories/category-add";
    }

    @GetMapping("/update/{id}")
    public String updateCategoryPage(@PathVariable Integer id) {
        Category category = categoryService.getCategoryById(id);
        return "admin/categories/category-edit";
    }

}
