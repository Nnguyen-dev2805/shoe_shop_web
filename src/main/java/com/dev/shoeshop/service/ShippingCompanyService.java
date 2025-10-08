package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.shippingcompany.ShippingCompanyResponse;
import com.dev.shoeshop.entity.ShippingCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ShippingCompanyService {
    
    // Lưu công ty vận chuyển
    ShippingCompany saveShippingCompany(ShippingCompany shippingCompany);
    
    // Lấy tất cả công ty vận chuyển (có phân trang)
    Page<ShippingCompany> getAllShippingCompanies(Pageable pageable);
    
    // Lấy tất cả công ty vận chuyển (không phân trang)
    List<ShippingCompany> getAllShippingCompanies();
    
    // Lấy công ty vận chuyển theo ID
    Optional<ShippingCompany> getShippingCompanyById(Integer id);
    
    // Tìm kiếm theo tên
    Page<ShippingCompany> searchByName(String name, Pageable pageable);
    
    // Lấy theo trạng thái hoạt động
    Page<ShippingCompany> getByIsActive(Boolean isActive, Pageable pageable);
    
    // Tìm kiếm theo tên và trạng thái
    Page<ShippingCompany> searchByNameAndIsActive(String name, Boolean isActive, Pageable pageable);
    
    // Lấy tất cả công ty đang hoạt động
    List<ShippingCompany> getActiveShippingCompanies();
    
    // Lấy tất cả công ty đang hoạt động (Response DTO)
    List<ShippingCompanyResponse> getAllActiveCompanies();
    
    // Xóa công ty vận chuyển
    void deleteShippingCompany(Integer id);
    
    // Đếm số lượng công ty theo trạng thái
    long countByIsActive(Boolean isActive);
    
    // Đếm tổng số công ty
    long countAllShippingCompanies();
    
    // Kiểm tra tên đã tồn tại chưa
    boolean existsByName(String name);
    
    // Kiểm tra tên đã tồn tại chưa (trừ ID hiện tại)
    boolean existsByNameAndIdNot(String name, Integer id);
}
