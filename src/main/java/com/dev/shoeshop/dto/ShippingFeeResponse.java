package com.dev.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeResponse {
    
    private Boolean success;
    private Integer fee;
    private Double distanceKm;
    private String formattedDistance;
    private String formattedDuration;
    private String warehouseName;
    private String warehouseAddress;
    private String message;
    private String errorCode;
    
    /**
     * Success response
     */
    public static ShippingFeeResponse success(Integer fee, Double distanceKm, 
                                              String warehouseName, String warehouseAddress,
                                              String formattedDistance, String formattedDuration) {
        return ShippingFeeResponse.builder()
                .success(true)
                .fee(fee)
                .distanceKm(distanceKm)
                .formattedDistance(formattedDistance)
                .formattedDuration(formattedDuration)
                .warehouseName(warehouseName)
                .warehouseAddress(warehouseAddress)
                .message("Tính phí thành công")
                .build();
    }
    
    /**
     * Error response
     */
    public static ShippingFeeResponse error(String message, String errorCode) {
        return ShippingFeeResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
