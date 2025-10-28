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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionServiceImpl implements PermissionService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionMapper permissionMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * ⚡ CACHED: Get all users with pagination
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "permissions", 
               key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize",
               unless = "#result == null")
    public Page<PermissionResponse> getAllUsers(Pageable pageable) {
        log.info("📦 Loading all users with pagination (page: {}, size: {})", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Users> usersPage = userRepository.findAll(pageable);
        return usersPage.map(permissionMapper::toResponse);
    }
    
    /**
     * ⚡ CACHED: Get all users (non-paginated)
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "permissions", key = "'all'")
    public List<PermissionResponse> getAllUsers() {
        log.info("📦 Loading all users from database");
        
        List<Users> users = userRepository.findAll();
        return users.stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * ⚡ CACHED: Get user by ID
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "permissions", key = "'user:' + #id")
    public PermissionResponse getUserById(Long id) {
        log.info("📦 Loading user {} from database", id);
        
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
    
    /**
     * ⚡ CACHED: Get all roles
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "permissions", key = "'roles:all'")
    public List<Role> getAllRoles() {
        log.info("📦 Loading all roles from database");
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
        return roleRepository.findByRoleName(roleName).orElse(null);
    }
    
    /**
     * 🗑️ CACHE EVICT: Clear permissions cache when updating user role
     */
    @Override
    @CacheEvict(value = "permissions", allEntries = true)
    public PermissionResponse updateUserRole(PermissionUpdateRequest request) {
        log.info("✏️ Updating user role: userId={}, roleId={}, clearing cache", request.getUserId(), request.getRoleId());
        
        return updateUserRole(request.getUserId(), request.getRoleId());
    }
    
    /**
     * 🗑️ CACHE EVICT: Clear permissions cache when updating user role
     */
    @Override
    @CacheEvict(value = "permissions", allEntries = true)
    public PermissionResponse updateUserRole(Long userId, Long roleId) {
        log.info("✏️ Updating user role: userId={}, roleId={}, clearing cache", userId, roleId);
        
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
    
    /**
     * ⚡ CACHED: Count users by role
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "permissions", key = "'count:role:' + #roleName")
    public long countUsersByRole(String roleName) {
        log.info("📦 Counting users by role: {}", roleName);
        
        Role role = roleRepository.findByRoleName(roleName).orElse(null);
        if (role == null) {
            return 0;
        }
        
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && user.getRole().getRoleId().equals(role.getRoleId()))
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
        
        // Get all users first, then filter by name
        List<Users> allUsers = userRepository.findAll();
        List<PermissionResponse> filteredUsers = allUsers.stream()
                .filter(user -> user.getFullname().toLowerCase().contains(name.toLowerCase()) ||
                               user.getEmail().toLowerCase().contains(name.toLowerCase()))
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredUsers.size());
        List<PermissionResponse> pageContent = filteredUsers.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredUsers.size());
    }
    
    /**
     * ⚡ CACHED: Get users by role with pagination
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "permissions", 
               key = "'role:' + #roleName + ':page:' + #pageable.pageNumber + ':' + #pageable.pageSize",
               unless = "#result == null")
    public Page<PermissionResponse> getUsersByRole(String roleName, Pageable pageable) {
        log.info("📦 Loading users by role {} (page: {}, size: {})", roleName, pageable.getPageNumber(), pageable.getPageSize());
        
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
    
    @Override
    @Transactional(readOnly = true)
    public Users getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    /**
     * 🗑️ CACHE EVICT: Clear permissions cache when creating user
     */
    @Override
    @CacheEvict(value = "permissions", allEntries = true)
    public Users createUser(java.util.Map<String, Object> userData) {
        log.info("➕ Creating new user, clearing permissions cache");
        
        // Validate input data
        validateUserData(userData, true);
        
        String email = (String) userData.get("email");
        
        // Check if email already exists
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email đã tồn tại: " + email);
        }
        
        Users user = new Users();
        user.setFullname((String) userData.get("fullname"));
        user.setEmail(email);
        user.setPhone((String) userData.get("phone"));
        
        // Encode password
        String password = (String) userData.get("password");
        user.setPassword(passwordEncoder.encode(password));
        
        // Set default role (user)
        Role defaultRole = roleRepository.findByRoleName("user")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRole(defaultRole);
        
        // Set default values
        user.setIsActive(true);
        user.setProvider("LOCAL");
        
        return userRepository.save(user);
    }
    
    /**
     * 🗑️ CACHE EVICT: Clear permissions cache when updating user
     */
    @Override
    @CacheEvict(value = "permissions", allEntries = true)
    public Users updateUser(Long id, java.util.Map<String, Object> userData) {
        log.info("✏️ Updating user {}, clearing permissions cache", id);
        
        Users user = getUserEntityById(id);
        
        // Validate input data
        validateUserData(userData, false);
        
        String newEmail = (String) userData.get("email");
        
        // Check if email already exists (excluding current user)
        if (!user.getEmail().equals(newEmail)) {
            Users existingUser = userRepository.findByEmail(newEmail);
            if (existingUser != null && !existingUser.getId().equals(id)) {
                throw new RuntimeException("Email đã tồn tại: " + newEmail);
            }
        }
        
        user.setFullname((String) userData.get("fullname"));
        user.setEmail(newEmail);
        user.setPhone((String) userData.get("phone"));
        
        // Update password if provided
        String password = (String) userData.get("password");
        if (StringUtils.hasText(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        
        return userRepository.save(user);
    }
    
    
    /**
     * Validate user data
     */
    private void validateUserData(java.util.Map<String, Object> userData, boolean isCreate) {
        String fullname = (String) userData.get("fullname");
        String email = (String) userData.get("email");
        String phone = (String) userData.get("phone");
        String password = (String) userData.get("password");
        
        // Validate fullname
        if (!StringUtils.hasText(fullname)) {
            throw new RuntimeException("Họ tên không được để trống");
        }
        if (fullname.length() < 2 || fullname.length() > 100) {
            throw new RuntimeException("Họ tên phải từ 2-100 ký tự");
        }
        
        // Validate email
        if (!StringUtils.hasText(email)) {
            throw new RuntimeException("Email không được để trống");
        }
        if (!isValidEmail(email)) {
            throw new RuntimeException("Email không đúng định dạng");
        }
        
        // Validate phone
        if (StringUtils.hasText(phone) && !isValidPhone(phone)) {
            throw new RuntimeException("Số điện thoại không đúng định dạng (10 số)");
        }
        
        // Validate password (only for create or when password is provided)
        if (isCreate || StringUtils.hasText(password)) {
            if (!StringUtils.hasText(password)) {
                throw new RuntimeException("Mật khẩu không được để trống");
            }
            if (password.length() < 6) {
                throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
            }
        }
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Validate phone format (10 digits)
     */
    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10}$");
    }
}
