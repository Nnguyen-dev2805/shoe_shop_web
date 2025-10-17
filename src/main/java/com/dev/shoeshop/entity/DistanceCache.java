package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "distance_cache", indexes = {
    @Index(name = "idx_coords", columnList = "origin_lat,origin_lng,dest_lat,dest_lng"),
    @Index(name = "idx_cached_at", columnList = "cached_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistanceCache {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "origin_lat", nullable = false)
    private Double originLat;
    
    @Column(name = "origin_lng", nullable = false)
    private Double originLng;
    
    @Column(name = "dest_lat", nullable = false)
    private Double destLat;
    
    @Column(name = "dest_lng", nullable = false)
    private Double destLng;
    
    @Column(name = "distance_meters", nullable = false)
    private Integer distanceMeters;
    
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;
    
    @Column(name = "cached_at")
    private LocalDateTime cachedAt;
    
    @PrePersist
    protected void onCreate() {
        cachedAt = LocalDateTime.now();
    }
    
    // Helper method to get distance in km
    public Double getDistanceKm() {
        return distanceMeters / 1000.0;
    }
    
    // Helper method to get formatted duration
    public String getFormattedDuration() {
        int minutes = durationSeconds / 60;
        return minutes + " phút";
    }
}
