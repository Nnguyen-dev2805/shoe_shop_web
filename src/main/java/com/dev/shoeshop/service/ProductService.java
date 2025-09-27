package com.dev.shoeshop.service;

import com.dev.shoeshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> findAllPage(Pageable pageable);
    Page<Product> searchProductsByTitle(String keyword, Pageable pageable);
    Page<Product> searchProductsByCategoryAndTitle(Long categoryId, String keyword, Pageable pageable);
}
