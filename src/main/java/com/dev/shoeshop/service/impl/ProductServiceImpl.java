package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.repository.ProductRepository;
import com.dev.shoeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Page<Product> findAllPage(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> searchProductsByTitle(String keyword, Pageable pageable) {
        return productRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public Page<Product> searchProductsByCategoryAndTitle(Long categoryId, String keyword, Pageable pageable) {
        return productRepository.findByCategoryIdAndTitleContainingIgnoreCase(categoryId, keyword, pageable);
    }
}
