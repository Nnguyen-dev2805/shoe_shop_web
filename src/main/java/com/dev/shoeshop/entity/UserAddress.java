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

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

}
