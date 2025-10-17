package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.ShippingFeeResponse;
import com.dev.shoeshop.service.ShippingFeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@Slf4j
public class ShippingFeeController {
    
    private final ShippingFeeService shippingFeeService;
    
    /**
     * Calculate shipping fee by address ID
     * GET /api/shipping/calculate-fee?addressId=1
     */
    @GetMapping("/calculate-fee")
    public ResponseEntity<ShippingFeeResponse> calculateFee(@RequestParam Long addressId) {
        log.info("📦 API: Calculate shipping fee for addressId={}", addressId);
        
        ShippingFeeResponse response = shippingFeeService.calculateShippingFee(addressId);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Calculate shipping fee by coordinates
     * GET /api/shipping/calculate-fee-coords?lat=10.8050&lng=106.6920
     */
    @GetMapping("/calculate-fee-coords")
    public ResponseEntity<ShippingFeeResponse> calculateFeeByCoords(
            @RequestParam Double lat,
            @RequestParam Double lng) {
        
        log.info("📦 API: Calculate shipping fee for coords: ({}, {})", lat, lng);
        
        ShippingFeeResponse response = shippingFeeService.calculateShippingFeeByCoords(lat, lng);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
