package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer> {
    List<OrderDetail> findOrderDetailsByOrderId(Long orderId);
    
    /**
     * Lấy top sản phẩm bán chạy nhất (theo số lượng đã bán từ các đơn DELIVERED) với date range
     * Trả về: Product ID, Product Title, Product Image, Tổng số lượng bán, Tổng doanh thu
     */
    @Query("SELECT od.product.product.id, od.product.product.title, od.product.product.image, " +
           "SUM(od.quantity), SUM(od.quantity * od.price) " +
           "FROM OrderDetail od " +
           "WHERE od.order.status = com.dev.shoeshop.enums.ShipmentStatus.DELIVERED " +
           "AND (:startDate IS NULL OR od.order.createdDate >= :startDate) " +
           "AND (:endDate IS NULL OR od.order.createdDate <= :endDate) " +
           "GROUP BY od.product.product.id, od.product.product.title, od.product.product.image " +
           "ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findTopSellingProducts(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
    
    /**
     * Lấy sản phẩm theo doanh thu (giảm dần)
     * Trả về: Product ID, Product Title, Product Image, Tổng số lượng bán, Tổng doanh thu
     */
    @Query("SELECT od.product.product.id, od.product.product.title, od.product.product.image, " +
           "SUM(od.quantity), SUM(od.quantity * od.price) " +
           "FROM OrderDetail od " +
           "WHERE od.order.status = com.dev.shoeshop.enums.ShipmentStatus.DELIVERED " +
           "AND (:startDate IS NULL OR od.order.createdDate >= :startDate) " +
           "AND (:endDate IS NULL OR od.order.createdDate <= :endDate) " +
           "GROUP BY od.product.product.id, od.product.product.title, od.product.product.image " +
           "ORDER BY SUM(od.quantity * od.price) DESC")
    List<Object[]> findProductsByRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
}
