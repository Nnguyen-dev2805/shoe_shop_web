package com.dev.shoeshop.service;

import com.dev.shoeshop.entity.ShopWarehouse;
import com.dev.shoeshop.repository.ShopWarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopWarehouseService {
    
    private final ShopWarehouseRepository warehouseRepository;
    
    /**
     * Lấy tất cả kho
     */
    public List<ShopWarehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
    
    /**
     * Lấy tất cả kho đang hoạt động
     */
    public List<ShopWarehouse> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue();
    }
    
    /**
     * Lấy kho mặc định
     */
    public Optional<ShopWarehouse> getDefaultWarehouse() {
        return warehouseRepository.findByIsDefaultTrue();
    }
    
    /**
     * Lấy kho theo ID
     */
    public Optional<ShopWarehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }
    
    /**
     * Lấy kho gần nhất theo city
     */
    public Optional<ShopWarehouse> getWarehouseByCity(String city) {
        return warehouseRepository.findByCityAndIsActiveTrue(city);
    }
    
    /**
     * Thêm hoặc cập nhật kho
     */
    @Transactional
    public ShopWarehouse saveWarehouse(ShopWarehouse warehouse) {
        // Nếu set làm default, bỏ default của các kho khác
        if (warehouse.getIsDefault() != null && warehouse.getIsDefault()) {
            warehouseRepository.findByIsDefaultTrue().ifPresent(existingDefault -> {
                if (!existingDefault.getId().equals(warehouse.getId())) {
                    existingDefault.setIsDefault(false);
                    warehouseRepository.save(existingDefault);
                    log.info("Removed default flag from warehouse: {}", existingDefault.getName());
                }
            });
        }
        
        // Nếu đây là kho đầu tiên, tự động set làm default
        if (warehouseRepository.count() == 0) {
            warehouse.setIsDefault(true);
        }
        
        ShopWarehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse saved: {} (ID: {})", saved.getName(), saved.getId());
        return saved;
    }
    
    /**
     * Set kho làm mặc định
     */
    @Transactional
    public void setDefaultWarehouse(Long warehouseId) {
        // Bỏ default của tất cả kho
        warehouseRepository.findByIsDefaultTrue().ifPresent(existing -> {
            existing.setIsDefault(false);
            warehouseRepository.save(existing);
        });
        
        // Set default cho kho được chọn
        warehouseRepository.findById(warehouseId).ifPresent(warehouse -> {
            warehouse.setIsDefault(true);
            warehouseRepository.save(warehouse);
            log.info("Set default warehouse: {}", warehouse.getName());
        });
    }
    
    /**
     * Xóa kho
     */
    @Transactional
    public void deleteWarehouse(Long id) {
        warehouseRepository.findById(id).ifPresent(warehouse -> {
            // Không cho xóa kho mặc định nếu còn kho khác
            if (warehouse.getIsDefault() && warehouseRepository.count() > 1) {
                throw new IllegalStateException("Không thể xóa kho mặc định. Vui lòng chọn kho khác làm mặc định trước.");
            }
            
            warehouseRepository.delete(warehouse);
            log.info("Warehouse deleted: {}", warehouse.getName());
        });
    }
    
    /**
     * Tính khoảng cách từ user đến kho (Haversine formula)
     */
    public double calculateDistance(double userLat, double userLng, ShopWarehouse warehouse) {
        final int EARTH_RADIUS_KM = 6371;
        
        double dLat = Math.toRadians(warehouse.getLatitude() - userLat);
        double dLng = Math.toRadians(warehouse.getLongitude() - userLng);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(warehouse.getLatitude()))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Tìm kho gần nhất với user address
     */
    public Optional<ShopWarehouse> findNearestWarehouse(double userLat, double userLng) {
        List<ShopWarehouse> activeWarehouses = getActiveWarehouses();
        
        if (activeWarehouses.isEmpty()) {
            return Optional.empty();
        }
        
        return activeWarehouses.stream()
                .min((w1, w2) -> {
                    double dist1 = calculateDistance(userLat, userLng, w1);
                    double dist2 = calculateDistance(userLat, userLng, w2);
                    return Double.compare(dist1, dist2);
                });
    }
}
