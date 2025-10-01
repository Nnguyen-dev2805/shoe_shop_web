package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.dto.pagination.PaginationResponse;
import com.dev.shoeshop.dto.product.ProductRequest;
import com.dev.shoeshop.dto.product.ProductResponse;
import com.dev.shoeshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ApiProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PaginationResponse<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ProductResponse> productPage = productService.getAllProducts(pageable, search, categoryId);
            PaginationResponse<ProductResponse> response = new PaginationResponse<>(
                    productPage.getContent(),
                    productPage.getTotalPages(),
                    productPage.getTotalElements(),
                    productPage.getNumber() + 1,  // frontend dùng 1-based page
                    productPage.getSize()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductResponse>> getAllProductsList() {
        try {
            List<ProductResponse> products = productService.getAllProductsList();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(@Valid @RequestPart("request") ProductRequest request,
                                           @RequestPart(name = "image", required = false) MultipartFile image,
                                           BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Lỗi validation: ");
            for (var error : result.getFieldErrors()) {
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMsg.toString());
        }
        try {
            productService.saveProduct(request, image);
            return ResponseEntity.ok("Thêm sản phẩm thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }
}
