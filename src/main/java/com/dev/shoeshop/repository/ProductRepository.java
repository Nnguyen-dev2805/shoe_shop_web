package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    
    // lấy danh sách sản phẩm không phân trang
    List<Product> findByIsDeleteFalse();

    // Lấy chi tiết sản phẩm với thông tin thương hiệu, danh mục, Flash Sale
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.details d " +
           "LEFT JOIN FETCH d.flashSaleItems fsi " +
           "LEFT JOIN FETCH fsi.flashSale fs " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithDetailsAndInventories(@Param("id") Long id);
    

    // JPQL Fix N+M Query
    // Load toàn bộ sản phẩm theo phân trang
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE p.isDelete = false")
    Page<Product> findAllWithCategoryAndBrand(Pageable pageable);
    
    // Load toàn bộ sản phẩm theo phân trang và tìm kiếm
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE p.isDelete = false " +
           "AND LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> findByTitleWithCategoryAndBrand(@Param("search") String search, Pageable pageable);
    
    // Load toàn bộ sản phẩm theo phân trang và danh mục
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE p.isDelete = false AND p.category.id = :categoryId")
    Page<Product> findByCategoryWithRelations(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // Load toàn bộ sản phẩm theo phân trang, tìm kiếm, danh mục
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE p.isDelete = false " +
           "AND p.category.id = :categoryId " +
           "AND LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> findByCategoryAndTitleWithRelations(
        @Param("categoryId") Long categoryId,
        @Param("search") String search,
        Pageable pageable
    );

    // lấy danh sách toàn bộ sản phẩm
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.details d " +
           "LEFT JOIN FETCH d.flashSaleItems fsi " +
           "LEFT JOIN FETCH fsi.flashSale fs " +
           "WHERE p.isDelete = false")
    Page<Product> findAllWithFlashSale(Pageable pageable);
    
    // tìm kiếm với full thông tin sản phẩm
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.details d " +
           "LEFT JOIN FETCH d.flashSaleItems fsi " +
           "LEFT JOIN FETCH fsi.flashSale fs " +
           "WHERE p.isDelete = false " +
           "AND LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> findByTitleWithFlashSale(@Param("search") String search, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.details d " +
           "LEFT JOIN FETCH d.flashSaleItems fsi " +
           "LEFT JOIN FETCH fsi.flashSale fs " +
           "WHERE p.isDelete = false AND p.category.id = :categoryId")
    Page<Product> findByCategoryWithFlashSale(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.details d " +
           "LEFT JOIN FETCH d.flashSaleItems fsi " +
           "LEFT JOIN FETCH fsi.flashSale fs " +
           "WHERE p.isDelete = false " +
           "AND p.category.id = :categoryId " +
           "AND LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> findByCategoryAndTitleWithFlashSale(
        @Param("categoryId") Long categoryId,
        @Param("search") String search,
        Pageable pageable
    );
}
