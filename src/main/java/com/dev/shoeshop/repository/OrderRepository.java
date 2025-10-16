package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
