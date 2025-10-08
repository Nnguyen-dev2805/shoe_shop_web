package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Long id;
    private String address; // Combined address display
    private String addressLine;
    private String district;
    private String city;
    private String postalCode;
    private String country;
    private Boolean isDefault;
    
    // Helper method to get full address display
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        
        if (addressLine != null && !addressLine.trim().isEmpty()) {
            fullAddress.append(addressLine);
        }
        
        if (district != null && !district.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(district);
        }
        
        if (city != null && !city.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(city);
        }
        
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(" ");
            fullAddress.append(postalCode);
        }
        
        if (country != null && !country.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(country);
        }
        
        return fullAddress.toString();
    }
}
