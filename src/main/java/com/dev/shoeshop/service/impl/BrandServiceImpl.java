package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.brand.BrandResponse;
import com.dev.shoeshop.entity.Brand;
import com.dev.shoeshop.repository.BrandRepository;
import com.dev.shoeshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public List<BrandResponse> getAllBrandsList() {
        return brandRepository.findAll()
                .stream()
                .map(brand -> new BrandResponse(brand.getId(), brand.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand không tồn tại với ID: " + id));
    }
}
