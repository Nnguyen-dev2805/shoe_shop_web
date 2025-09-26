package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;
import com.dev.shoeshop.repository.CategoryRepository;
import com.dev.shoeshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

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
    public void deleteCategoryById(Long id) {

    }

}
