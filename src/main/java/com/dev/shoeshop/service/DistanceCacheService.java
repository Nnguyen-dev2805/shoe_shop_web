package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.DistanceResult;
import com.dev.shoeshop.entity.DistanceCache;
import com.dev.shoeshop.repository.DistanceCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistanceCacheService {
    
    private final DistanceCacheRepository cacheRepository;
    
    @Value("${shipping.cache.expiry.days:30}")
    private int cacheExpiryDays;
    
    /**
     * Round coordinates to 4 decimal places (~11 meters precision)
     */
    private double roundCoordinate(double coord) {
        return Math.round(coord * 10000.0) / 10000.0;
    }
    
    /**
     * Lấy khoảng cách từ cache
     */
    public Optional<DistanceResult> getCachedDistance(double originLat, double originLng,
                                                      double destLat, double destLng) {
        // Round coordinates
        double roundedOriginLat = roundCoordinate(originLat);
        double roundedOriginLng = roundCoordinate(originLng);
        double roundedDestLat = roundCoordinate(destLat);
        double roundedDestLng = roundCoordinate(destLng);
        
        log.debug("🔍 Checking cache for: ({},{}) -> ({},{})",
                roundedOriginLat, roundedOriginLng, roundedDestLat, roundedDestLng);
        
        Optional<DistanceCache> cached = cacheRepository.findByCoordinates(
                roundedOriginLat, roundedOriginLng,
                roundedDestLat, roundedDestLng
        );
        
        if (cached.isPresent()) {
            log.info("✅ Cache HIT! Distance: {} km", cached.get().getDistanceKm());
            DistanceCache cache = cached.get();
            return Optional.of(DistanceResult.builder()
                    .distanceMeters(cache.getDistanceMeters())
                    .distanceKm(cache.getDistanceKm())
                    .durationSeconds(cache.getDurationSeconds())
                    .build());
        }
        
        log.info("❌ Cache MISS - will call API");
        return Optional.empty();
    }
    
    /**
     * Lưu khoảng cách vào cache
     */
    @Transactional
    public void saveToCache(double originLat, double originLng,
                           double destLat, double destLng,
                           DistanceResult result) {
        // Round coordinates
        double roundedOriginLat = roundCoordinate(originLat);
        double roundedOriginLng = roundCoordinate(originLng);
        double roundedDestLat = roundCoordinate(destLat);
        double roundedDestLng = roundCoordinate(destLng);
        
        DistanceCache cache = new DistanceCache();
        cache.setOriginLat(roundedOriginLat);
        cache.setOriginLng(roundedOriginLng);
        cache.setDestLat(roundedDestLat);
        cache.setDestLng(roundedDestLng);
        cache.setDistanceMeters(result.getDistanceMeters());
        cache.setDurationSeconds(result.getDurationSeconds());
        
        cacheRepository.save(cache);
        log.info("💾 Saved to cache: {} km", result.getDistanceKm());
    }
    
    /**
     * Xóa cache cũ (chạy hàng ngày lúc 2:00 AM)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanOldCache() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(cacheExpiryDays);
        int deleted = cacheRepository.deleteByCachedAtBefore(cutoffDate);
        log.info("🧹 Cleaned {} old distance cache entries (older than {} days)", deleted, cacheExpiryDays);
    }
    
    /**
     * Get cache statistics
     */
    public long getCacheCount() {
        return cacheRepository.count();
    }
}
