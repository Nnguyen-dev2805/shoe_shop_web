package com.dev.shoeshop.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionResponse {
    private Long id;
    private String fullname;
    private String email;
    private String address;
    private String phone;
    private Long roleId;
    private String roleName;
    
    // Helper methods for display
    public String getRoleDisplayName() {
        if (roleName == null) return "Chưa có vai trò";
        switch (roleName.toLowerCase()) {
            case "admin": return "Quản trị viên";
            case "manager": return "Quản lý";
            case "user": return "Người dùng";
            case "shipper": return "Người giao hàng";
            default: return roleName;
        }
    }
    
    public String getRoleBadgeClass() {
        if (roleName == null) return "bg-secondary-subtle text-secondary";
        switch (roleName.toLowerCase()) {
            case "admin": return "bg-danger-subtle text-danger";
            case "manager": return "bg-warning-subtle text-warning";
            case "user": return "bg-primary-subtle text-primary";
            case "shipper": return "bg-info-subtle text-info";
            default: return "bg-secondary-subtle text-secondary";
        }
    }
}
