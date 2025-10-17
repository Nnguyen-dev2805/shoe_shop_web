package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.DistanceResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GoongDistanceService {
    
    @Value("${goong.api.key}")
    private String goongApiKey;
    
    @Value("${goong.distance.matrix.url}")
    private String distanceMatrixUrl;
    
    @Value("${shipping.goong.enabled:true}")
    private boolean goongEnabled;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public GoongDistanceService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Tính khoảng cách giữa 2 điểm sử dụng Goong Distance Matrix API
     * 
     * @param originLat Vĩ độ điểm xuất phát
     * @param originLng Kinh độ điểm xuất phát
     * @param destLat Vĩ độ điểm đến
     * @param destLng Kinh độ điểm đến
     * @return DistanceResult chứa thông tin khoảng cách và thời gian
     */
    public DistanceResult getDistance(double originLat, double originLng, 
                                      double destLat, double destLng) {
        try {
            log.info("🚗 Calling Goong Distance Matrix API: ({},{}) -> ({},{})", 
                     originLat, originLng, destLat, destLng);
            
            // Check if Goong API is enabled
            if (!goongEnabled) {
                log.warn("⚠️ Goong API is disabled, using Haversine fallback");
                return getDistanceHaversine(originLat, originLng, destLat, destLng);
            }
            
            // Build URL
            String url = String.format("%s?origins=%f,%f&destinations=%f,%f&vehicle=car&api_key=%s",
                    distanceMatrixUrl,
                    originLat, originLng,
                    destLat, destLng,
                    goongApiKey);
            
            // Call API
            String response = restTemplate.getForObject(url, String.class);
            
            // Parse response
            JsonNode root = objectMapper.readTree(response);
            JsonNode element = root.path("rows").get(0).path("elements").get(0);
            
            String status = element.path("status").asText();
            if (!"OK".equals(status)) {
                log.error("❌ Goong API error: status={}", status);
                return null;
            }
            
            int distanceMeters = element.path("distance").path("value").asInt();
            int durationSeconds = element.path("duration").path("value").asInt();
            
            DistanceResult result = DistanceResult.builder()
                    .distanceMeters(distanceMeters)
                    .distanceKm(distanceMeters / 1000.0)
                    .durationSeconds(durationSeconds)
                    .build();
            
            log.info("✅ Distance calculated: {} km, {} minutes", 
                     result.getDistanceKm(), durationSeconds / 60);
            
            return result;
            
        } catch (Exception e) {
            log.error("❌ Error calling Goong Distance Matrix API", e);
            return null;
        }
    }
    
    /**
     * Calculate distance using Haversine formula (fallback when API fails)
     */
    public DistanceResult getDistanceHaversine(double originLat, double originLng,
                                               double destLat, double destLng) {
        final int EARTH_RADIUS_KM = 6371;
        
        double dLat = Math.toRadians(destLat - originLat);
        double dLng = Math.toRadians(destLng - originLng);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(originLat)) * Math.cos(Math.toRadians(destLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = EARTH_RADIUS_KM * c;
        
        // Estimate duration (assume 30 km/h average speed)
        int estimatedSeconds = (int) ((distanceKm / 30.0) * 3600);
        
        log.info("📏 Haversine fallback distance: {} km", distanceKm);
        
        return DistanceResult.builder()
                .distanceKm(distanceKm)
                .distanceMeters((int) (distanceKm * 1000))
                .durationSeconds(estimatedSeconds)
                .build();
    }
}
