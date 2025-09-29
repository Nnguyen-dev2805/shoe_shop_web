package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.repository.ProductDetailRepository;
import com.dev.shoeshop.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductDetailRepository productDetailRepository;

    @Override
    public void save(ProductDetail productDetail) {
        productDetailRepository.save(productDetail);
    }
}
