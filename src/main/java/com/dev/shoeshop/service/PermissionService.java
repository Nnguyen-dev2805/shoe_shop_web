package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.permission.PermissionResponse;
import com.dev.shoeshop.dto.permission.PermissionUpdateRequest;
import com.dev.shoeshop.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
    
    // User management
    Page<PermissionResponse> getAllUsers(Pageable pageable);
    List<PermissionResponse> getAllUsers();
    PermissionResponse getUserById(Long id);
    PermissionResponse getUserByEmail(String email);
    
    // Role management
    List<Role> getAllRoles();
    Role getRoleById(Long id);
    Role getRoleByName(String roleName);
    
    // Permission management
    PermissionResponse updateUserRole(PermissionUpdateRequest request);
    PermissionResponse updateUserRole(Long userId, Long roleId);
    
    // Statistics
    long countUsersByRole(String roleName);
    long countAllUsers();
    
    // Search and filter
    Page<PermissionResponse> searchUsersByName(String name, Pageable pageable);
    Page<PermissionResponse> getUsersByRole(String roleName, Pageable pageable);
}
