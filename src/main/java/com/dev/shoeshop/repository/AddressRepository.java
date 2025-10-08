package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * Find addresses by user ID through UserAddress relationship
     */
    @Query("SELECT a FROM Address a JOIN UserAddress ua ON a.id = ua.address.id " +
           "WHERE ua.user.id = :userId AND ua.isDelete = false " +
           "ORDER BY ua.isDefault DESC, a.id ASC")
    List<Address> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find default address for user
     */
    @Query("SELECT a FROM Address a JOIN UserAddress ua ON a.id = ua.address.id " +
           "WHERE ua.user.id = :userId AND ua.isDefault = true AND ua.isDelete = false")
    Address findDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * Check if address belongs to user
     */
    @Query("SELECT COUNT(ua) > 0 FROM UserAddress ua " +
           "WHERE ua.address.id = :addressId AND ua.user.id = :userId AND ua.isDelete = false")
    boolean existsByIdAndUserId(@Param("addressId") Long addressId, @Param("userId") Long userId);
}
