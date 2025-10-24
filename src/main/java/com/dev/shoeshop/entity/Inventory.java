package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Inventory - Tồn kho hiện tại
 * Chỉ lưu số lượng tồn kho hiện tại của mỗi ProductDetail
 * Lịch sử nhập/xuất được lưu trong InventoryHistory
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = "product_detail_id")
})
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ProductDetail reference - UNIQUE
     * Mỗi ProductDetail chỉ có 1 record Inventory
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id", nullable = false, unique = true)
    private ProductDetail productDetail;

    /**
     * Số lượng tồn kho còn lại (hiện tại)
     * Giảm dần khi bán hàng
     */
    @Column(name = "remaining_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer remainingQuantity = 0;
    
    /**
     * Tổng số lượng từ lúc nhập (không thay đổi)
     * Dùng để tracking tổng hàng đã nhập
     */
    @Column(name = "total_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalQuantity = 0;
    
    // ===== HELPER METHODS =====
    
    /**
     * Check if out of stock
     */
    @Transient
    public boolean isOutOfStock() {
        return remainingQuantity == null || remainingQuantity <= 0;
    }
    
    /**
     * Get sold quantity
     */
    @Transient
    public Integer getSoldQuantity() {
        if (totalQuantity == null || remainingQuantity == null) return 0;
        return totalQuantity - remainingQuantity;
    }
    
    /**
     * Get sold percentage
     */
    @Transient
    public Double getSoldPercentage() {
        if (totalQuantity == null || totalQuantity == 0) return 0.0;
        return (getSoldQuantity() * 100.0) / totalQuantity;
    }
    
    /**
     * Add stock (khi nhập hàng)
     */
    public void addStock(Integer quantity) {
        if (quantity == null || quantity <= 0) return;
        
        if (this.remainingQuantity == null) this.remainingQuantity = 0;
        if (this.totalQuantity == null) this.totalQuantity = 0;
        
        this.remainingQuantity += quantity;
        this.totalQuantity += quantity;
    }
    
    /**
     * Remove stock (khi bán hàng)
     */
    public void removeStock(Integer quantity) {
        if (quantity == null || quantity <= 0) return;
        if (this.remainingQuantity == null) this.remainingQuantity = 0;
        
        this.remainingQuantity = Math.max(0, this.remainingQuantity - quantity);
    }
}
