package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.FlashSaleItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {

    // tìm tất cả items của một flash sale
    // dùng để hiển thị danh sách sản phẩm flash sale
    @Query("SELECT fsi FROM FlashSaleItem fsi " +
           "JOIN FETCH fsi.productDetail pd " +
           "JOIN FETCH pd.product p " +
           "WHERE fsi.flashSale.id = :flashSaleId " +
           "ORDER BY fsi.position ASC")
    List<FlashSaleItem> findByFlashSaleIdWithProduct(@Param("flashSaleId") Long flashSaleId);

    // Tìm flash sale item với PESSIMISTIC LOCK
    // dùng khi user mua hàng để tránh overselling
    // PESSIMISTIC_WRITE: Lock row trong database
    // Nếu User A đang mua → Row bị lock
    // User B phải chờ User A xong mới mua được
    // Đảm bảo không bán quá số lượng stock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT fsi FROM FlashSaleItem fsi WHERE fsi.id = :id")
    Optional<FlashSaleItem> findByIdWithLock(@Param("id") Long id);

    // Đếm số lượng items trong flash sale dùng cho thống kê
    @Query("SELECT COUNT(fsi) FROM FlashSaleItem fsi WHERE fsi.flashSale.id = :flashSaleId")
    Long countByFlashSaleId(@Param("flashSaleId") Long flashSaleId);
    
    // Kiểm tra ProductDetail đã có trong flash sale chưa
    // Dùng để tránh duplicate khi thêm product vào flash sale
    @Query("SELECT CASE WHEN COUNT(fsi) > 0 THEN true ELSE false END " +
           "FROM FlashSaleItem fsi " +
           "WHERE fsi.flashSale.id = :flashSaleId " +
           "AND fsi.productDetail.id = :productDetailId")
    boolean existsByFlashSaleIdAndProductDetailId(
        @Param("flashSaleId") Long flashSaleId, 
        @Param("productDetailId") Long productDetailId
    );
    
    // Tìm FlashSaleItem theo flash sale ID và product detail ID
    // Dùng để check sản phẩm trong cart có đang flash sale không
    @Query("SELECT fsi FROM FlashSaleItem fsi " +
           "WHERE fsi.flashSale.id = :flashSaleId " +
           "AND fsi.productDetail.id = :productDetailId")
    Optional<FlashSaleItem> findByFlashSaleIdAndProductDetailId(
        @Param("flashSaleId") Long flashSaleId, 
        @Param("productDetailId") Long productDetailId
    );
    
    // Xóa FlashSaleItem theo flash sale ID và product detail ID
    // Dùng trong giao diện quản lý flash sale khi xóa 1 sản phẩm cụ thể
    // Trigger after_delete_flash_sale_item_update_total sẽ tự động giảm total_items
    @Modifying
    @Query("DELETE FROM FlashSaleItem fsi " +
           "WHERE fsi.flashSale.id = :flashSaleId " +
           "AND fsi.productDetail.id = :productDetailId")
    void deleteByFlashSaleIdAndProductDetailId(
        @Param("flashSaleId") Long flashSaleId, 
        @Param("productDetailId") Long productDetailId
    );
}
