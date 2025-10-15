package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.AddressDTO;
import com.dev.shoeshop.dto.AddressRequestDTO;
import com.dev.shoeshop.entity.Address;
import com.dev.shoeshop.entity.UserAddress;
import com.dev.shoeshop.entity.UserAddressId;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.AddressRepository;
import com.dev.shoeshop.repository.UserAddressRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {
    
    private final AddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public AddressDTO addAddress(Long userId, AddressRequestDTO addressRequest) {
        log.info("Adding address for user {}", userId);
        
        // Find user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create Address entity - PHIÊN BẢN ĐƠN GIẢN (không có ward/district)
        Address address = Address.builder()
                .address_line(addressRequest.getStreet())  // Bao gồm: số nhà, đường, phường, quận
                .city(addressRequest.getCity())            // Tỉnh/Thành phố
//                .postal_code(addressRequest.getPostalCode())
                .country(addressRequest.getCountry() != null ? addressRequest.getCountry() : "Việt Nam")
                .latitude(addressRequest.getLatitude())    // ⭐ GPS cho shipper & tính phí
                .longitude(addressRequest.getLongitude())
                .addressType(addressRequest.getAddressType() != null ? addressRequest.getAddressType() : "HOME")
                .note(null)  // Không dùng
                .build();
        
        // Save address
        address = addressRepository.save(address);
        
        // If this is set as default, unset all other defaults first
        if (Boolean.TRUE.equals(addressRequest.getIsDefault())) {
            userAddressRepository.unsetAllDefaultForUser(userId);
        }
        
        // Create UserAddress relationship
        UserAddressId userAddressId = new UserAddressId(userId, address.getId());
        UserAddress userAddress = UserAddress.builder()
                .id(userAddressId)
                .user(user)
                .address(address)
                .recipientName(addressRequest.getRecipientName())
                .recipientPhone(addressRequest.getRecipientPhone())
                .label(null)  // Không dùng
                .isDefault(addressRequest.getIsDefault() != null ? addressRequest.getIsDefault() : false)
                .isDelete(false)
                .build();
        
        userAddressRepository.save(userAddress);
        
        log.info("Address saved successfully with ID: {}", address.getId());
        
        // Return DTO
        return convertToDTO(address, userAddress);
    }
    
    @Override
    public List<AddressDTO> getUserAddresses(Long userId) {
        log.info("Getting addresses for user {}", userId);
        
        List<UserAddress> userAddresses = userAddressRepository.findByUserId(userId);
        
        return userAddresses.stream()
                .map(ua -> convertToDTO(ua.getAddress(), ua))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        log.info("Setting default address {} for user {}", addressId, userId);
        
        // Check if address belongs to user
        if (!addressRepository.existsByIdAndUserId(addressId, userId)) {
            throw new RuntimeException("Address not found or does not belong to user");
        }
        
        // Unset all defaults
        userAddressRepository.unsetAllDefaultForUser(userId);
        
        // Set new default
        UserAddressId id = new UserAddressId(userId, addressId);
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAddress not found"));
        
        userAddress.setIsDefault(true);
        userAddressRepository.save(userAddress);
        
        log.info("Default address set successfully");
    }
    
    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        log.info("Deleting address {} for user {}", addressId, userId);
        
        UserAddressId id = new UserAddressId(userId, addressId);
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Soft delete
        userAddress.setIsDelete(true);
        userAddress.setIsDefault(false);
        userAddressRepository.save(userAddress);
        
        log.info("Address deleted successfully");
    }
    
    @Override
    public AddressDTO getDefaultAddress(Long userId) {
        log.info("Getting default address for user {}", userId);
        
        return userAddressRepository.findDefaultByUserId(userId)
                .map(ua -> convertToDTO(ua.getAddress(), ua))
                .orElse(null);
    }
    
    /**
     * Convert Address + UserAddress to AddressDTO
     */
    private AddressDTO convertToDTO(Address address, UserAddress userAddress) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setAddressLine(address.getAddress_line());
        dto.setStreet(address.getAddress_line()); // Alias
        dto.setCity(address.getCity());
//        dto.setPostalCode(address.getPostal_code());
        dto.setCountry(address.getCountry());
        dto.setIsDefault(userAddress.getIsDefault());
        
        // Map recipient information from UserAddress
        dto.setRecipientName(userAddress.getRecipientName());
        dto.setRecipientPhone(userAddress.getRecipientPhone());
        
        // Build full address string
        StringBuilder fullAddress = new StringBuilder();
        if (address.getAddress_line() != null) {
            fullAddress.append(address.getAddress_line());
        }
        if (address.getCity() != null) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(address.getCity());
        }
        if (address.getCountry() != null) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(address.getCountry());
        }
        
        dto.setAddress(fullAddress.toString());
        
        return dto;
    }
}
