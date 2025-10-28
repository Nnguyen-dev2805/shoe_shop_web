package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.CoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, Long> {
    
    /**
     * Tính tổng xu đã chi tiêu (SPENT)
     */
    @Query("SELECT COALESCE(SUM(ABS(ct.amount)), 0) FROM CoinTransaction ct WHERE ct.user.id = :userId AND ct.transactionType IN ('SPENT')")
    Long getTotalSpentByUserId(@Param("userId") Long userId);
    
    /**
     * Tính tổng xu đã nhận (EARNED, REFUND, PROMOTION)
     */
    @Query("SELECT COALESCE(SUM(ct.amount), 0) FROM CoinTransaction ct WHERE ct.user.id = :userId AND ct.transactionType IN ('EARNED', 'REFUND', 'PROMOTION')")
    Long getTotalEarnedByUserId(@Param("userId") Long userId);
    
    /**
     * Lấy lịch sử giao dịch của user, sắp xếp theo ngày mới nhất (hỗ trợ phân trang)
     */
    @Query("SELECT ct FROM CoinTransaction ct WHERE ct.user.id = :userId ORDER BY ct.createdDate DESC")
    List<CoinTransaction> findByUserIdOrderByCreatedDateDesc(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
}
