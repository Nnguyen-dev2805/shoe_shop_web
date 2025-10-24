package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_detail_id", nullable = false)
    private ProductDetail productDetail;

    @Column(name = "quantity")
    private int quantity; // Số lượng còn lại
    
    // ✅ Tracking số lượng đã bán từ lô này
    @Column(name = "sold_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer soldQuantity = 0;
    
    // ✅ Số lượng nhập ban đầu
    @Column(name = "initial_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer initialQuantity = 0;
    
    // ✅ Giá nhập của lô hàng này
    @Column(name = "cost_price", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double costPrice = 0.0;
    
    // ✅ Ngày nhập hàng
    @Column(name = "import_date")
    private LocalDateTime importDate;
    
    // ✅ Ghi chú (optional)
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    // ===== HELPER METHODS =====
    
    /**
     * Tính % đã bán
     */
    @Transient
    public double getSoldPercentage() {
        if (initialQuantity == null || initialQuantity == 0) return 0;
        return (soldQuantity * 100.0) / initialQuantity;
    }
    
    /**
     * Tính doanh thu từ lô này
     */
    @Transient
    public double getRevenueFromBatch(double sellingPrice) {
        return soldQuantity * sellingPrice;
    }
    
    /**
     * Tính lợi nhuận từ lô này
     */
    @Transient
    public double getProfitFromBatch(double sellingPrice) {
        return soldQuantity * (sellingPrice - costPrice);
    }
}
