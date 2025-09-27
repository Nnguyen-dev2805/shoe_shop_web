package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    List<OrderDetail> findOrderDetailsByOrderId(Long orderId);
}
