package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.dto.permission.PermissionResponse;
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
    public String assignRole(@RequestParam Integer userId, 
                           @RequestParam Integer roleId,
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
}
