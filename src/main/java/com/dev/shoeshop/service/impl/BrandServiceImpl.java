package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.brand.BrandRequest;
import com.dev.shoeshop.dto.brand.BrandResponse;
import com.dev.shoeshop.entity.Brand;
import com.dev.shoeshop.repository.BrandRepository;
import com.dev.shoeshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    /**
     * ⚡ CACHED: Get all brands list (for dropdowns)
     * Cache key: "all"
     * TTL: 5 minutes
     */
    @Override
    @Cacheable(value = "brands", key = "'all'")
    public List<BrandResponse> getAllBrandsList() {
        log.info("📦 Loading all brands list from database");
        return brandRepository.findAll()
                .stream()
                .map(brand -> new BrandResponse(brand.getId(), brand.getName()))
                .collect(Collectors.toList());
    }

    /**
     * ⚡ CACHED: Get brand by ID
     * Cache key: "detail:{id}"
     */
    @Override
    @Cacheable(value = "brands", key = "'detail:' + #id")
    public Brand getBrandById(Long id) {
        log.info("📦 Loading brand {} from database", id);
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand không tồn tại với ID: " + id));
    }

    /**
     * ⚡ CACHED: Get brands with pagination and search
     * Cache key includes: page number, page size, search term
     */
    @Override
    @Cacheable(value = "brands", 
               key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + (#search != null ? #search : 'none')",
               unless = "#result == null")
    public Page<BrandResponse> getAllBrands(Pageable pageable, String search) {
        log.info("📦 Loading brands (page: {}, size: {}, search: {})", 
                 pageable.getPageNumber(), pageable.getPageSize(), search);
        
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

    /**
     * 🗑️ CACHE EVICT: Clear all brand caches when creating new brand
     */
    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public BrandResponse createBrand(BrandRequest request) {
        log.info("➕ Creating new brand, clearing brands cache");
        
        // Kiểm tra tên brand đã tồn tại chưa
        if (existsByName(request.getName())) {
            throw new RuntimeException("Tên brand đã tồn tại: " + request.getName());
        }
        
        Brand brand = new Brand();
        brand.setName(request.getName());
        
        Brand savedBrand = brandRepository.save(brand);
        return new BrandResponse(savedBrand.getId(), savedBrand.getName());
    }

    /**
     * 🗑️ CACHE EVICT: Clear all brand caches when updating
     */
    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        log.info("✏️ Updating brand {}, clearing brands cache", id);
        
        Brand brand = getBrandById(id);
        
        // Kiểm tra tên brand đã tồn tại cho brand khác chưa
        if (existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Tên brand đã tồn tại: " + request.getName());
        }
        
        brand.setName(request.getName());
        Brand updatedBrand = brandRepository.save(brand);
        return new BrandResponse(updatedBrand.getId(), updatedBrand.getName());
    }

    /**
     * 🗑️ CACHE EVICT: Clear all brand caches when deleting
     */
    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public void deleteBrand(Long id) {
        log.info("🗑️ Deleting brand {}, clearing brands cache", id);
        
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
