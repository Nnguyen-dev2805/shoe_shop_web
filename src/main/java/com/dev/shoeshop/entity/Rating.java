package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "rating")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Star rating cannot be less than 0")
    @Max(value = 5, message = "Star rating cannot be greater than 5")
    private int star = 0;

    @Column(nullable = false)
    private String comment;

    private String image;

    @CreationTimestamp
    @Column(name = "created_dte", updatable = false)
    Date createdDate;

    @Column(name = "modified_dte")
    @UpdateTimestamp
    Date modified;

    // Quan hệ ManyToOne với Product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // Khóa ngoại đến Product
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Product product;

    // Quan hệ ManyToOne với Users
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Khóa ngoại đến Users
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Users user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", unique = true, nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private OrderDetail orderDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ProductDetail productDetail;
}
