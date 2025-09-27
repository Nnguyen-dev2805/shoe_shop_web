package com.dev.shoeshop.dto.permission;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionUpdateRequest {
    @NotNull(message = "User ID không được để trống")
    private Long userId;
    
    @NotNull(message = "Role ID không được để trống")
    private Long roleId;
}
