package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipper")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipper {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @NotNull(message = "User không được để trống")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;
    
    @NotNull(message = "Shipping company không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_company_id", nullable = false)
    private ShippingCompany shippingCompany;
    
    // Constructors
    public Shipper(Users user, ShippingCompany shippingCompany) {
        this.user = user;
        this.shippingCompany = shippingCompany;
    }
    
    // Business logic methods
    
    /**
     * Kiểm tra shipper có đang hoạt động không
     */
    public boolean isWorking() {
        return shippingCompany != null && 
               shippingCompany.getIsActive() != null && 
               shippingCompany.getIsActive();
    }
    
    /**
     * Lấy thông tin liên hệ đầy đủ
     */
    public String getContactInfo() {
        if (user == null) return "Không có thông tin user";
        
        StringBuilder contact = new StringBuilder();
        contact.append("Tên: ").append(user.getFullname());
        if (user.getPhone() != null) {
            contact.append(", SĐT: ").append(user.getPhone());
        }
        if (user.getEmail() != null) {
            contact.append(", Email: ").append(user.getEmail());
        }
        return contact.toString();
    }
    
    /**
     * Lấy tên shipper
     */
    public String getShipperName() {
        return user != null ? user.getFullname() : "Unknown";
    }
    
    /**
     * Lấy số điện thoại shipper
     */
    public String getShipperPhone() {
        return user != null ? user.getPhone() : null;
    }
    
    /**
     * Lấy email shipper
     */
    public String getShipperEmail() {
        return user != null ? user.getEmail() : null;
    }
    
    // Static factory methods
    
    /**
     * Tạo shipper từ User
     */
    public static Shipper createFromUser(Users user, ShippingCompany shippingCompany) {
        return new Shipper(user, shippingCompany);
    }
}
