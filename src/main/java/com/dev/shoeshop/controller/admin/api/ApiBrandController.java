package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.brand.BrandRequest;
import com.dev.shoeshop.dto.brand.BrandResponse;
import com.dev.shoeshop.dto.pagination.PaginationResponse;
import com.dev.shoeshop.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class ApiBrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<PaginationResponse<BrandResponse>> getBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BrandResponse> brandPage = brandService.getAllBrands(pageable, search);
            PaginationResponse<BrandResponse> response = new PaginationResponse<>(
                    brandPage.getContent(),
                    brandPage.getTotalPages(),
                    brandPage.getTotalElements(),
                    brandPage.getNumber() + 1,  // frontend dùng 1-based page
                    brandPage.getSize()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<BrandResponse>> getAllBrandsList() {
        try {
            List<BrandResponse> brands = brandService.getAllBrandsList();
            return ResponseEntity.ok(brands);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> createBrand(@Valid @RequestBody BrandRequest request,
                                         BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Lỗi validation: ");
            for (var error : result.getFieldErrors()) {
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMsg.toString());
        }
        try {
            BrandResponse brand = brandService.createBrand(request);
            return ResponseEntity.ok(brand);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Long id,
                                         @Valid @RequestBody BrandRequest request,
                                         BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Lỗi validation: ");
            for (var error : result.getFieldErrors()) {
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMsg.toString());
        }
        try {
            BrandResponse brand = brandService.updateBrand(id, request);
            return ResponseEntity.ok(brand);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.ok("Xóa brand thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        try {
            BrandResponse brand = new BrandResponse(
                    brandService.getBrandById(id).getId(),
                    brandService.getBrandById(id).getName()
            );
            return ResponseEntity.ok(brand);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
