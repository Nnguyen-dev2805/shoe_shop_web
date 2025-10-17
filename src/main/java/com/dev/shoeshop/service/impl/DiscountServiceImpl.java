package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.discount.DiscountCreateRequest;
import com.dev.shoeshop.dto.discount.DiscountResponse;
import com.dev.shoeshop.dto.discount.DiscountUpdateRequest;
import com.dev.shoeshop.entity.Discount;
import com.dev.shoeshop.mapper.DiscountMapper;
import com.dev.shoeshop.repository.DiscountRepository;
import com.dev.shoeshop.service.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    @Override
    public Discount saveDiscount(Discount discount) {
        log.info("Saving discount: {}", discount.getName());
        
        // Tự động cập nhật status dựa trên ngày tháng
        discount.preUpdate();
        
        return discountRepository.save(discount);
    }

    /**
     * Tạo discount mới từ DiscountCreateRequest
     */
    @Override
    @Transactional
    public Discount createDiscount(DiscountCreateRequest request, Long createdBy) {
        log.info("Creating new discount: {}", request.getName());
        
        // Kiểm tra tên discount đã tồn tại chưa
        if (discountRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Discount name already exists: " + request.getName());
        }
        
        // Sử dụng mapper để chuyển đổi
        Discount discount = discountMapper.toEntity(request);
        discount.setCreatedBy(createdBy);
        
        // Tự động cập nhật status dựa trên ngày tháng
        discount.prePersist();
        
        return discountRepository.save(discount);
    }

    /**
     * Cập nhật discount từ DiscountUpdateRequest
     */
    @Override
    @Transactional
    public Discount updateDiscount(Long id, DiscountUpdateRequest request, Long updatedBy) {
        log.info("Updating discount: {} with ID: {}", request.getName(), id);
        
        // Lấy discount hiện tại
        Discount existingDiscount = getDiscountById(id);
        if (existingDiscount == null) {
            throw new IllegalArgumentException("Discount not found with ID: " + id);
        }
        
        // Kiểm tra tên discount đã tồn tại cho discount khác chưa
        if (!existingDiscount.getName().equalsIgnoreCase(request.getName()) 
            && discountRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new IllegalArgumentException("Discount name already exists for another discount: " + request.getName());
        }
        
        // Sử dụng mapper để cập nhật
        discountMapper.updateEntity(existingDiscount, request);
        existingDiscount.setUpdatedBy(updatedBy);
        
        // Tự động cập nhật status dựa trên ngày tháng
        existingDiscount.preUpdate();
        
        return discountRepository.save(existingDiscount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiscountResponse> getAllDiscounts(Pageable pageable) {
        log.info("Getting all discounts with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Discount> discounts = discountRepository.findByIsDeleteFalse(pageable);
        return discounts.map(discountMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getAllDiscounts() {
        log.info("Getting all discounts without pagination");
        
        return discountRepository.findByIsDeleteFalse()
                .stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Discount getDiscountById(Long id) {
        log.info("Getting discount by id: {}", id);
        
        return discountRepository.findById(id)
                .filter(discount -> !discount.getIsDelete())
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountResponseById(Long id) {
        log.info("Getting discount response by id: {}", id);
        
        Discount discount = getDiscountById(id);
        return discount != null ? discountMapper.toResponse(discount) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiscountResponse> getDiscountsByStatus(String status, Pageable pageable) {
        log.info("Getting discounts by status: {} with pagination", status);
        
        Page<Discount> discounts = discountRepository.findByStatusAndIsDeleteFalse(status, pageable);
        return discounts.map(discountMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getDiscountsByStatus(String status) {
        log.info("Getting discounts by status: {}", status);
        
        return discountRepository.findByStatusAndIsDeleteFalse(status)
                .stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getActiveDiscounts() {
        log.info("Getting active discounts");
        
        LocalDate now = LocalDate.now();
        return discountRepository.findActiveDiscounts(now)
                .stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getComingDiscounts() {
        log.info("Getting coming discounts");
        
        LocalDate now = LocalDate.now();
        return discountRepository.findComingDiscounts(now)
                .stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getExpiredDiscounts() {
        log.info("Getting expired discounts");
        
        LocalDate now = LocalDate.now();
        return discountRepository.findExpiredDiscounts(now)
                .stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> searchDiscountsByName(String name) {
        log.info("Searching discounts by name: {}", name);
        
        return discountRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getUsableDiscounts() {
        log.info("Getting usable discounts");
        
        LocalDate now = LocalDate.now();
        return discountRepository.findUsableDiscounts(now)
                .stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Discount updateDiscount(Discount discount) {
        log.info("=== SERVICE: Updating discount: {} with ID: {}", discount.getName(), discount.getId());
        log.info("=== SERVICE: Discount minOrderValue: {}", discount.getMinOrderValue());
        
        Discount existingDiscount = getDiscountById(discount.getId());
        if (existingDiscount == null) {
            log.error("=== SERVICE: Discount not found with id: {}", discount.getId());
            throw new RuntimeException("Discount not found with id: " + discount.getId());
        }
        
        log.info("=== SERVICE: Found existing discount: {}", existingDiscount.getName());
        
        // Tự động cập nhật status dựa trên ngày tháng
        discount.preUpdate();
        
        log.info("=== SERVICE: About to save discount to database");
        Discount savedDiscount = discountRepository.save(discount);
        log.info("=== SERVICE: Successfully saved discount: {} with minOrderValue: {}", 
                savedDiscount.getName(), savedDiscount.getMinOrderValue());
        
        return savedDiscount;
    }

    @Override
    public void deleteDiscount(Long id) {
        log.info("Soft deleting discount with id: {}", id);
        
        Discount discount = getDiscountById(id);
        if (discount == null) {
            throw new RuntimeException("Discount not found with id: " + id);
        }
        
        discount.setIsDelete(true);
        discountRepository.save(discount);
    }

    @Override
    public void permanentDeleteDiscount(Long id) {
        log.info("Permanently deleting discount with id: {}", id);
        
        discountRepository.deleteById(id);
    }

    @Override
    public void restoreDiscount(Long id) {
        log.info("Restoring discount with id: {}", id);
        
        Discount discount = discountRepository.findById(id).orElse(null);
        if (discount == null) {
            throw new RuntimeException("Discount not found with id: " + id);
        }
        
        discount.setIsDelete(false);
        discountRepository.save(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        log.info("Counting discounts by status: {}", status);
        
        return discountRepository.countByStatusAndIsDeleteFalse(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalCount() {
        log.info("Getting total discount count");
        
        return discountRepository.countByIsDeleteFalse();
    }

    @Override
    public void autoUpdateDiscountStatus() {
        log.info("Auto updating discount status based on dates");
        
        List<Discount> allDiscounts = discountRepository.findByIsDeleteFalse();
        LocalDate now = LocalDate.now();
        
        for (Discount discount : allDiscounts) {
            String oldStatus = discount.getStatus();
            discount.preUpdate();
            
            if (!oldStatus.equals(discount.getStatus())) {
                discountRepository.save(discount);
                log.info("Updated discount {} status from {} to {}", 
                        discount.getName(), oldStatus, discount.getStatus());
            }
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getAvailableDiscounts() {
        log.info("Getting available discounts for users");
        return getUsableDiscounts(); // Delegate to existing method
    }
    
    // ========== SHIPPING VOUCHER IMPLEMENTATIONS ==========
    
    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getActiveOrderVouchers() {
        log.info("Getting active order vouchers");
        
        List<Discount> orderVouchers = discountRepository.findByIsDeleteFalse().stream()
                .filter(d -> "ACTIVE".equals(d.getStatus()))
                .filter(d -> d.getType() == com.dev.shoeshop.enums.VoucherType.ORDER_DISCOUNT)
                .filter(d -> d.getStartDate() != null && !LocalDate.now().isBefore(d.getStartDate()))
                .filter(d -> d.getEndDate() != null && !LocalDate.now().isAfter(d.getEndDate()))
                .toList();
        
        log.info("Found {} active order vouchers", orderVouchers.size());
        return orderVouchers.stream()
                .map(discountMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getActiveShippingVouchers() {
        log.info("Getting active shipping vouchers");
        
        List<Discount> shippingVouchers = discountRepository.findByIsDeleteFalse().stream()
                .filter(d -> "ACTIVE".equals(d.getStatus()))
                .filter(d -> d.getType() == com.dev.shoeshop.enums.VoucherType.SHIPPING_DISCOUNT)
                .filter(d -> d.getStartDate() != null && !LocalDate.now().isBefore(d.getStartDate()))
                .filter(d -> d.getEndDate() != null && !LocalDate.now().isAfter(d.getEndDate()))
                .toList();
        
        log.info("Found {} active shipping vouchers", shippingVouchers.size());
        return shippingVouchers.stream()
                .map(discountMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculateShippingDiscount(Long voucherId, Double shippingFee, Double orderValue) {
        log.info("Calculating shipping discount for voucher ID: {}, shipping fee: {}, order value: {}", 
                voucherId, shippingFee, orderValue);
        
        if (voucherId == null || shippingFee == null || shippingFee <= 0) {
            log.warn("Invalid parameters for shipping discount calculation");
            return 0.0;
        }
        
        Discount voucher = getDiscountById(voucherId);
        if (voucher == null) {
            log.warn("Voucher not found: {}", voucherId);
            return 0.0;
        }
        
        Double discountAmount = voucher.calculateShippingDiscount(shippingFee, orderValue);
        log.info("Calculated shipping discount: {} for voucher: {}", discountAmount, voucher.getName());
        
        return discountAmount;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateShippingVoucher(Long voucherId, Double orderValue, Double shippingFee) {
        log.info("Validating shipping voucher ID: {} for order value: {}, shipping fee: {}", 
                voucherId, orderValue, shippingFee);
        
        try {
            Discount voucher = getDiscountById(voucherId);
            
            if (voucher == null) {
                log.warn("Voucher not found: {}", voucherId);
                return false;
            }
            
            // Check if it's a shipping voucher
            if (!voucher.isShippingVoucher()) {
                log.warn("Voucher {} is not a shipping voucher", voucherId);
                return false;
            }
            
            // Check status
            if (!"ACTIVE".equals(voucher.getStatus())) {
                log.warn("Voucher {} is not active. Status: {}", voucherId, voucher.getStatus());
                return false;
            }
            
            // Check date range
            LocalDate now = LocalDate.now();
            if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
                log.warn("Voucher {} has not started yet", voucherId);
                return false;
            }
            
            if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
                log.warn("Voucher {} has expired", voucherId);
                return false;
            }
            
            // Check min order value
            if (voucher.getMinOrderValue() != null && orderValue != null) {
                if (orderValue < voucher.getMinOrderValue()) {
                    log.warn("Order value {} is less than min order value {}", 
                            orderValue, voucher.getMinOrderValue());
                    return false;
                }
            }
            
            // Check if can calculate discount
            Double discount = voucher.calculateShippingDiscount(shippingFee, orderValue);
            if (discount == null || discount <= 0) {
                log.warn("Cannot calculate valid discount for voucher {}", voucherId);
                return false;
            }
            
            log.info("✅ Shipping voucher {} is valid", voucherId);
            return true;
            
        } catch (Exception e) {
            log.error("Error validating shipping voucher: {}", e.getMessage(), e);
            return false;
        }
    }
}
