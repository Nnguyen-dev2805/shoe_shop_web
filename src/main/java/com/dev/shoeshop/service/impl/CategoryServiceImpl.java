package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.category.CategoryRequest;
import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;
import com.dev.shoeshop.repository.CategoryRepository;
import com.dev.shoeshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
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
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(cat -> CategoryResponse.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .description(cat.getDescription())
                        .productCount((cat.getProducts() != null && !cat.getProducts().isEmpty()) ? cat.getProducts().size() : 0)
                        .build()
                )
                .collect(Collectors.toList());

    }


    @Override
    public Category updateCategory(Integer id, CategoryRequest request) {
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
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category không tồn tại với ID: " + id));
    }

    @Override
    public void deleteCategoryById(Integer id) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category không tồn tại với ID: " + id));
        categoryRepository.delete(existing);
    }

}
