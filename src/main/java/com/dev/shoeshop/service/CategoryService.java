package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;

import java.util.List;

public interface CategoryService {
    Category saveCategory(Category category);
    List<CategoryResponse> getAllCategories();
    void deleteCategoryById(Long id);
}
