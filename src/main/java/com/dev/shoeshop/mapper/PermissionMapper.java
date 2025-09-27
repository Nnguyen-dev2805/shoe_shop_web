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
        
        return PermissionResponse.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .address(user.getAddress())
                .phone(user.getPhone())
                .roleId(user.getRole() != null ? user.getRole().getRoleId() : null)
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .build();
    }
    
    public PermissionUpdateRequest toUpdateRequest(Integer userId, Integer roleId) {
        return new PermissionUpdateRequest(userId, roleId);
    }
}
