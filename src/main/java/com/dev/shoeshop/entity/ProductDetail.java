package com.dev.shoeshop.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Set;

@Data
@Entity
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull(message = "Product cannot be null")
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

//    @NotNull(message = "Size cannot be null")
    private int size;

//    @Min(value = 0, message = "Price add must be greater than or equal to 0")
    @Column(name = "price_add")
    private double priceadd;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OrderDetail> orderDetailSet;

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<CartDetail> cartDetailSet;
//
//    @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL)
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<Inventory> inventories;
}
