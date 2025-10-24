package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * InventoryHistory - Lịch sử nhập hàng
 * Mỗi lần nhập hàng sẽ tạo 1 record mới
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventory_history")
public class InventoryHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * ProductDetail reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id", nullable = false)
    private ProductDetail productDetail;
    
    /**
     * Ngày nhập hàng
     */
    @Column(name = "import_date", nullable = false)
    private LocalDateTime importDate;
    
    /**
     * Số lượng nhập
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    /**
     * ⭐ Giá nhập cho 1 đơn vị (1 đôi giày)
     * Dùng để tính giá vốn trung bình và lợi nhuận
     */
    @Column(name = "cost_price")
    private Double costPrice;
    
    /**
     * Ghi chú (optional)
     */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    /**
     * Helper: Tính tổng giá trị nhập (giá nhập × số lượng)
     */
    public Double getTotalCost() {
        if (costPrice != null && quantity != null) {
            return costPrice * quantity;
        }
        return 0.0;
    }
}
