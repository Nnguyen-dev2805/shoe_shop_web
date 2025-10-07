package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.product.ProductDetailResponse;
import com.dev.shoeshop.dto.product.ProductRequest;
import com.dev.shoeshop.dto.product.ProductResponse;
import com.dev.shoeshop.dto.productdetail.ProductDetailRequest;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.entity.Rating;
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

        if (image != null && !image.isEmpty()) {
            String fileUrl = storageService.storeFile(image);
            product.setImage(fileUrl);
            productRepository.save(product);
        }


    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, String search, Long categoryId) {
        Page<Product> productPage;
        
        // Filter by category and search
        if (categoryId != null && search != null && !search.trim().isEmpty()) {
            productPage = productRepository.findByCategoryIdAndTitleContainingIgnoreCase(categoryId, search, pageable);
        } 
        // Filter by category only
        else if (categoryId != null) {
            productPage = productRepository.findByCategoryId(categoryId, pageable);
        } 
        // Search only
        else if (search != null && !search.trim().isEmpty()) {
            productPage = productRepository.findByTitleContainingIgnoreCase(search, pageable);
        } 
        // Get all
        else {
            productPage = productRepository.findAll(pageable);
        }
        
        List<ProductResponse> responses = productPage.getContent().stream()
                .map(cat -> new ProductResponse(
                        cat.getId(),
                        cat.getTitle(),
                        cat.getPrice(),
                        cat.getImage(),
                        cat.getCategory().getName(),
                        cat.getBrand().getName()
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, productPage.getTotalElements());
    }

    @Override
    public List<ProductResponse> getAllProductsList() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(cat -> new ProductResponse(
                        cat.getId(),
                        cat.getTitle(),
                        cat.getPrice(),
                        cat.getImage(),
                        cat.getCategory().getName(),
                        cat.getBrand().getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDetailResponse getProductById(Long id) {
        // Use custom query to eagerly fetch details and inventories
        Product product = productRepository.findByIdWithDetailsAndInventories(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Convert ProductDetails to SizeOptions with stock from product_detail.quantity
        List<ProductDetailResponse.SizeOption> sizeOptions = product.getDetails().stream()
                .map(detail -> {
                    // Get quantity directly from ProductDetail entity
                    int stock = detail.getQuantity();
                    
                    System.out.println("=== Processing ProductDetail ID: " + detail.getId() 
                            + ", Size: " + detail.getSize() 
                            + ", Stock: " + stock);
                    
                    return ProductDetailResponse.SizeOption.builder()
                            .id(detail.getId())
                            .size(detail.getSize())
                            .priceAdd(detail.getPriceadd())
                            .stock(stock)
                            .build();
                })
                .collect(Collectors.toList());

        // Calculate average rating
        Double avgRating = 0.0;
        Integer totalReviews = 0;
        if (product.getRatings() != null && !product.getRatings().isEmpty()) {
            totalReviews = product.getRatings().size();
            avgRating = product.getRatings().stream()
                    .mapToInt(Rating::getStar)
                    .average()
                    .orElse(0.0);
        }

        return ProductDetailResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .image(product.getImage())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "N/A")
                .brandName(product.getBrand() != null ? product.getBrand().getName() : "N/A")
                .sizeOptions(sizeOptions)
                .avgRating(avgRating)
                .totalReviews(totalReviews)
                .build();
    }
}
