package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.product.ProductRequest;
import com.dev.shoeshop.dto.product.ProductResponse;
import com.dev.shoeshop.dto.productdetail.ProductDetailRequest;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.repository.ProductRepository;
import com.dev.shoeshop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    private final StorageService storageService;

    private final ProductDetailService productDetailService;

    private final BrandService brandService;

    @Transactional
    @Override
    public void saveProduct(ProductRequest request, MultipartFile image) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setVoucher(request.getVoucher());
        product.setCategory(categoryService.getCategoryById(request.getCategoryId()));
        product.setBrand(brandService.getBrandById(request.getBrandId()));

        if (image != null && !image.isEmpty()) {
            String fileUrl = storageService.storeFile(image);
            product.setImage(fileUrl);
        }

        productRepository.save(product);

        if (request.getProductDetails() != null) {
            for (ProductDetailRequest detailReq : request.getProductDetails()) {
                ProductDetail detail = new ProductDetail();
                detail.setProduct(product);
                detail.setSize(detailReq.getSize());
                detail.setPriceadd(detailReq.getPriceAdd());

                productDetailService.save(detail);
            }
        }
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, String search) {
        Page<Product> productPage;
        if (search != null && !search.trim().isEmpty()) {
            productPage = productRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
        List<ProductResponse> responses = productPage.getContent().stream()
                .map(cat -> new ProductResponse(
                        cat.getId(),
                        cat.getTitle(),
                        cat.getPrice(),
                        cat.getImage(),
                        cat.getCategory(),
                        cat.getBrand()
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, productPage.getTotalElements());
    }
}
