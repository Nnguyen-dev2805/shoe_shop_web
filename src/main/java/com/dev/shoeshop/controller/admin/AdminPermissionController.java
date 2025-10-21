package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.dto.permission.PermissionResponse;
import com.dev.shoeshop.entity.Address;
import com.dev.shoeshop.entity.Role;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.PermissionService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPermissionController {

    private final PermissionService permissionService;

    /**
     * Hiển thị danh sách permissions
     * URL: /admin/permission
     */
    @GetMapping("/permission")
    public String permissionList(HttpSession session, Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String role) {
        
        // Kiểm tra đăng nhập
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PermissionResponse> usersPage;
            
            // Lấy danh sách users theo role hoặc tất cả
            if (role != null && !role.trim().isEmpty()) {
                usersPage = permissionService.getUsersByRole(role, pageable);
            } else {
                usersPage = permissionService.getAllUsers(pageable);
            }
            
            // Lấy danh sách roles
            List<Role> roles = permissionService.getAllRoles();
            
            // Thống kê số lượng users theo từng role
            long adminCount = permissionService.countUsersByRole("admin");
            long managerCount = permissionService.countUsersByRole("manager");
            long userCount = permissionService.countUsersByRole("user");
            long shipperCount = permissionService.countUsersByRole("shipper");
            
            model.addAttribute("users", usersPage.getContent());
            model.addAttribute("roles", roles);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usersPage.getTotalPages());
            model.addAttribute("totalElements", usersPage.getTotalElements());
            model.addAttribute("selectedRole", role);
            
            // Thống kê
            model.addAttribute("admin", adminCount);
            model.addAttribute("manager", managerCount);
            model.addAttribute("user", userCount);
            model.addAttribute("shipper", shipperCount);
            
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách permissions: " + e.getMessage());
            model.addAttribute("users", java.util.Collections.emptyList());
            model.addAttribute("roles", java.util.Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
            
            // Thống kê mặc định
            model.addAttribute("admin", 0);
            model.addAttribute("manager", 0);
            model.addAttribute("user", 0);
            model.addAttribute("shipper", 0);
        }
        
        return "admin/permission/pages-permissions";
    }

    /**
     * Cập nhật role cho user
     * URL: POST /admin/permission/assignRole
     */
    @PostMapping("/permission/assignRole")
    @ResponseBody
    public String assignRole(@RequestParam Long userId,
                           @RequestParam Long roleId,
                           HttpSession session) {
        
        // Kiểm tra đăng nhập
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "error: Chưa đăng nhập";
        }
        
        try {
            permissionService.updateUserRole(userId, roleId);
            return "success: Vai trò đã được cập nhật thành công!";
        } catch (Exception e) {
            return "error: Có lỗi xảy ra khi cập nhật vai trò: " + e.getMessage();
        }
    }
    
    /**
     * Get user by ID
     * URL: GET /admin/api/user/{id}
     */
    @GetMapping("/api/user/{id}")
    @ResponseBody
    public java.util.Map<String, Object> getUserById(@PathVariable Long id, HttpSession session) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        Users currentUser = (Users) session.getAttribute(Constant.SESSION_USER);
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Chưa đăng nhập");
            return response;
        }
        
        try {
            Users user = permissionService.getUserEntityById(id);
            if (user != null) {
                java.util.Map<String, Object> userData = new java.util.HashMap<>();
                userData.put("id", user.getId());
                userData.put("fullname", user.getFullname());
                userData.put("email", user.getEmail());
                userData.put("phone", user.getPhone());
                
                // Get default address if exists
                String address = "";
                if (user.getUserAddresses() != null && !user.getUserAddresses().isEmpty()) {
                    var defaultAddr = user.getUserAddresses().stream()
                        .filter(ua -> ua.getIsDefault() != null && ua.getIsDefault())
                        .findFirst()
                        .orElse(user.getUserAddresses().get(0));
                    
                    if (defaultAddr != null && defaultAddr.getAddress() != null) {
                        Address addr = defaultAddr.getAddress();
                        address = addr.getAddress_line() + ", " + addr.getCity();
                    }
                }
                userData.put("address", address);
                
                response.put("success", true);
                response.put("data", userData);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy user");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Create new user
     * URL: POST /admin/api/user/create
     */
    @PostMapping("/api/user/create")
    @ResponseBody
    public java.util.Map<String, Object> createUser(@RequestBody java.util.Map<String, Object> userData, HttpSession session) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        Users currentUser = (Users) session.getAttribute(Constant.SESSION_USER);
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Chưa đăng nhập");
            return response;
        }
        
        // Check admin permission
        if (currentUser.getRole() == null || 
            (!currentUser.getRole().getRoleName().equalsIgnoreCase("admin") && 
             !currentUser.getRole().getRoleName().equalsIgnoreCase("manager"))) {
            response.put("success", false);
            response.put("message", "Không có quyền thực hiện chức năng này");
            return response;
        }
        
        try {
            Users newUser = permissionService.createUser(userData);
            response.put("success", true);
            response.put("message", "Thêm user thành công");
            
            // Return safe user data (without password)
            java.util.Map<String, Object> safeUserData = new java.util.HashMap<>();
            safeUserData.put("id", newUser.getId());
            safeUserData.put("fullname", newUser.getFullname());
            safeUserData.put("email", newUser.getEmail());
            safeUserData.put("phone", newUser.getPhone());
            safeUserData.put("roleName", newUser.getRole().getRoleName());
            
            response.put("data", safeUserData);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Update user
     * URL: PUT /admin/api/user/update/{id}
     */
    @PutMapping("/api/user/update/{id}")
    @ResponseBody
    public java.util.Map<String, Object> updateUser(@PathVariable Long id, @RequestBody java.util.Map<String, Object> userData, HttpSession session) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        Users currentUser = (Users) session.getAttribute(Constant.SESSION_USER);
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Chưa đăng nhập");
            return response;
        }
        
        // Check admin permission
        if (currentUser.getRole() == null || 
            (!currentUser.getRole().getRoleName().equalsIgnoreCase("admin") && 
             !currentUser.getRole().getRoleName().equalsIgnoreCase("manager"))) {
            response.put("success", false);
            response.put("message", "Không có quyền thực hiện chức năng này");
            return response;
        }
        
        try {
            Users updatedUser = permissionService.updateUser(id, userData);
            response.put("success", true);
            response.put("message", "Cập nhật user thành công");
            
            // Return safe user data (without password)
            java.util.Map<String, Object> safeUserData = new java.util.HashMap<>();
            safeUserData.put("id", updatedUser.getId());
            safeUserData.put("fullname", updatedUser.getFullname());
            safeUserData.put("email", updatedUser.getEmail());
            safeUserData.put("phone", updatedUser.getPhone());
            safeUserData.put("roleName", updatedUser.getRole().getRoleName());
            
            response.put("data", safeUserData);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
}
