package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.product.ProductDetailResponse;
import com.dev.shoeshop.dto.product.ProductRequest;
import com.dev.shoeshop.dto.product.ProductResponse;
import com.dev.shoeshop.dto.productdetail.ProductDetailRequest;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.entity.Rating;
import com.dev.shoeshop.repository.InventoryRepository;
import com.dev.shoeshop.repository.ProductDetailRepository;
import com.dev.shoeshop.repository.ProductRepository;
import com.dev.shoeshop.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    private final StorageService storageService;

    private final ProductDetailService productDetailService;

    private final BrandService brandService;

    private final InventoryRepository inventoryRepository;
    
    private final ProductDetailRepository productDetailRepository;


    /**
     * 🗑️ CACHE EVICT: Clear product caches when saving new product
     */
    @Transactional
    @Override
    @CacheEvict(value = {"products", "productDetails"}, allEntries = true)
    public void saveProduct(ProductRequest request, MultipartFile image) {
        log.info("➕ Creating new product, clearing products cache");
        
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

    /**
     * ⚡ CACHED: Get products with pagination, search, and category filter
     * Cache key includes: page, size, search, categoryId
     */
    @Override
    @Cacheable(value = "products", 
               key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + (#search != null ? #search : 'none') + ':' + (#categoryId != null ? #categoryId : 'none')",
               unless = "#result == null")
    public Page<ProductResponse> getAllProducts(Pageable pageable, String search, Long categoryId) {
        log.info("📦 Loading products (page: {}, search: {}, category: {})", 
                 pageable.getPageNumber(), search, categoryId);
        
        Page<Product> productPage;
        
        // ✅ SOFT DELETE + EAGER LOADING: Lấy sản phẩm chưa xóa với Flash Sale info
        
        // Filter by category and search
        if (categoryId != null && search != null && !search.trim().isEmpty()) {
            productPage = productRepository.findByCategoryAndTitleWithFlashSale(categoryId, search, pageable);
        } 
        // Filter by category only
        else if (categoryId != null) {
            productPage = productRepository.findByCategoryWithFlashSale(categoryId, pageable);
        } 
        // Search only
        else if (search != null && !search.trim().isEmpty()) {
            productPage = productRepository.findByTitleWithFlashSale(search, pageable);
        } 
        // Get all (exclude deleted)
        else {
            productPage = productRepository.findAllWithFlashSale(pageable);
        }
        
        List<ProductResponse> responses = productPage.getContent().stream()
                .map(product -> {
                    // ========== CHECK FLASH SALE ==========
                    ProductResponse.FlashSaleInfo flashSaleInfo = getFlashSaleInfo(product);
                    
                    // Tạo ProductResponse với Builder pattern (tránh lỗi constructor)
                    return ProductResponse.builder()
                            .id(product.getId())
                            .title(product.getTitle())
                            .price(product.getPrice())
                            .image(product.getImage())
                            .categoryName(product.getCategory().getName())
                            .brandName(product.getBrand().getName())
                            .soldQuantity(product.getSoldQuantity())  // ← NEW: Add sold quantity
                            .averageRating(product.getAverage_rating())  // ← NEW: Add rating
                            .totalReviews(product.getTotal_reviews())    // ← NEW: Add review count
                            .flashSale(flashSaleInfo)  // ✅ Include flash sale
                            .build();
                })
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, productPage.getTotalElements());
    }

    /**
     * ⚡ CACHED: Get all products list (for dropdowns/quick access)
     */
    @Override
    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponse> getAllProductsList() {
        log.info("📦 Loading all products list from database");
        
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
                            .averageRating(product.getAverage_rating())  // ← NEW: Add rating
                            .totalReviews(product.getTotal_reviews())    // ← NEW: Add review count
                            .flashSale(flashSaleInfo)  // ✅ Include flash sale
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * ⚡ CACHED: Get product detail by ID
     */
    @Override
    @Cacheable(value = "productDetails", key = "'detail:' + #id")
    public ProductDetailResponse getProductById(Long id) {
        log.info("📦 Loading product detail {} from database", id);
        
        // Use custom query to eagerly fetch details and inventories
        Product product = productRepository.findByIdWithDetailsAndInventories(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // ✅ FIX: Remove duplicates caused by JPA Cartesian product (multiple JOIN FETCH)
        // Use LinkedHashSet to maintain order and remove duplicates by ProductDetail.id
        List<ProductDetail> uniqueDetails = product.getDetails().stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        ProductDetail::getId,
                        detail -> detail,
                        (existing, replacement) -> existing, // Keep first occurrence
                        LinkedHashMap::new
                    ),
                    map -> new ArrayList<>(map.values())
                ));
        
        System.out.println("=== Before dedup: " + product.getDetails().size() 
                + " | After dedup: " + uniqueDetails.size());

        // Convert ProductDetails to SizeOptions with stock from Inventory
        List<ProductDetailResponse.SizeOption> sizeOptions = uniqueDetails.stream()
                .map(detail -> {
                    // Get remaining quantity from Inventory for this ProductDetail
                    int stock = inventoryRepository.findByProductDetail(detail)
                            .map(inv -> inv.getRemainingQuantity() != null ? inv.getRemainingQuantity() : 0)
                            .orElse(0);
                    
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

        // ✅ Get rating and reviews from Product entity (stored in database)
        Double avgRating = product.getAverage_rating() != null ? product.getAverage_rating() : 0.0;
        Long totalReviews = product.getTotal_reviews() != null ? product.getTotal_reviews() : 0L;

        return ProductDetailResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .image(product.getImage())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "N/A")
                .brandName(product.getBrand() != null ? product.getBrand().getName() : "N/A")
                .sizeOptions(sizeOptions)
                .avgRating(avgRating)  // ✅ From database
                .totalReviews(totalReviews.intValue())  // ✅ From database
                .soldQuantity(product.getSoldQuantity())  // ← NEW: Add sold quantity
                .flashSale(getFlashSaleInfoForDetail(product))  // ← NEW: Add flash sale info
                .build();
    }
    
    /**
     * Helper method for ProductDetailResponse: Lấy thông tin Flash Sale của Product (nếu có)
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
     * Logic:
     * - Duyệt qua tất cả ProductDetail của Product
     * - Check xem ProductDetail có FlashSaleItem nào đang active không
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
     * 🗑️ CACHE EVICT: Clear product caches when updating product
     * UPDATE PRODUCT - RESTful API
     * 
     * @param id Product ID
     * @param request ProductRequest with updated data
     * @param image New product image (optional)
     */
    @Override
    @Transactional
    @CacheEvict(value = {"products", "productDetails"}, allEntries = true)
    public void updateProduct(Long id, ProductRequest request, MultipartFile image) {
        log.info("✏️ Updating product {}, clearing products cache", id);
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
     * DELETE PRODUCT - RESTful API (Soft Delete)
     * 
     * @param id Product ID
     */
    /**
     * 🗑️ CACHE EVICT: Clear product caches when deleting product
     */
    @Override
    @Transactional
    @CacheEvict(value = {"products", "productDetails"}, allEntries = true)
    public void deleteProduct(Long id) {
        log.info("🗑️ Deleting product {}, clearing products cache", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
        
        // Soft delete
        product.setDelete(true);
        productRepository.save(product);
    }
}
