package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@Entity
@Table(name="product_detail")
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product cannot be null")
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Size cannot be null")
    @Column(name = "size", nullable = false)
//    private int size;
    private Integer size;

    @Min(value = 0, message = "Price add must be greater than or equal to 0")
    @Column(name = "price_add")
    private double priceadd;
    
//    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
//    @Column(name = "quantity", nullable = false)
//    private int quantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OrderDetail> orderDetailSet;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<CartDetail> cartDetailSet;
  
    @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Inventory> inventories;

    @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Rating> ratings;
  
    // ========== THÊM MỚI: Flash Sale Relationship ==========
    @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<FlashSaleItem> flashSaleItems;
    
    // Business logic methods
    
    /**
     * Tính giá cuối cùng (bao gồm priceadd)
     */
    public double getFinalPrice() {
        if (product == null) return priceadd;
        return product.getPrice() + priceadd;
    }
    
    /**
     * Kiểm tra product detail có đang trong flash sale không
     */
    public boolean isInFlashSale() {
        if (flashSaleItems == null || flashSaleItems.isEmpty()) return false;
        
        return flashSaleItems.stream()
                .anyMatch(item -> item.getFlashSale() != null 
                        && item.getFlashSale().isActive() 
                        && item.hasStock());
    }
    
    /**
     * Lấy flash sale item đang active (nếu có)
     */
    public FlashSaleItem getActiveFlashSaleItem() {
        if (flashSaleItems == null || flashSaleItems.isEmpty()) return null;
        
        return flashSaleItems.stream()
                .filter(item -> item.getFlashSale() != null 
                        && item.getFlashSale().isActive() 
                        && item.hasStock())
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Lấy giá flash sale (nếu có)
     */
    public Double getFlashSalePrice() {
        FlashSaleItem activeItem = getActiveFlashSaleItem();
        return activeItem != null ? activeItem.getFlashSalePrice() : null;
    }
}
