package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.ShippingCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingCompanyRepository extends JpaRepository<ShippingCompany, Integer> {
    
    // Tìm kiếm theo tên (không phân biệt hoa thường)
    @Query("SELECT sc FROM ShippingCompany sc WHERE LOWER(sc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ShippingCompany> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Tìm kiếm theo trạng thái hoạt động
    Page<ShippingCompany> findByIsActive(Boolean isActive, Pageable pageable);
    
    // Tìm kiếm theo tên và trạng thái
    @Query("SELECT sc FROM ShippingCompany sc WHERE LOWER(sc.name) LIKE LOWER(CONCAT('%', :name, '%')) AND sc.isActive = :isActive")
    Page<ShippingCompany> findByNameContainingIgnoreCaseAndIsActive(String name, Boolean isActive, Pageable pageable);
    
    // Lấy tất cả công ty đang hoạt động
    List<ShippingCompany> findByIsActiveTrue();
    
    // Đếm số lượng công ty theo trạng thái
    long countByIsActive(Boolean isActive);
    
    // Kiểm tra tên đã tồn tại chưa
    boolean existsByNameIgnoreCase(String name);
    
    // Kiểm tra tên đã tồn tại chưa (trừ ID hiện tại)
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
    
    // Tìm theo ID
    Optional<ShippingCompany> findById(Integer id);
}
