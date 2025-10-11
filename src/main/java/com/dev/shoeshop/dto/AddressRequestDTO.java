package com.dev.shoeshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {
    
    // ⭐ Thông tin người nhận - BẮT BUỘC
    @NotBlank(message = "Tên người nhận không được để trống")
    private String recipientName;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    private String recipientPhone;
    
    // ⭐ Địa chỉ từ Goong Maps
    @NotBlank(message = "Địa chỉ không được để trống")
    private String selectedAddress; // Full address from Goong Maps
    
    private Double latitude;  // GPS coordinates
    private Double longitude; // GPS coordinates
    
    // ⭐ Chi tiết địa chỉ - PHIÊN BẢN ĐƠN GIẢN
    @NotBlank(message = "Số nhà, tên đường không được để trống")
    private String street; // Bao gồm: Số nhà, đường, phường, quận (VD: "Toyota Dũng Tiến, 233 Đại Lộ Hùng Vương, Phường 5, Tuy Hòa")
    
    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String city; // Tỉnh/Thành phố (VD: "Phú Yên", "Hồ Chí Minh")
    
    private String country; // Mặc định "Việt Nam" - không bắt buộc nhập
    
    private String postalCode; // Optional
    
    // ⭐ Metadata
    private String addressType; // HOME hoặc OFFICE (chỉ 2 loại)
    
    private Boolean isDefault = false; // Checkbox: Đặt làm địa chỉ mặc định
}
