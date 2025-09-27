package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;

import org.springframework.dao.DataIntegrityViolationException;
import com.dev.shoeshop.dto.category.CategoryRequest;
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

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Integer id) {
        try {
            Category cat = categoryService.getCategoryById(id);
            CategoryResponse response = new CategoryResponse(cat.getId(), cat.getName(), cat.getDescription(), cat.getProducts() !=null ?  cat.getProducts().size() : 0);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest request, BindingResult result){
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Lỗi validation: ");
            for (var error : result.getFieldErrors()) {
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMsg.toString());
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
                    savedCategory.getProducts() != null ? savedCategory.getProducts().size() : 0
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryRequest request, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Lỗi validation: ");
            for (var error : result.getFieldErrors()) {
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMsg.toString());
        }
        try {
            Category updatedCategory = categoryService.updateCategory(id, request);
            CategoryResponse response = new CategoryResponse(
                    updatedCategory.getId(),
                    updatedCategory.getName(),
                    updatedCategory.getDescription(),
                    updatedCategory.getProducts().size()
            );
            return ResponseEntity.ok(response);
        }
        catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Danh mục với tên này đã tồn tại. Vui lòng nhập tên khác.");
        }
        catch (RuntimeException e) {
            if (e.getMessage().contains("không tồn tại")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category không tồn tại: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("không tồn tại")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category không tồn tại: " + id);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }
}
