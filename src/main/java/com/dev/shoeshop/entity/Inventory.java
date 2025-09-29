package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_detail_id", nullable = false)
    private ProductDetail productDetail;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
