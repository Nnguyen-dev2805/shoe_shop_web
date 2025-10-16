package com.dev.shoeshop.controller.manager.api;

import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.dto.manager.CategoryResponseDTO;
import com.dev.shoeshop.dto.manager.CategoryStatisticsDTO;
import com.dev.shoeshop.dto.product.ProductResponse;
import com.dev.shoeshop.service.CategoryService;
import com.dev.shoeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ManagerCategoryApiController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategoriesList();
            List<CategoryResponseDTO> categoryDTOs = categories.stream()
                    .map(this::convertFromCategoryResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(categoryDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<CategoryStatisticsDTO> getCategoryStatistics() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategoriesList();
            List<ProductResponse> allProducts = productService.getAllProductsList();
            
            long totalCategories = categories.size();
            long totalProducts = allProducts.size();
            
            // Count categories with products
            long categoriesWithProducts = categories.stream()
                    .mapToLong(category -> {
                        long productCount = allProducts.stream()
                                .filter(product -> product.getCategoryName().equals(category.getName()))
                                .count();
                        return productCount > 0 ? 1 : 0;
                    })
                    .sum();
            
            long emptyCategories = totalCategories - categoriesWithProducts;

            CategoryStatisticsDTO statistics = CategoryStatisticsDTO.builder()
                    .totalCategories(totalCategories)
                    .totalProducts(totalProducts)
                    .categoriesWithProducts(categoriesWithProducts)
                    .emptyCategories(emptyCategories)
                    .build();

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        try {
            // Get category by ID using getCategoryById method
            com.dev.shoeshop.entity.Category category = categoryService.getCategoryById(id);
            if (category == null) {
                return ResponseEntity.notFound().build();
            }
            
            CategoryResponseDTO categoryDTO = convertFromEntity(category);
            return ResponseEntity.ok(categoryDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private CategoryResponseDTO convertFromCategoryResponse(CategoryResponse categoryResponse) {
        // Count products for this category
        List<ProductResponse> allProducts = productService.getAllProductsList();
        long productCount = allProducts.stream()
                .filter(product -> product.getCategoryName().equals(categoryResponse.getName()))
                .count();
        
        return CategoryResponseDTO.builder()
                .id(categoryResponse.getId())
                .name(categoryResponse.getName())
                .description(categoryResponse.getDescription())
                .productCount(productCount)
                .build();
    }
    
    private CategoryResponseDTO convertFromEntity(com.dev.shoeshop.entity.Category category) {
        // Count products for this category
        List<ProductResponse> allProducts = productService.getAllProductsList();
        long productCount = allProducts.stream()
                .filter(product -> product.getCategoryName().equals(category.getName()))
                .count();
        
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .productCount(productCount)
                .build();
    }
}
