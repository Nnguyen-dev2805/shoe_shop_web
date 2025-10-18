package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_detail",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cart_product",
        columnNames = {"cart_id", "productdetail_id"}
    ))
public class CartDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Changed from int to Long for consistency with other entities

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @NotNull(message = "Cart cannot be null")
    private Cart cart;

    @ManyToOne
    @NotNull(message = "Product cannot be null")
    @JoinColumn(name = "productdetail_id", nullable = false)
    private ProductDetail product;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Column(name = "price_per_unit", nullable = false)
    @NotNull(message = "Price per unit cannot be null")
    @Positive(message = "Price per unit must be positive")
    private double pricePerUnit; // Price for ONE unit (product base price + size price)

    // ========== BUSINESS LOGIC METHODS ==========

    /**
     * Calculate total price for this cart item (pricePerUnit × quantity)
     */
    @Transient
    public double getTotalPrice() {
        return pricePerUnit * quantity;
    }

    /**
     * Get formatted total price
     */
    @Transient
    public String getFormattedTotalPrice() {
        return String.format("%,.0f VNĐ", getTotalPrice());
    }

    /**
     * Get formatted unit price
     */
    @Transient
    public String getFormattedUnitPrice() {
        return String.format("%,.0f VNĐ", pricePerUnit);
    }
}
