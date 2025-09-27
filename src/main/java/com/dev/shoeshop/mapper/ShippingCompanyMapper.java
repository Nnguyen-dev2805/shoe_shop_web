package com.dev.shoeshop.mapper;

import com.dev.shoeshop.dto.shippingcompany.ShippingCompanyCreateRequest;
import com.dev.shoeshop.dto.shippingcompany.ShippingCompanyResponse;
import com.dev.shoeshop.dto.shippingcompany.ShippingCompanyUpdateRequest;
import com.dev.shoeshop.entity.ShippingCompany;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShippingCompanyMapper {
    
    // Entity to Response
    public ShippingCompanyResponse toResponse(ShippingCompany shippingCompany) {
        if (shippingCompany == null) {
            return null;
        }
        
        return new ShippingCompanyResponse(
                shippingCompany.getId(),
                shippingCompany.getName(),
                shippingCompany.getHotline(),
                shippingCompany.getEmail(),
                shippingCompany.getAddress(),
                shippingCompany.getWebsite(),
                shippingCompany.getIsActive()
        );
    }
    
    // List Entity to List Response
    public List<ShippingCompanyResponse> toResponseList(List<ShippingCompany> shippingCompanies) {
        return shippingCompanies.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    // CreateRequest to Entity
    public ShippingCompany toEntity(ShippingCompanyCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        ShippingCompany shippingCompany = new ShippingCompany();
        shippingCompany.setName(request.getName());
        shippingCompany.setHotline(request.getHotline());
        shippingCompany.setEmail(request.getEmail());
        shippingCompany.setAddress(request.getAddress());
        shippingCompany.setWebsite(request.getWebsite());
        shippingCompany.setIsActive(request.getIsActive());
        
        return shippingCompany;
    }
    
    // UpdateRequest to Entity (existing)
    public void updateEntity(ShippingCompany shippingCompany, ShippingCompanyUpdateRequest request) {
        if (shippingCompany == null || request == null) {
            return;
        }
        
        shippingCompany.setName(request.getName());
        shippingCompany.setHotline(request.getHotline());
        shippingCompany.setEmail(request.getEmail());
        shippingCompany.setAddress(request.getAddress());
        shippingCompany.setWebsite(request.getWebsite());
        shippingCompany.setIsActive(request.getIsActive());
    }
}
