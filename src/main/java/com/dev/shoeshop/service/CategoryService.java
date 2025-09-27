package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.category.CategoryRequest;
import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;

import java.util.List;

public interface CategoryService {
    Category saveCategory(Category category);
    List<CategoryResponse> getAllCategories();
    void deleteCategoryById(Integer id);
    Category updateCategory(Integer id, CategoryRequest request);
    Category getCategoryById(Integer id);
}
