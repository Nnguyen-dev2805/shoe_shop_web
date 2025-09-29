package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.brand.BrandResponse;
import com.dev.shoeshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class ApiBrandController {

    private final BrandService brandService;

    @GetMapping("/list")
    public ResponseEntity<List<BrandResponse>> getAllCategoriesList() {
        try {
            List<BrandResponse> brands = brandService.getAllBrandsList();
            return ResponseEntity.ok(brands);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
