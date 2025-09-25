package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@Entity
@Table(name = "Category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "description", length = 255, columnDefinition = "nvarchar(255)")
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Product> products;
}
