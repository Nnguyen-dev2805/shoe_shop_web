package com.dev.shoeshop.dto.shippingcompany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingCompanyResponse {
    
    private Integer id;
    private String name;
    private String hotline;
    private String email;
    private String address;
    private String website;
    private Boolean isActive;
    
    // Helper methods for display
    public String getStatusDisplay() {
        return isActive ? "Hoạt động" : "Không hoạt động";
    }
    
    public String getStatusBadgeClass() {
        return isActive ? "bg-success-subtle text-success" : "bg-danger-subtle text-danger";
    }
    
    public String getHotlineDisplay() {
        return hotline != null && !hotline.trim().isEmpty() ? hotline : "Chưa cập nhật";
    }
    
    public String getEmailDisplay() {
        return email != null && !email.trim().isEmpty() ? email : "Chưa cập nhật";
    }
    
    public String getAddressDisplay() {
        return address != null && !address.trim().isEmpty() ? address : "Chưa cập nhật";
    }
    
    public String getWebsiteDisplay() {
        return website != null && !website.trim().isEmpty() ? website : "Chưa cập nhật";
    }
}
