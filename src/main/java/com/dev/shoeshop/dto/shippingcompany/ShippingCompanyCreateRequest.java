package com.dev.shoeshop.dto.shippingcompany;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingCompanyCreateRequest {
    
    @NotBlank(message = "Tên công ty vận chuyển không được để trống")
    private String name;
    
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Số điện thoại không hợp lệ")
    private String hotline;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String address;
    
    @Pattern(regexp = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$", 
             message = "Website không hợp lệ")
    private String website;
    
    @NotNull(message = "Trạng thái hoạt động không được để trống")
    private Boolean isActive;
}
