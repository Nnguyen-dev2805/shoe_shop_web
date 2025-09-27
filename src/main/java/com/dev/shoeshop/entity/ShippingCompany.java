package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipping_company")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingCompany {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @NotBlank(message = "Tên công ty vận chuyển không được để trống")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "hotline", length = 50)
    private String hotline;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "address", length = 255)
    private String address;
    
    @Column(name = "website", length = 255)
    private String website;
    
    @NotNull(message = "Trạng thái hoạt động không được để trống")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Constructor cho tạo mới
    public ShippingCompany(String name, String hotline, String email, String address, String website, Boolean isActive) {
        this.name = name;
        this.hotline = hotline;
        this.email = email;
        this.address = address;
        this.website = website;
        this.isActive = isActive;
    }
}
