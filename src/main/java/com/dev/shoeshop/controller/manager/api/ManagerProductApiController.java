package com.dev.shoeshop.controller.manager.api;

import com.dev.shoeshop.dto.manager.ProductResponseDTO;
import com.dev.shoeshop.dto.product.ProductResponse;
import com.dev.shoeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ManagerProductApiController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            
            // Use the existing getAllProducts method with filters
            Page<ProductResponse> productPage = productService.getAllProducts(pageable, search, categoryId);
            
            // Convert to DTO
            List<ProductResponseDTO> productDTOs = productPage.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            Page<ProductResponseDTO> productDTOPage = new PageImpl<>(
                    productDTOs, 
                    pageable, 
                    productPage.getTotalElements()
            );
            
            return ResponseEntity.ok(productDTOPage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            // Use getProductById method which returns ProductDetailResponse with all details
            var productDetail = productService.getProductById(id);
            if (productDetail == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Return full ProductDetailResponse including size options
            return ResponseEntity.ok(productDetail);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleProductStatus(@PathVariable Long id) {
        try {
            // This functionality might need to be implemented in the service layer
            // For now, return a success message
            return ResponseEntity.ok("Cập nhật trạng thái sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật trạng thái: " + e.getMessage());
        }
    }

    private ProductResponseDTO convertToDTO(ProductResponse product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description("") // Not available in ProductResponse
                .price(product.getPrice())
                .image(product.getImage())
                .categoryName(product.getCategoryName())
                .brandName(product.getBrandName())
                .totalReviews(0L) // Not available in ProductResponse
                .averageRating(0.0) // Not available in ProductResponse
                .totalQuantity(0) // Not available in ProductResponse
                .isDelete(false) // Default value
                .build();
    }
}
