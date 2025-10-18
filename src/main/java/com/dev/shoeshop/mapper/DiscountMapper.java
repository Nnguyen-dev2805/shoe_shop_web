package com.dev.shoeshop.mapper;

import com.dev.shoeshop.dto.discount.DiscountCreateRequest;
import com.dev.shoeshop.dto.discount.DiscountResponse;
import com.dev.shoeshop.dto.discount.DiscountUpdateRequest;
import com.dev.shoeshop.entity.Discount;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Mapper để chuyển đổi giữa Discount Entity và các DTO
 */
@Component
public class DiscountMapper {

    /**
     * Chuyển đổi từ DiscountCreateRequest sang Discount Entity
     */
    public Discount toEntity(DiscountCreateRequest request) {
        if (request == null) {
            return null;
        }

        Discount discount = new Discount();
        discount.setName(request.getName());
        discount.setQuantity(request.getQuantity());
        discount.setPercent(request.getPercent());
        discount.setStatus(request.getStatus());
        discount.setMinOrderValue(request.getMinOrderValue());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setCreatedDate(LocalDate.now());
        discount.setIsDelete(false);
        
        // Shipping voucher fields
        discount.setType(request.getType());
        discount.setDiscountValueType(request.getDiscountValueType());
        discount.setMaxDiscountAmount(request.getMaxDiscountAmount());
        
        return discount;
    }

    /**
     * Chuyển đổi từ DiscountUpdateRequest sang Discount Entity
     */
    public Discount toEntity(DiscountUpdateRequest request) {
        if (request == null) {
            return null;
        }

        Discount discount = new Discount();
        discount.setId(request.getId());
        discount.setName(request.getName());
        discount.setQuantity(request.getQuantity());
        discount.setPercent(request.getPercent());
        discount.setStatus(request.getStatus());
        discount.setMinOrderValue(request.getMinOrderValue());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setUpdatedDate(LocalDate.now());
        
        return discount;
    }

    /**
     * Chuyển đổi từ Discount Entity sang DiscountResponse
     */
    public DiscountResponse toResponse(Discount discount) {
        if (discount == null) {
            return null;
        }

        DiscountResponse response = new DiscountResponse();
        response.setId(discount.getId());
        response.setName(discount.getName());
        response.setQuantity(discount.getQuantity());
        response.setPercent(discount.getPercent());
        response.setStatus(discount.getStatus());
        response.setMinOrderValue(discount.getMinOrderValue());
        response.setStartDate(discount.getStartDate());
        response.setEndDate(discount.getEndDate());
        response.setCreatedDate(discount.getCreatedDate());
        
        // Shipping voucher fields
        response.setType(discount.getType());
        response.setDiscountValueType(discount.getDiscountValueType());
        response.setMaxDiscountAmount(discount.getMaxDiscountAmount());
        
        return response;
    }

    /**
     * Cập nhật existing Discount Entity với dữ liệu từ DiscountUpdateRequest
     */
    public void updateEntity(Discount existingDiscount, DiscountUpdateRequest request) {
        if (existingDiscount == null || request == null) {
            return;
        }

        existingDiscount.setName(request.getName());
        existingDiscount.setQuantity(request.getQuantity());
        existingDiscount.setPercent(request.getPercent());
        existingDiscount.setStatus(request.getStatus());
        existingDiscount.setMinOrderValue(request.getMinOrderValue());
        existingDiscount.setStartDate(request.getStartDate());
        existingDiscount.setEndDate(request.getEndDate());
        existingDiscount.setUpdatedDate(LocalDate.now());
    }

    /**
     * Cập nhật existing Discount Entity với dữ liệu từ DiscountCreateRequest
     */
    public void updateEntityFromCreate(Discount existingDiscount, DiscountCreateRequest request, Long createdBy) {
        if (existingDiscount == null || request == null) {
            return;
        }

        existingDiscount.setName(request.getName());
        existingDiscount.setQuantity(request.getQuantity());
        existingDiscount.setPercent(request.getPercent());
        existingDiscount.setStatus(request.getStatus());
        existingDiscount.setMinOrderValue(request.getMinOrderValue());
        existingDiscount.setStartDate(request.getStartDate());
        existingDiscount.setEndDate(request.getEndDate());
        existingDiscount.setCreatedBy(createdBy);
        existingDiscount.setCreatedDate(LocalDate.now());
        existingDiscount.setIsDelete(false);
    }

    /**
     * Tạo Discount Entity từ thông tin cơ bản
     */
    public Discount createBasicDiscount(String name, Double percent, String status, 
                                       LocalDate startDate, LocalDate endDate, Long createdBy) {
        Discount discount = new Discount();
        discount.setName(name);
        discount.setPercent(percent);
        discount.setStatus(status);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setCreatedBy(createdBy);
        discount.setCreatedDate(LocalDate.now());
        discount.setIsDelete(false);
        discount.setQuantity(1000); // Default quantity
        
        return discount;
    }
}
