package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Category;
import com.dev.shoeshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Page<Category> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
