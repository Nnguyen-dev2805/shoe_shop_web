package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.brand.BrandRequest;
import com.dev.shoeshop.dto.brand.BrandResponse;
import com.dev.shoeshop.entity.Brand;
import com.dev.shoeshop.repository.BrandRepository;
import com.dev.shoeshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Page<BrandResponse> getAllBrands(Pageable pageable, String search) {
        Page<Brand> brandPage;
        
        if (search != null && !search.trim().isEmpty()) {
            brandPage = brandRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            brandPage = brandRepository.findAll(pageable);
        }
        
        List<BrandResponse> responses = brandPage.getContent().stream()
                .map(brand -> new BrandResponse(brand.getId(), brand.getName()))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, brandPage.getTotalElements());
    }

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        // Kiểm tra tên brand đã tồn tại chưa
        if (existsByName(request.getName())) {
            throw new RuntimeException("Tên brand đã tồn tại: " + request.getName());
        }
        
        Brand brand = new Brand();
        brand.setName(request.getName());
        
        Brand savedBrand = brandRepository.save(brand);
        return new BrandResponse(savedBrand.getId(), savedBrand.getName());
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        Brand brand = getBrandById(id);
        
        // Kiểm tra tên brand đã tồn tại cho brand khác chưa
        if (existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Tên brand đã tồn tại: " + request.getName());
        }
        
        brand.setName(request.getName());
        Brand updatedBrand = brandRepository.save(brand);
        return new BrandResponse(updatedBrand.getId(), updatedBrand.getName());
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = getBrandById(id);
        
        // Kiểm tra xem brand có sản phẩm nào không
        if (brand.getProducts() != null && !brand.getProducts().isEmpty()) {
            throw new RuntimeException("Không thể xóa brand này vì đang có sản phẩm sử dụng");
        }
        
        brandRepository.delete(brand);
    }

    @Override
    public boolean existsByName(String name) {
        return brandRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return brandRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }
}
