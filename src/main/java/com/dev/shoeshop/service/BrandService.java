package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.brand.BrandRequest;
import com.dev.shoeshop.dto.brand.BrandResponse;
import com.dev.shoeshop.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllBrandsList();
    Brand getBrandById(Long id);
    Page<BrandResponse> getAllBrands(Pageable pageable, String search);
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(Long id, BrandRequest request);
    void deleteBrand(Long id);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
