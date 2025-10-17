package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected Long id;

    @NotNull(message = "Title cannot be null")
    @Basic
    @Column(length = 255,name = "title", nullable = false,  columnDefinition = "nvarchar(255)")
    private String title;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Basic
    @Column(name = "description", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String description;

    // chưa đụng vào
    private Long voucher;

    @Basic
    @Column(name = "price", nullable = false)
    private double price;

    @Basic
    @Column(nullable = true)
    private String image;

    @Column(name = "total_reviews", nullable = false, columnDefinition = "bigint default 0")
    private Long total_reviews = 0L;

    @Column(name = "total_reviewers", nullable = false, columnDefinition = "bigint default 0")
    private Long total_reviewers = 0L;

    @Column(name = "total_stars", nullable = false, columnDefinition = "bigint default 0")
    private Long total_stars = 0L;

    @Column(name = "average_rating", nullable = false, columnDefinition = "float default 0.0")
    private Double average_rating = 0.0;

    @Column(name = "average_stars", nullable = false, columnDefinition = "float default 0.0")
    private Double average_stars = 0.0;

    @Column(name = "sold_quantity", nullable = false, columnDefinition = "bigint default 0")
    private Long soldQuantity = 0L;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @EqualsAndHashCode.Exclude
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ProductDetail> details = new ArrayList<>();

    @Column(name = "is_delete", nullable = false, columnDefinition = "boolean default false")
    private boolean isDelete = false;

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Rating> ratings = new ArrayList<>();
}
