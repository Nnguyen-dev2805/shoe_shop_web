package com.dev.shoeshop.mapper;

import com.dev.shoeshop.dto.permission.PermissionResponse;
import com.dev.shoeshop.dto.permission.PermissionUpdateRequest;
import com.dev.shoeshop.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {
    
    public PermissionResponse toResponse(Users user) {
        if (user == null) {
            return null;
        }
        
        // Get default address or first address
        String address = "";
        if (user.getUserAddresses() != null && !user.getUserAddresses().isEmpty()) {
            var defaultAddr = user.getUserAddresses().stream()
                .filter(ua -> ua.getIsDefault() != null && ua.getIsDefault())
                .findFirst()
                .orElse(user.getUserAddresses().get(0));
            
            if (defaultAddr != null && defaultAddr.getAddress() != null) {
                var addr = defaultAddr.getAddress();
                address = addr.getAddress_line() + ", " + addr.getCity();
            }
        }
        
        return PermissionResponse.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .address(address)
                .phone(user.getPhone())
                .roleId(user.getRole() != null ? user.getRole().getRoleId() : null)
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .build();
    }
    
    public PermissionUpdateRequest toUpdateRequest(Long userId, Long roleId) {
        return new PermissionUpdateRequest(userId, roleId);
    }
}
