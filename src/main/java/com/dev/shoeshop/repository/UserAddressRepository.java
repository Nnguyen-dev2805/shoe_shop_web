package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.UserAddress;
import com.dev.shoeshop.entity.UserAddressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UserAddressId> {
    
    /**
     * Find all addresses for a user (not deleted)
     */
    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.isDelete = false")
    List<UserAddress> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find default address for user
     */
    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.isDefault = true AND ua.isDelete = false")
    Optional<UserAddress> findDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * Unset all default addresses for user
     */
    @Modifying
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.user.id = :userId")
    void unsetAllDefaultForUser(@Param("userId") Long userId);
    
    /**
     * Check if user has address
     */
    @Query("SELECT COUNT(ua) > 0 FROM UserAddress ua WHERE ua.user.id = :userId AND ua.address.id = :addressId AND ua.isDelete = false")
    boolean existsByUserIdAndAddressId(@Param("userId") Long userId, @Param("addressId") Long addressId);
}
