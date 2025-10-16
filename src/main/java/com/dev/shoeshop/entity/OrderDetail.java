package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
//    @NotNull(message = "Order cannot be null")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Order order;

    @ManyToOne
//    @NotNull(message = "Product cannot be null")
    @JoinColumn(name = "productdetail_id", nullable = false)
    private ProductDetail product;

//    @NotNull(message = "Quantity cannot be null")
//    @PositiveOrZero(message = "Total price must be greater than or equal to 0")
    private int quantity;

//    @NotNull(message = "Price cannot be null")
//    @PositiveOrZero(message = "Total price must be greater than or equal to 0")
    // don gia tung san pham
    private double price;

    @OneToOne(mappedBy = "orderDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Rating rating;
}
