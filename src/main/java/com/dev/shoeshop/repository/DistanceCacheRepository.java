package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.DistanceCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DistanceCacheRepository extends JpaRepository<DistanceCache, Long> {
    
    /**
     * Tìm cache theo tọa độ (đã round)
     */
    @Query("SELECT dc FROM DistanceCache dc WHERE " +
           "dc.originLat = :originLat AND dc.originLng = :originLng " +
           "AND dc.destLat = :destLat AND dc.destLng = :destLng")
    Optional<DistanceCache> findByCoordinates(
        @Param("originLat") Double originLat,
        @Param("originLng") Double originLng,
        @Param("destLat") Double destLat,
        @Param("destLng") Double destLng
    );
    
    /**
     * Xóa cache cũ hơn một ngày cụ thể
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM DistanceCache dc WHERE dc.cachedAt < :cutoffDate")
    int deleteByCachedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Đếm số lượng cache
     */
    long count();
}
