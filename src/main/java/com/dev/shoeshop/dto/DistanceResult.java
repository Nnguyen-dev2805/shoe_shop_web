package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistanceResult {
    
    private Double distanceKm;
    private Integer distanceMeters;
    private Integer durationSeconds;
    private String formattedDistance;
    private String formattedDuration;
    
    /**
     * Format distance for display
     */
    public String getFormattedDistance() {
        if (distanceKm == null) return "N/A";
        if (distanceKm < 1) {
            return String.format("%d m", distanceMeters);
        }
        return String.format("%.1f km", distanceKm);
    }
    
    /**
     * Format duration for display
     */
    public String getFormattedDuration() {
        if (durationSeconds == null) return "N/A";
        int minutes = durationSeconds / 60;
        if (minutes < 60) {
            return minutes + " phút";
        }
        int hours = minutes / 60;
        int mins = minutes % 60;
        return hours + " giờ " + mins + " phút";
    }
}
