package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.category.CategoryRequest;
import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;
import com.dev.shoeshop.repository.CategoryRepository;
import com.dev.shoeshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 🗑️ CACHE EVICT: Clear cache when saving category
     */
    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Category saveCategory(Category category) {
        log.info("💾 Saving category, clearing categories cache");
        return categoryRepository.save(category);
    }

    /**
     * ⚡ CACHED: Get categories with pagination and search
     */
    @Override
    @Cacheable(value = "categories", 
               key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + (#search != null ? #search : 'none')",
               unless = "#result == null")
    public Page<CategoryResponse> getAllCategories(Pageable pageable, String search) {
        log.info("📦 Loading categories (page: {}, size: {}, search: {})", 
                 pageable.getPageNumber(), pageable.getPageSize(), search);
        
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

    /**
     * ⚡ CACHED: Get all categories list (for dropdowns)
     */
    @Override
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> getAllCategoriesList() {
        log.info("📦 Loading all categories list from database");
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


    /**
     * 🗑️ CACHE EVICT: Clear cache when updating
     */
    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Category updateCategory(Long id, CategoryRequest request) {
        log.info("✏️ Updating category {}, clearing categories cache", id);
        
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

    /**
     * ⚡ CACHED: Get category by ID
     */
    @Override
    @Cacheable(value = "categories", key = "'detail:' + #id")
    public Category getCategoryById(Long id) {
        log.info("📦 Loading category {} from database", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category không tồn tại với ID: " + id));
    }

    /**
     * 🗑️ CACHE EVICT: Clear cache when deleting
     */
    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategoryById(Long id) {
        log.info("🗑️ Deleting category {}, clearing categories cache", id);
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category không tồn tại với ID: " + id));
        categoryRepository.delete(existing);
    }

}
