package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart", 
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_cart", 
        columnNames = "user_id"
    ))
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "user_id", nullable = false) // Khóa ngoại đến User
    private Users user; // Changed from userId to user for proper naming

    // ❌ REMOVED: totalPrice field - this is a calculated value
    // Should be computed from cartDetails, not stored in DB
    // Use getTotalPrice() method instead

    @CreationTimestamp
    @Column(name = "created_date", updatable = false, columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP) // Định dạng DateTime
    private Date createdDate;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("id DESC") // Sort by ID descending - newest items first
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<CartDetail> cartDetails = new HashSet<>(); // Changed from orderDetailSet to cartDetails

    // ========== BUSINESS LOGIC METHODS ==========

    /**
     * Calculate total price from all cart details (real-time calculation)
     * This replaces the old totalPrice field to avoid data inconsistency
     */
    @Transient
    public Double getTotalPrice() {
        if (cartDetails == null || cartDetails.isEmpty()) {
            return 0.0;
        }
        return cartDetails.stream()
                .mapToDouble(cd -> cd.getPricePerUnit() * cd.getQuantity())
                .sum();
    }

    /**
     * Get number of unique items in cart
     */
    @Transient
    public int getItemCount() {
        return cartDetails != null ? cartDetails.size() : 0;
    }

    /**
     * Get total quantity of all items
     */
    @Transient
    public int getTotalQuantity() {
        if (cartDetails == null || cartDetails.isEmpty()) {
            return 0;
        }
        return cartDetails.stream()
                .mapToInt(CartDetail::getQuantity)
                .sum();
    }

    /**
     * Check if cart is empty
     */
    @Transient
    public boolean isEmpty() {
        return cartDetails == null || cartDetails.isEmpty();
    }
}
