package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Entity
public class CartDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @NotNull(message = "Cart cannot be null")
    private Cart cart;

    @ManyToOne
    @NotNull(message = "Product cannot be null")
    @JoinColumn(name = "productdetail_id", nullable = false)
    private ProductDetail product;

    @NotNull(message = "Quantity cannot be null")
    @PositiveOrZero(message = "Total price must be greater than or equal to 0")
    private int quantity;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Total price must be greater than or equal to 0")
    private double price; // (gia sau khi chon size + gia goc)
}
