package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;

import com.dev.shoeshop.dto.category.CategoryCreateRequest;
import com.dev.shoeshop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class ApiCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody  CategoryCreateRequest request, BindingResult result){
        if (result.hasErrors()) {
            String errorMsg = "Lỗi validation: ";
            for (var error : result.getFieldErrors()) {
                errorMsg += error.getField() + " - " + error.getDefaultMessage() + "; ";
            }
            return ResponseEntity.badRequest().body(errorMsg);  // 400 Bad Request với message
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        try {
            Category savedCategory = categoryService.saveCategory(category);
            CategoryResponse response = new CategoryResponse(
                    savedCategory.getId(),
                    savedCategory.getName(),
                    savedCategory.getDescription(),
                    0
            );
            // Return 200 OK với JSON response
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());  // 500 Internal Server Error
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        System.out.println("ahhahhahaahahhhhhhhhhhhha");
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
//        try {
//            categoryService.deleteCategoryById(id);
//            return ResponseEntity.ok("Xóa thành công!");  // 200 OK với message
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Lỗi server: " + e.getMessage());
//        }
//    }
}
