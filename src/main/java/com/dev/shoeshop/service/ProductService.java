package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.product.ProductDetailResponse;
import com.dev.shoeshop.dto.product.ProductRequest;
import com.dev.shoeshop.dto.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    void saveProduct(ProductRequest request, MultipartFile image);
    Page<ProductResponse> getAllProducts(Pageable pageable, String search, Long categoryId);
    List<ProductResponse> getAllProductsList();
    // thêm để hiển thị giao diện chi tiết sản phẩm
    ProductDetailResponse getProductById(Long id);
}
