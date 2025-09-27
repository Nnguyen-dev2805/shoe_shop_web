package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    // đếm số sản phẩm của một danh mục
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    int countProductsByCategoryId(@Param("categoryId") Long categoryId);

//    @Query(value = "SELECT COUNT(*) FROM product p WHERE p.category_id = :categoryId", nativeQuery = true)
//    int countProductsByCategoryId(@Param("categoryId") Long categoryId);
}
