package com.dev.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wishlist",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_product",
                        columnNames = {"user_id", "product_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_product_id", columnList = "product_id"),
                @Index(name = "idx_user_active", columnList = "user_id, is_active"),
                @Index(name = "idx_created_at", columnList = "created_at")
        }
)
@Builder
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wishlist_user"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wishlist_product"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Product product;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== Business Logic Methods ====================

    /**
     * Kích hoạt lại wishlist (soft delete recovery)
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Vô hiệu hóa wishlist (soft delete)
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Kiểm tra wishlist có đang active không
     */
    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }

    /**
     * Lấy thông tin user ID
     */
    public Long getUserId() {
        return this.user != null ? this.user.getId() : null;
    }

    /**
     * Lấy thông tin product ID
     */
    public Long getProductId() {
        return this.product != null ? this.product.getId() : null;
    }

    /**
     * Lấy tên sản phẩm
     */
    public String getProductTitle() {
        return this.product != null ? this.product.getTitle() : null;
    }

    /**
     * Lấy giá sản phẩm
     */
    public Double getProductPrice() {
        return this.product != null ? this.product.getPrice() : null;
    }

    /**
     * Kiểm tra sản phẩm có còn hàng không
     */
    public boolean isProductAvailable() {
        return this.product != null && !this.product.isDelete();
    }

    // ==================== Static Factory Methods ====================

    /**
     * Tạo wishlist mới từ user và product
     */
    public static WishList createWishlist(Users user, Product product) {
        return WishList.builder()
                .user(user)
                .product(product)
                .isActive(true)
                .build();
    }

    /**
     * Tạo wishlist không active (cho soft delete từ đầu)
     */
    public static WishList createInactiveWishlist(Users user, Product product) {
        return WishList.builder()
                .user(user)
                .product(product)
                .isActive(false)
                .build();
    }
}
