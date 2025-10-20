package com.dev.shoeshop.entity;

import com.dev.shoeshop.enums.FlashSaleStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flash_sale")
@Builder
public class FlashSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Flash sale name cannot be blank")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start time cannot be null")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FlashSaleStatus status = FlashSaleStatus.SCHEDULED;

    @Column(name = "total_items")
    private Integer totalItems = 0; // Tổng số sản phẩm trong flash sale

    @Column(name = "total_sold")
    private Integer totalSold = 0; // Tổng số đã bán

    @Column(name = "banner_image")
    private String bannerImage; // Banner cho flash sale

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    // @Column(name = "created_by")
    // private Long createdBy;

    @Column(name = "is_delete", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDelete = false;

    @OneToMany(mappedBy = "flashSale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FlashSaleItem> items = new ArrayList<>();
    
    // ========== BUSINESS LOGIC METHODS ==========
    
    @PrePersist
    public void prePersist() {
        if (this.createdDate == null) {
            this.createdDate = LocalDateTime.now();
        }
        if (this.isDelete == null) {
            this.isDelete = false;
        }
        if (this.status == null) {
            this.status = FlashSaleStatus.SCHEDULED;
        }
        if (this.totalItems == null) {
            this.totalItems = 0;
        }
        if (this.totalSold == null) {
            this.totalSold = 0;
        }
    }
    
    /**
     * Kiểm tra flash sale có đang active không
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return FlashSaleStatus.ACTIVE.equals(status) 
                && !now.isBefore(startTime) 
                && !now.isAfter(endTime);
    }
    
    /**
     * Kiểm tra flash sale có sắp bắt đầu không
     */
    public boolean isUpcoming() {
        LocalDateTime now = LocalDateTime.now();
        return FlashSaleStatus.SCHEDULED.equals(status) && now.isBefore(startTime);
    }
    
    /**
     * Kiểm tra flash sale đã kết thúc chưa
     */
    public boolean isEnded() {
        LocalDateTime now = LocalDateTime.now();
        return FlashSaleStatus.ENDED.equals(status) || now.isAfter(endTime);
    }
    
    /**
     * Tính % đã bán
     */
    public double getSoldPercentage() {
        if (totalItems == null || totalItems == 0) return 0;
        if (totalSold == null) return 0;
        return (totalSold.doubleValue() / totalItems.doubleValue()) * 100;
    }
    
    /**
     * Tăng số lượng đã bán
     */
    public void incrementSold(int quantity) {
        if (this.totalSold == null) {
            this.totalSold = 0;
        }
        this.totalSold += quantity;
    }
    
    /**
     * Giảm số lượng đã bán (khi order bị CANCEL hoặc RETURN)
     */
    public void decrementSold(int quantity) {
        if (this.totalSold == null) {
            this.totalSold = 0;
        }
        this.totalSold = Math.max(0, this.totalSold - quantity);
    }
    
    /**
     * Tự động update status dựa trên thời gian hiện tại
     */
    public void updateStatusByTime() {
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isBefore(startTime)) {
            this.status = FlashSaleStatus.SCHEDULED;
        } else if (now.isAfter(endTime)) {
            this.status = FlashSaleStatus.ENDED;
        } else {
            this.status = FlashSaleStatus.ACTIVE;
        }
    }
    
    /**
     * Kiểm tra có thể mua flash sale không
     */
    public boolean canPurchase() {
        return isActive() && !isDelete && totalSold < totalItems;
    }
}
