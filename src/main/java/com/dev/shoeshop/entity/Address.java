package com.dev.shoeshop.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ⭐ PHIÊN BẢN ĐƠN GIẢN - Gộp địa chỉ chi tiết vào address_line
    @Column(name = "address_line", nullable = false, length = 500)
    private String address_line; // Bao gồm: Số nhà, đường, phường, quận (VD: "Toyota Dũng Tiến, 233 Đại Lộ Hùng Vương, Phường 5, Tuy Hòa")

    @Column(name = "city", nullable = false, length = 100)
    private String city; // Tỉnh/Thành phố (VD: "Phú Yên", "Hồ Chí Minh")

    @Column(name = "country", length = 100)
    private String country; // Mặc định "Việt Nam"
    
    // ⭐ GPS Coordinates - QUAN TRỌNG cho shipper tìm địa chỉ chính xác & tính phí ship
    @Column(name = "latitude")
    private Double latitude;  // VD: 13.0883 (Phú Yên)
    
    @Column(name = "longitude")
    private Double longitude; // VD: 109.2958
    
    // ⭐ Address Type
    @Column(name = "address_type", length = 20)
    private String addressType; // HOME hoặc OFFICE (chỉ 2 loại)
    
    // Note: Không dùng trong version đơn giản
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<UserAddress> userAddresses = new ArrayList<>();


    @OneToMany(mappedBy = "address",cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Order>Orders;
}
