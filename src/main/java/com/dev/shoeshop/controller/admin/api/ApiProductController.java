package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ApiProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage;
        if (search != null && !search.trim().isEmpty()) {
            productsPage = productService.searchProductsByTitle(search.trim(), pageable);
        } else {
            productsPage = productService.findAllPage(pageable);
        }
        return ResponseEntity.ok(productsPage);
    }

}
