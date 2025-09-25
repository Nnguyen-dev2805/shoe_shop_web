package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected Long id;

//    @NotNull(message = "Title cannot be null")
    @Basic
    @Column(length = 255,name = "title", nullable = true,  columnDefinition = "nvarchar(255)")
    private String title;

//    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Basic
    @Column(name = "description", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String description;

    private Long voucher;

    @Basic
    @Column(name = "price", nullable = false)
    private double price;

    @Basic
    @Column(nullable = true)
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @EqualsAndHashCode.Exclude
    private Category category;


//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private List<ProductDetail> details = new ArrayList<>();

    @Column(name = "is_delete", nullable = false, columnDefinition = "boolean default false")
    private boolean isDelete = false;

    // Quan hệ OneToMany với Rating
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Rating> ratings = new ArrayList<>();
}
