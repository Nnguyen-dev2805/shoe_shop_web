package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "min_distance_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal minDistanceKm;
    
    @Column(name = "max_distance_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxDistanceKm;
    
    @Column(nullable = false)
    private Integer fee;
    
    @Column(length = 255)
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
