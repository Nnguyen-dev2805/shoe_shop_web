package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.AddressDTO;
import com.dev.shoeshop.dto.AddressRequestDTO;

import java.util.List;

public interface AddressService {
    
    /**
     * Add new address for user
     */
    AddressDTO addAddress(Long userId, AddressRequestDTO addressRequest);
    
    /**
     * Update existing address
     */
    AddressDTO updateAddress(Long userId, Long addressId, AddressRequestDTO addressRequest);
    
    /**
     * Get all addresses for user
     */
    List<AddressDTO> getUserAddresses(Long userId);
    
    /**
     * Set default address
     */
    void setDefaultAddress(Long userId, Long addressId);
    
    /**
     * Delete address (soft delete)
     */
    void deleteAddress(Long userId, Long addressId);
    
    /**
     * Get default address for user
     */
    AddressDTO getDefaultAddress(Long userId);
}
