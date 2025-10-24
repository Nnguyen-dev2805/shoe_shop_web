package com.dev.shoeshop.controller.web.api;

import com.dev.shoeshop.dto.dashboard.TopProductDTO;
import com.dev.shoeshop.dto.pagination.PaginationResponse;
import com.dev.shoeshop.dto.product.ProductResponse;
import com.dev.shoeshop.service.DashboardService;
import com.dev.shoeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ApiHomeController {

    private final ProductService productService;
    private final DashboardService dashboardService;

    /**
     * Get products with pagination and optional search
     * Endpoint: GET /api/shop/products
     * @param page - page number (0-based)
     * @param size - number of items per page
     * @param search - optional search keyword
     * @param categoryId - optional category ID for filtering
     * @param sortBy - sort field (default: id)
     * @param sortDir - sort direction (asc/desc, default: asc)
     */
    @GetMapping("/products")
    public ResponseEntity<PaginationResponse<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ProductResponse> productPage = productService.getAllProducts(pageable, search, categoryId);
            
            PaginationResponse<ProductResponse> response = new PaginationResponse<>(
                    productPage.getContent(),
                    productPage.getTotalPages(),
                    productPage.getTotalElements(),
                    productPage.getNumber(),  // 0-based for frontend
                    productPage.getSize()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Get products by category with pagination
     * Endpoint: GET /api/shop/products/category/{categoryId}
     * @param categoryId - category ID
     * @param page - page number (0-based)
     * @param size - number of items per page
     * @param sortBy - sort field (default: id)
     * @param sortDir - sort direction (asc/desc, default: asc)
     */
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<PaginationResponse<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            // Bây giờ đã implement category filtering
            Page<ProductResponse> productPage = productService.getAllProducts(pageable, null, categoryId);

            PaginationResponse<ProductResponse> response = new PaginationResponse<>(
                    productPage.getContent(),
                    productPage.getTotalPages(),
                    productPage.getTotalElements(),
                    productPage.getNumber(),  // 0-based for frontend
                    productPage.getSize()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Get all products without pagination (for list display)
     * Endpoint: GET /api/shop/products/list
     */
    @GetMapping("/products/list")
    public ResponseEntity<List<ProductResponse>> getAllProductsList() {
        try {
            List<ProductResponse> products = productService.getAllProductsList();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Get top best-selling products
     * Endpoint: GET /api/shop/best-sellers
     * @param limit - number of products to return (default: 10)
     */
    @GetMapping("/best-sellers")
    public ResponseEntity<List<TopProductDTO>> getBestSellingProducts(
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            // Lấy top sản phẩm bán chạy nhất (không giới hạn thời gian)
            List<TopProductDTO> bestSellers = dashboardService.getProductsByQuantity(null, null, limit);
            return ResponseEntity.ok(bestSellers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
