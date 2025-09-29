package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.brand.BrandResponse;
import com.dev.shoeshop.entity.Brand;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllBrandsList();
    Brand getBrandById(Long id);
}
