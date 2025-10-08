package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    // tìm kiếm theo tên
    Page<Product> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    // tìm kiếm theo tên và category id
    Page<Product> findByCategoryIdAndTitleContainingIgnoreCase(Long categoryId, String keyword, Pageable pageable);
    // lọc theo sản phẩm theo category dùng cho hiển thị web
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
