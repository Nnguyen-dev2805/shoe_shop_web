package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "flash_sale_item")
@Builder
public class FlashSaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Flash sale cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id", nullable = false)
    private FlashSale flashSale;

    @NotNull(message = "Product detail cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id", nullable = false)
    private ProductDetail productDetail; // Link đến ProductDetail (có size cụ thể)

    @NotNull(message = "Original price cannot be null")
    @Column(name = "original_price", nullable = false)
    private Double originalPrice; // Giá gốc

    @NotNull(message = "Flash sale price cannot be null")
    @Column(name = "flash_sale_price", nullable = false)
    private Double flashSalePrice; // Giá flash sale

    @Column(name = "discount_percent")
    private Double discountPercent; // % giảm giá (tự động tính)

    @Column(name = "position")
    private Integer position; // Vị trí hiển thị trong flash sale
    
    // ========== BUSINESS LOGIC METHODS ==========
    
    @PrePersist
    @PreUpdate
    public void calculateDiscountPercent() {
        if (originalPrice != null && flashSalePrice != null && originalPrice > 0) {
            this.discountPercent = ((originalPrice - flashSalePrice) / originalPrice) * 100;
        }
    }
    
    /**
     * Tính số tiền tiết kiệm được cho 1 sản phẩm
     */
    public Double calculateSavings(int quantity) {
        if (originalPrice == null || flashSalePrice == null) return 0.0;
        return (originalPrice - flashSalePrice) * quantity;
    }
}
