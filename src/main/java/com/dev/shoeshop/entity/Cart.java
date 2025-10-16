package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "user_id", nullable = false) // Khóa ngoại đến User
    private Users user; // Changed from userId to user for proper naming

    @Column(name = "total_price", nullable = false)
    @NotNull(message = "Total price cannot be null")
    @PositiveOrZero(message = "Total price must be greater than or equal to 0")
    private Double totalPrice;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false, columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP) // Định dạng DateTime
    private Date createdDate;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<CartDetail> cartDetails = new HashSet<>(); // Changed from orderDetailSet to cartDetails
}
