package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.product.ProductDetailResponse;
import com.dev.shoeshop.dto.product.ProductRequest;
import com.dev.shoeshop.dto.product.ProductResponse;
import com.dev.shoeshop.dto.productdetail.ProductDetailRequest;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.repository.InventoryRepository;
import com.dev.shoeshop.repository.ProductDetailRepository;
import com.dev.shoeshop.repository.ProductRepository;
import com.dev.shoeshop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    private final InventoryRepository inventoryRepository;
    
    private final ProductDetailRepository productDetailRepository;


    @CacheEvict(value = {"products", "productDetail"}, allEntries = true)
    @Transactional
    @Override
    public void saveProduct(ProductRequest request, MultipartFile image) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
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

    // Lấy danh sách sản phẩm chưa xóa
    @Cacheable(value = "products", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + (#search != null ? #search : 'null') + '_' + (#categoryId != null ? #categoryId : 'null')")
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, String search, Long categoryId) {
        Page<Product> productPage;

        if (categoryId != null && search != null && !search.trim().isEmpty()) {
            productPage = productRepository.findByCategoryAndTitleWithFlashSale(categoryId, search, pageable);
        } 

        else if (categoryId != null) {
            productPage = productRepository.findByCategoryWithFlashSale(categoryId, pageable);
        } 

        else if (search != null && !search.trim().isEmpty()) {
            productPage = productRepository.findByTitleWithFlashSale(search, pageable);
        } 

        else {
            productPage = productRepository.findAllWithFlashSale(pageable);
        }
        
        List<ProductResponse> responses = productPage.getContent().stream()
                .map(product -> {

                    ProductResponse.FlashSaleInfo flashSaleInfo = getFlashSaleInfo(product);

                    return ProductResponse.builder()
                            .id(product.getId())
                            .title(product.getTitle())
                            .price(product.getPrice())
                            .image(product.getImage())
                            .categoryName(product.getCategory().getName())
                            .brandName(product.getBrand().getName())
                            .soldQuantity(product.getSoldQuantity())
                            .flashSale(flashSaleInfo)
                            .build();
                })
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, productPage.getTotalElements());
    }

    @Override
    public List<ProductResponse> getAllProductsList() {
        // ✅ SOFT DELETE: Chỉ lấy sản phẩm chưa xóa (isDelete = false)
        List<Product> products = productRepository.findByIsDeleteFalse();
        return products.stream()
                .map(product -> {
                    // Check flash sale
                    ProductResponse.FlashSaleInfo flashSaleInfo = getFlashSaleInfo(product);
                    
                    // Build response với Builder pattern
                    return ProductResponse.builder()
                            .id(product.getId())
                            .title(product.getTitle())
                            .price(product.getPrice())
                            .image(product.getImage())
                            .categoryName(product.getCategory().getName())
                            .brandName(product.getBrand().getName())
                            .soldQuantity(product.getSoldQuantity())  // ← NEW: Add sold quantity
                            .flashSale(flashSaleInfo)  // ✅ Include flash sale
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Cacheable(value = "productDetail", key = "#id")
    @Override
    public ProductDetailResponse getProductById(Long id) {
        // Use custom query to eagerly fetch details and inventories
        Product product = productRepository.findByIdWithDetailsAndInventories(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Convert ProductDetails to SizeOptions with stock from Inventory
        List<ProductDetailResponse.SizeOption> sizeOptions = product.getDetails().stream()
                .map(detail -> {
                    // Get total quantity from all Inventory records for this ProductDetail
                    int stock = inventoryRepository.getTotalQuantityByProductDetail(detail);
                    
                    System.out.println("=== Processing ProductDetail ID: " + detail.getId() 
                            + ", Size: " + detail.getSize() 
                            + ", Stock from Inventory: " + stock);
                    
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
        // đoạn này huy làm chưa đụng đến


//        if (product.getRatings() != null && !product.getRatings().isEmpty()) {
//            totalReviews = product.getRatings().size();
//            avgRating = product.getRatings().stream()
//                    .mapToInt(Rating::getStar)
//                    .average()
//                    .orElse(0.0);
//        }

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
                .soldQuantity(product.getSoldQuantity())  // ← NEW: Add sold quantity
                .flashSale(getFlashSaleInfoForDetail(product))  // ← NEW: Add flash sale info
                .build();
    }
    
    /**
     * Helper method for ProductDetailResponse: Lấy thông tin Flash Sale của Product (nếu có)
     * 
     * ✅ OPTIMIZED: Data đã được load sẵn bởi findByIdWithDetailsAndInventories() query
     * - Method này chỉ traverse data trong memory → ZERO additional queries
     */
    private ProductDetailResponse.FlashSaleInfo getFlashSaleInfoForDetail(Product product) {
        if (product.getDetails() == null || product.getDetails().isEmpty()) {
            return null;
        }
        
        // Duyệt qua tất cả ProductDetail của product
        for (ProductDetail detail : product.getDetails()) {
            // Check xem detail có flash sale item nào đang active không
            if (detail.getFlashSaleItems() != null && !detail.getFlashSaleItems().isEmpty()) {
                // Tìm flash sale item đầu tiên đang active
                var activeFlashSaleItem = detail.getFlashSaleItems().stream()
                        .filter(item -> item.getFlashSale() != null && item.getFlashSale().isActive())
                        .findFirst();
                
                if (activeFlashSaleItem.isPresent()) {
                    var item = activeFlashSaleItem.get();
                    var flashSale = item.getFlashSale();
                    
                    // Tính giá flash sale
                    double flashSalePrice = item.getFlashSalePrice();
                    double discountPercent = item.getDiscountPercent();
                    
                    // Tính stock info từ FlashSale entity
                    int totalStock = flashSale.getTotalItems() != null ? flashSale.getTotalItems() : 0;
                    int sold = flashSale.getTotalSold() != null ? flashSale.getTotalSold() : 0;
                    int remaining = totalStock - sold;
                    double soldPercentage = totalStock > 0 ? ((double) sold / totalStock) * 100 : 0;
                    
                    // Return Flash Sale Info for ProductDetailResponse
                    return ProductDetailResponse.FlashSaleInfo.builder()
                            .id(flashSale.getId()) // 🔥 THÊM Flash Sale ID
                            .active(true)
                            .flashSalePrice(flashSalePrice)
                            .discountPercent(discountPercent)
                            .endTime(flashSale.getEndTime())
                            .stock(totalStock)
                            .sold(sold)
                            .remaining(remaining)
                            .soldPercentage(soldPercentage)
                            .build();
                }
            }
        }
        
        // Không có flash sale active
        return null;
    }
    
    /**
     * Helper method for ProductResponse: Lấy thông tin Flash Sale của Product (nếu có)
     * 
     * ✅ OPTIMIZED: Sau khi apply Fix 2, method này KHÔNG TẠO THÊM QUERIES
     * - Tất cả data (ProductDetails + FlashSaleItems + FlashSale) đã được load sẵn bởi JOIN FETCH
     * - Method này chỉ traverse data có sẵn trong memory → ZERO queries!
     * 
     * Logic:
     * - Duyệt qua tất cả ProductDetail của Product (already loaded)
     * - Check xem ProductDetail có FlashSaleItem nào đang active không (already loaded)
     * - Nếu có → return FlashSaleInfo
     * - Nếu không → return null
     */
    private ProductResponse.FlashSaleInfo getFlashSaleInfo(Product product) {
        if (product.getDetails() == null || product.getDetails().isEmpty()) {
            return null;
        }
        
        // Duyệt qua tất cả ProductDetail của product
        for (ProductDetail detail : product.getDetails()) {
            // Check xem detail có flash sale item nào đang active không
            if (detail.getFlashSaleItems() != null && !detail.getFlashSaleItems().isEmpty()) {
                // Tìm flash sale item đầu tiên đang active
                var activeFlashSaleItem = detail.getFlashSaleItems().stream()
                        .filter(item -> item.getFlashSale() != null && item.getFlashSale().isActive())
                        .findFirst();
                
                if (activeFlashSaleItem.isPresent()) {
                    var item = activeFlashSaleItem.get();
                    var flashSale = item.getFlashSale();
                    
                    // Tính giá flash sale
                    double originalPrice = detail.getFinalPrice();
                    double flashSalePrice = item.getFlashSalePrice();
                    double discountPercent = item.getDiscountPercent();
                    
                    // Return Flash Sale Info
                    return ProductResponse.FlashSaleInfo.builder()
                            .id(flashSale.getId()) // 🔥 THÊM Flash Sale ID
                            .active(true)
                            .flashSalePrice(flashSalePrice)
                            .originalPrice(originalPrice)
                            .discountPercent(discountPercent)
                            .flashSaleName(flashSale.getName())
                            .stock(null)  // TODO: Implement stock tracking nếu cần
                            .build();
                }
            }
        }
        
        // Không có flash sale active
        return null;
    }
    
    /**
     * UPDATE PRODUCT - RESTful API
     * 
     * @param id Product ID
     * @param request ProductRequest with updated data
     * @param image New product image (optional)
     */
    @CacheEvict(value = {"products", "productDetail"}, allEntries = true)
    @Override
    @Transactional
    public void updateProduct(Long id, ProductRequest request, MultipartFile image) {
        // Find existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
        
        // Update basic fields
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(categoryService.getCategoryById(request.getCategoryId()));
        product.setBrand(brandService.getBrandById(request.getBrandId()));
        
        // Update image if provided
        if (image != null && !image.isEmpty()) {
            String fileName = image.getOriginalFilename();
            if (fileName != null && !fileName.isEmpty()) {
                try {
                    // Save new image
                    String uploadDir = "uploads/products/";
                    java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                    if (!java.nio.file.Files.exists(uploadPath)) {
                        java.nio.file.Files.createDirectories(uploadPath);
                    }
                    
                    String newFileName = fileName;
                    java.nio.file.Path filePath = uploadPath.resolve(newFileName);
                    image.transferTo(filePath.toFile());
                    
                    product.setImage("/uploads/products/" + newFileName);
                } catch (Exception e) {
                    throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage());
                }
            }
        }
        
        productRepository.save(product);
        
        // Update ProductDetails if provided
        if (request.getProductDetails() != null && !request.getProductDetails().isEmpty()) {
            // Clear existing details
            product.getDetails().clear();
            productRepository.save(product); // Flush changes
            
            // Add new details
            request.getProductDetails().forEach(detailReq -> {
                ProductDetail detail = new ProductDetail();
                detail.setProduct(product);
                detail.setSize(detailReq.getSize());
                detail.setPriceadd(detailReq.getPriceAdd());
                productDetailService.save(detail);
            });
        }
    }
    
    /**
     * DELETE PRODUCT - Soft Delete
     * Set isDelete = true instead of deleting from database
     */
    @CacheEvict(value = {"products", "productDetail"}, allEntries = true)
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
        
        // Soft delete
        product.setDelete(true);
        productRepository.save(product);
    }
}
