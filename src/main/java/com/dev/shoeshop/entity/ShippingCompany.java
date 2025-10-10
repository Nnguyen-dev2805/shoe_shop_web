package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    
    // Quan hệ One-to-Many với Shipper
    @OneToMany(mappedBy = "shippingCompany", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Shipper> shippers = new ArrayList<>();
    
    // Constructor cho tạo mới
    public ShippingCompany(String name, String hotline, String email, String address, String website, Boolean isActive) {
        this.name = name;
        this.hotline = hotline;
        this.email = email;
        this.address = address;
        this.website = website;
        this.isActive = isActive;
    }
    
    // Business logic methods
    
    /**
     * Đếm số lượng shipper đang hoạt động
     */
    public long getActiveShipperCount() {
        if (shippers == null) return 0;
        return shippers.stream()
                .filter(Shipper::isWorking)
                .count();
    }
    
    /**
     * Đếm tổng số shipper
     */
    public long getTotalShipperCount() {
        return shippers != null ? shippers.size() : 0;
    }
    
    /**
     * Lấy danh sách shipper đang hoạt động
     */
    public List<Shipper> getActiveShippers() {
        if (shippers == null) return new ArrayList<>();
        return shippers.stream()
                .filter(Shipper::isWorking)
                .toList();
    }
    
    /**
     * Kiểm tra công ty có shipper nào đang hoạt động không
     */
    public boolean hasActiveShippers() {
        return getActiveShipperCount() > 0;
    }
    
    /**
     * Thêm shipper mới vào công ty
     */
    public void addShipper(Shipper shipper) {
        if (shippers == null) {
            shippers = new ArrayList<>();
        }
        shipper.setShippingCompany(this);
        shippers.add(shipper);
    }
    
    /**
     * Xóa shipper khỏi công ty
     */
    public void removeShipper(Shipper shipper) {
        if (shippers != null) {
            shippers.remove(shipper);
            shipper.setShippingCompany(null);
        }
    }
    
    /**
     * Lấy thông tin liên hệ đầy đủ
     */
    public String getFullContactInfo() {
        StringBuilder contact = new StringBuilder();
        contact.append("Công ty: ").append(name);
        if (hotline != null) {
            contact.append(", Hotline: ").append(hotline);
        }
        if (email != null) {
            contact.append(", Email: ").append(email);
        }
        if (address != null) {
            contact.append(", Địa chỉ: ").append(address);
        }
        return contact.toString();
    }
    
    /**
     * Đánh dấu công ty không hoạt động
     */
    public void deactivate() {
        this.isActive = false;
    }
    
    /**
     * Kích hoạt lại công ty
     */
    public void activate() {
        this.isActive = true;
    }
}
