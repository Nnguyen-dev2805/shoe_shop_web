package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.product.ProductRequest;
import com.dev.shoeshop.dto.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    void saveProduct(ProductRequest request, MultipartFile image);
    Page<ProductResponse> getAllProducts(Pageable pageable, String search);
    List<ProductResponse> getAllProductsList();
}
