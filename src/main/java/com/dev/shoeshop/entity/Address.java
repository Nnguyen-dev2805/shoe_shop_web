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

    @Column(name = "address_line")
    private String address_line;

    @Column(name = "district")
    private String district;

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    private String postal_code;

    @Column(name = "country")
    private String country;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<UserAddress> userAddresses = new ArrayList<>();


    @OneToMany(mappedBy = "address",cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Order>Orders;
}
