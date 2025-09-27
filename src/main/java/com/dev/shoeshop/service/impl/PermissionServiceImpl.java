package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.permission.PermissionResponse;
import com.dev.shoeshop.dto.permission.PermissionUpdateRequest;
import com.dev.shoeshop.entity.Role;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.mapper.PermissionMapper;
import com.dev.shoeshop.repository.RoleRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionServiceImpl implements PermissionService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionMapper permissionMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> getAllUsers(Pageable pageable) {
        log.info("Getting all users with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Users> usersPage = userRepository.findAll(pageable);
        return usersPage.map(permissionMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllUsers() {
        log.info("Getting all users");
        
        List<Users> users = userRepository.findAll();
        return users.stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return permissionMapper.toResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return permissionMapper.toResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        log.info("Getting all roles");
        return roleRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        log.info("Getting role by id: {}", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Role getRoleByName(String roleName) {
        log.info("Getting role by name: {}", roleName);
        return roleRepository.findByRoleName(roleName);
    }
    
    @Override
    public PermissionResponse updateUserRole(PermissionUpdateRequest request) {
        log.info("Updating user role: userId={}, roleId={}", request.getUserId(), request.getRoleId());
        
        return updateUserRole(request.getUserId(), request.getRoleId());
    }
    
    @Override
    public PermissionResponse updateUserRole(Long userId, Long roleId) {
        log.info("Updating user role: userId={}, roleId={}", userId, roleId);
        
        // Get user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Get role
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        // Update user role
        user.setRole(role);
        Users savedUser = userRepository.save(user);
        
        log.info("Successfully updated user role: user={}, newRole={}", savedUser.getEmail(), role.getRoleName());
        return permissionMapper.toResponse(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(String roleName) {
        log.info("Counting users by role: {}", roleName);
        
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            return 0;
        }
        
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && user.getRole().getRoleId() == role.getRoleId())
                .count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAllUsers() {
        log.info("Counting all users");
        return userRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> searchUsersByName(String name, Pageable pageable) {
        log.info("Searching users by name: {}", name);
        
        // For now, get all users and filter by name
        // TODO: Implement proper search query in repository
        Page<Users> usersPage = userRepository.findAll(pageable);
        return usersPage.map(user -> {
            if (user.getFullname().toLowerCase().contains(name.toLowerCase()) ||
                user.getEmail().toLowerCase().contains(name.toLowerCase())) {
                return permissionMapper.toResponse(user);
            }
            return null;
        }).map(permissionResponse -> permissionResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> getUsersByRole(String roleName, Pageable pageable) {
        log.info("Getting users by role: {}", roleName);
        
        // Get all users first, then filter by role
        List<Users> allUsers = userRepository.findAll();
        List<PermissionResponse> filteredUsers = allUsers.stream()
                .filter(user -> user.getRole() != null && user.getRole().getRoleName().equalsIgnoreCase(roleName))
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredUsers.size());
        List<PermissionResponse> pageContent = filteredUsers.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredUsers.size());
    }
}
