package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;



public interface OrderRepository extends JpaRepository<Order,Long> {

    Long countByStatus(ShipmentStatus status);

    public List<Order> findByStatus(ShipmentStatus status);
    
    /**
     * Tìm orders theo status, sắp xếp theo ngày tạo mới nhất (cho admin)
     */
    public List<Order> findByStatusOrderByCreatedDateDesc(ShipmentStatus status);
    
    public Page<Order> findByStatus(ShipmentStatus status, Pageable pageable);

    public Order findOrderById(Long id);
    
    /**
     * Lấy tất cả orders, sắp xếp theo ngày tạo mới nhất (cho admin)
     */
    public List<Order> findAllByOrderByCreatedDateDesc();
    
    // Lấy danh sách orders của user theo userId, sắp xếp theo ngày tạo mới nhất
    public List<Order> findByUserIdOrderByCreatedDateDesc(Long userId);
    
    // Lấy danh sách orders của user theo userId và status
    public List<Order> findByUserIdAndStatusOrderByCreatedDateDesc(Long userId, ShipmentStatus status);
    
    // ========== DASHBOARD STATISTICS QUERIES ==========
    
    /**
     * Đếm tổng số đơn hàng (loại trừ CANCEL và RETURN) với date range
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status NOT IN (com.dev.shoeshop.enums.ShipmentStatus.CANCEL, com.dev.shoeshop.enums.ShipmentStatus.RETURN) " +
           "AND (:startDate IS NULL OR o.createdDate >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdDate <= :endDate)")
    Long countTotalOrders(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * Tính tổng doanh thu từ các đơn hàng DELIVERED với date range
     */
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0.0) FROM Order o WHERE o.status = com.dev.shoeshop.enums.ShipmentStatus.DELIVERED " +
           "AND (:startDate IS NULL OR o.createdDate >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdDate <= :endDate)")
    Double calculateTotalRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * Đếm số lượng sản phẩm đã bán (từ các đơn DELIVERED) với date range
     */
    @Query("SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od WHERE od.order.status = com.dev.shoeshop.enums.ShipmentStatus.DELIVERED " +
           "AND (:startDate IS NULL OR od.order.createdDate >= :startDate) " +
           "AND (:endDate IS NULL OR od.order.createdDate <= :endDate)")
    Long countTotalProductsSold(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * Đếm số khách hàng đã đặt hàng với date range
     */
    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o " +
           "WHERE (:startDate IS NULL OR o.createdDate >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdDate <= :endDate)")
    Long countTotalCustomers(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * Đếm số đơn hàng theo từng trạng thái với date range
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o " +
           "WHERE (:startDate IS NULL OR o.createdDate >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdDate <= :endDate) " +
           "GROUP BY o.status")
    List<Object[]> countOrdersByStatus(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * Thống kê đơn hàng theo ngày với date range
     */
    @Query("SELECT DATE(o.createdDate), COUNT(o) " +
           "FROM Order o " +
           "WHERE (:startDate IS NULL OR o.createdDate >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdDate <= :endDate) " +
           "GROUP BY DATE(o.createdDate) " +
           "ORDER BY DATE(o.createdDate) DESC")
    List<Object[]> getOrderTimeSeries(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * Thống kê doanh thu theo ngày với date range - chỉ tính đơn DELIVERED
     */
    @Query("SELECT DATE(o.createdDate), COALESCE(SUM(o.totalPrice), 0.0) " +
           "FROM Order o " +
           "WHERE o.status = com.dev.shoeshop.enums.ShipmentStatus.DELIVERED " +
           "AND (:startDate IS NULL OR o.createdDate >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdDate <= :endDate) " +
           "GROUP BY DATE(o.createdDate) " +
           "ORDER BY DATE(o.createdDate) DESC")
    List<Object[]> getRevenueTimeSeries(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * Lấy top khách hàng mua nhiều nhất (theo tổng tiền chi)
     * Trả về: User ID, Full Name, Email, Phone, Total Orders, Total Spent
     */
    @Query(value = "SELECT u.id, u.full_name, u.email, u.phone, " +
           "COUNT(o.id) as total_orders, " +
           "COALESCE(SUM(o.total_price), 0.0) as total_spent " +
           "FROM orders o " +
           "INNER JOIN users u ON o.customer_id = u.id " +
           "WHERE o.status = 'DELIVERED' " +
           "AND (:startDate IS NULL OR o.created_date >= :startDate) " +
           "AND (:endDate IS NULL OR o.created_date <= :endDate) " +
           "GROUP BY u.id, u.full_name, u.email, u.phone " +
           "ORDER BY total_spent DESC", 
           nativeQuery = true)
    List<Object[]> findTopCustomers(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

}
