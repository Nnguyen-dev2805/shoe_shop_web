package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.category.CategoryRequest;
import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    Category saveCategory(Category category);
    void deleteCategoryById(Long id);
    Category updateCategory(Long id, CategoryRequest request);
    Category getCategoryById(Long id);
    Page<CategoryResponse> getAllCategories(Pageable pageable, String search);
    List<CategoryResponse> getAllCategoriesList();
}
