package com.dev.shoeshop.controller;

import com.dev.shoeshop.dto.product.ProductDetailResponse;
import com.dev.shoeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final ProductService productService;

    /**
     * API để lấy chi tiết sản phẩm theo ID
     * @param id Product ID
     * @return ProductDetailResponse
     */
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDetailResponse> getProductDetails(@PathVariable Long id) {
        try {
            ProductDetailResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
