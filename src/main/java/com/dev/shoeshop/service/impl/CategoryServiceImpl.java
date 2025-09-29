package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.category.CategoryRequest;
import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;
import com.dev.shoeshop.repository.CategoryRepository;
import com.dev.shoeshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable, String search) {
        Page<Category> categoryPage;
        if (search != null && !search.trim().isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }
        List<CategoryResponse> responses = categoryPage.getContent().stream()
                .map(cat -> new CategoryResponse(
                        cat.getId(),
                        cat.getName(),
                        cat.getDescription(),
                        cat.getProducts() != null ? cat.getProducts().size() : 0
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, categoryPage.getTotalElements());
    }

    @Override
    public List<CategoryResponse> getAllCategoriesList() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getProducts() != null ? category.getProducts().size() : 0
                ))
                .collect(Collectors.toList());
    }


    @Override
    public Category updateCategory(Long id, CategoryRequest request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category không tồn tại với ID: " + id));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            existing.setName(request.getName().trim());
        }
        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            existing.setDescription(request.getDescription().trim());
        }
        return categoryRepository.save(existing);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category không tồn tại với ID: " + id));
    }

    @Override
    public void deleteCategoryById(Long id) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category không tồn tại với ID: " + id));
        categoryRepository.delete(existing);
    }

}
