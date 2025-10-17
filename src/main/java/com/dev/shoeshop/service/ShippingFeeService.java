package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.DistanceResult;
import com.dev.shoeshop.dto.ShippingFeeResponse;
import com.dev.shoeshop.entity.Address;
import com.dev.shoeshop.entity.ShippingRate;
import com.dev.shoeshop.entity.ShopWarehouse;
import com.dev.shoeshop.repository.AddressRepository;
import com.dev.shoeshop.repository.ShippingRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingFeeService {
    
    private final ShopWarehouseService warehouseService;
    private final GoongDistanceService goongDistanceService;
    private final DistanceCacheService cacheService;
    private final ShippingRateRepository shippingRateRepository;
    private final AddressRepository addressRepository;
    
    /**
     * Tính phí ship dựa trên address ID
     */
    public ShippingFeeResponse calculateShippingFee(Long addressId) {
        try {
            log.info("💰 Calculating shipping fee for addressId: {}", addressId);
            
            // 1. Get user address
            Optional<Address> addressOpt = addressRepository.findById(addressId);
            if (addressOpt.isEmpty()) {
                return ShippingFeeResponse.error("Không tìm thấy địa chỉ", "ADDRESS_NOT_FOUND");
            }
            
            Address address = addressOpt.get();
            
            // Check if address has coordinates
            if (address.getLatitude() == null || address.getLongitude() == null) {
                return ShippingFeeResponse.error(
                    "Địa chỉ chưa có tọa độ GPS. Vui lòng cập nhật địa chỉ.", 
                    "MISSING_COORDINATES"
                );
            }
            
            return calculateShippingFeeByCoords(address.getLatitude(), address.getLongitude());
            
        } catch (Exception e) {
            log.error("❌ Error calculating shipping fee", e);
            return ShippingFeeResponse.error("Có lỗi xảy ra khi tính phí ship", "INTERNAL_ERROR");
        }
    }
    
    /**
     * Tính phí ship dựa trên tọa độ
     */
    public ShippingFeeResponse calculateShippingFeeByCoords(double userLat, double userLng) {
        try {
            log.info("📍 Calculating fee for coordinates: ({}, {})", userLat, userLng);
            
            // 2. Get default warehouse (or nearest)
            Optional<ShopWarehouse> warehouseOpt = warehouseService.getDefaultWarehouse();
            if (warehouseOpt.isEmpty()) {
                // Try to find nearest warehouse
                warehouseOpt = warehouseService.findNearestWarehouse(userLat, userLng);
            }
            
            if (warehouseOpt.isEmpty()) {
                return ShippingFeeResponse.error("Không tìm thấy kho hàng", "WAREHOUSE_NOT_FOUND");
            }
            
            ShopWarehouse warehouse = warehouseOpt.get();
            log.info("🏭 Using warehouse: {}", warehouse.getName());
            
            // 3. Check cache first
            Optional<DistanceResult> cachedResult = cacheService.getCachedDistance(
                    warehouse.getLatitude(), warehouse.getLongitude(),
                    userLat, userLng
            );
            
            DistanceResult distanceResult;
            
            if (cachedResult.isPresent()) {
                // Cache hit
                distanceResult = cachedResult.get();
            } else {
                // Cache miss - call Goong API
                distanceResult = goongDistanceService.getDistance(
                        warehouse.getLatitude(), warehouse.getLongitude(),
                        userLat, userLng
                );
                
                // If API fails, use Haversine fallback
                if (distanceResult == null) {
                    log.warn("⚠️ Goong API failed, using Haversine fallback");
                    distanceResult = goongDistanceService.getDistanceHaversine(
                            warehouse.getLatitude(), warehouse.getLongitude(),
                            userLat, userLng
                    );
                }
                
                // Save to cache
                if (distanceResult != null) {
                    cacheService.saveToCache(
                            warehouse.getLatitude(), warehouse.getLongitude(),
                            userLat, userLng,
                            distanceResult
                    );
                }
            }
            
            if (distanceResult == null) {
                return ShippingFeeResponse.error("Không thể tính khoảng cách", "DISTANCE_CALCULATION_FAILED");
            }
            
            // 4. Get shipping fee by distance
            Integer fee = getShippingFeeByDistance(distanceResult.getDistanceKm());
            
            log.info("✅ Shipping fee calculated: {} VND for {} km", fee, distanceResult.getDistanceKm());
            
            return ShippingFeeResponse.success(
                    fee,
                    distanceResult.getDistanceKm(),
                    warehouse.getName(),
                    warehouse.getAddress(),
                    distanceResult.getFormattedDistance(),
                    distanceResult.getFormattedDuration()
            );
            
        } catch (Exception e) {
            log.error("❌ Error calculating shipping fee by coords", e);
            return ShippingFeeResponse.error("Có lỗi xảy ra khi tính phí ship", "INTERNAL_ERROR");
        }
    }
    
    /**
     * Lấy shipping fee dựa trên khoảng cách
     */
    public Integer getShippingFeeByDistance(double distanceKm) {
        BigDecimal distance = BigDecimal.valueOf(distanceKm);
        
        Optional<ShippingRate> rateOpt = shippingRateRepository.findByDistanceRange(distance);
        
        if (rateOpt.isPresent()) {
            return rateOpt.get().getFee();
        }
        
        // Default fee if no rate found (shouldn't happen if rates are set up correctly)
        log.warn("⚠️ No shipping rate found for distance: {} km, using default fee", distanceKm);
        return 30000; // Default 30k VND
    }
}
