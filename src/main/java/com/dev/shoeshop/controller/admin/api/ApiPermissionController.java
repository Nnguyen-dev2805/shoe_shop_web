package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.permission.PermissionResponse;
import com.dev.shoeshop.dto.permission.PermissionUpdateRequest;
import com.dev.shoeshop.entity.Role;
import com.dev.shoeshop.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApiPermissionController {

    private final PermissionService permissionService;

    /**
     * Lấy danh sách tất cả users với pagination và filtering
     * URL: GET /api/permission?page=0&size=10&role=admin
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PermissionResponse> usersPage;

            if (role != null && !role.trim().isEmpty()) {
                usersPage = permissionService.getUsersByRole(role, pageable);
            } else {
                usersPage = permissionService.getAllUsers(pageable);
            }

            return ResponseEntity.ok(usersPage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Lấy user theo ID
     * URL: GET /api/permission/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            PermissionResponse user = permissionService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy user với ID: " + id);
        }
    }

    /**
     * Cập nhật role cho user
     * URL: PUT /api/permission/{id}/role
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Integer id,
            @Valid @RequestBody PermissionUpdateRequest request,
            BindingResult result) {

        if (result.hasErrors()) {
            String errorMsg = result.getFieldErrors().stream()
                    .map(e -> e.getField() + " - " + e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body("Lỗi validation: " + errorMsg);
        }

        try {
            PermissionResponse updatedUser = permissionService.updateUserRole(id, request.getRoleId());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách tất cả roles
     * URL: GET /api/permission/roles
     */
    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        try {
            List<Role> roles = permissionService.getAllRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Lấy users theo role
     * URL: GET /api/permission/role/{roleName}
     */
    @GetMapping("/role/{roleName}")
    public ResponseEntity<?> getUsersByRole(
            @PathVariable String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PermissionResponse> usersPage = permissionService.getUsersByRole(roleName, pageable);
            return ResponseEntity.ok(usersPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Tìm kiếm users theo tên
     * URL: GET /api/permission/search?name=admin&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PermissionResponse> usersPage = permissionService.searchUsersByName(name, pageable);
            return ResponseEntity.ok(usersPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Thống kê số lượng users theo role
     * URL: GET /api/permission/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            long adminCount = permissionService.countUsersByRole("admin");
            long managerCount = permissionService.countUsersByRole("manager");
            long userCount = permissionService.countUsersByRole("user");
            long shipperCount = permissionService.countUsersByRole("shipper");
            long totalCount = permissionService.countAllUsers();

            var stats = new Object() {
                public final long admin = adminCount;
                public final long manager = managerCount;
                public final long user = userCount;
                public final long shipper = shipperCount;
                public final long total = totalCount;
            };
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }
}
