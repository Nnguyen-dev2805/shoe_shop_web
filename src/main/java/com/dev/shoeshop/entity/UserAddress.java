package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_address")
@Builder
public class UserAddress {
    @EmbeddedId
    private UserAddressId id = new UserAddressId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @MapsId("addressId")
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    
    // ⭐ Thông tin người nhận (có thể khác với user)
    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;
    
    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;
    
    // ⭐ Label do user đặt
    @Column(name = "label", length = 50)
    private String label; // VD: "Nhà riêng", "Công ty", "Nhà bố mẹ"

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

}
