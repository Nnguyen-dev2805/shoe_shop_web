package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop_warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ShopWarehouse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name; // VD: "Kho HCM", "Cửa hàng Hà Nội"
    
    @Column(name = "address", columnDefinition = "TEXT", nullable = false)
    private String address; // Địa chỉ đầy đủ
    
    @Column(name = "latitude", nullable = false)
    private Double latitude; // Vĩ độ
    
    @Column(name = "longitude", nullable = false)
    private Double longitude; // Kinh độ
    
    @Column(name = "city")
    private String city; // Tỉnh/Thành phố
    
    @Column(name = "phone")
    private String phone; // SĐT liên hệ
    
    @Column(name = "is_active")
    private Boolean isActive = true; // Kho có hoạt động không
    
    @Column(name = "is_default")
    private Boolean isDefault = false; // Kho mặc định
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
