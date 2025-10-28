package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.CoinTransaction;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.ReturnRequest;
import com.dev.shoeshop.entity.ReturnShipment;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.CoinTransactionType;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.enums.ReturnReason;
import com.dev.shoeshop.enums.ReturnStatus;
import com.dev.shoeshop.repository.CoinTransactionRepository;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.repository.ReturnRequestRepository;
import com.dev.shoeshop.repository.ReturnShipmentRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.EmailService;
import com.dev.shoeshop.service.MembershipService;
import com.dev.shoeshop.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReturnRequestServiceImpl implements ReturnRequestService {
    
    private final ReturnRequestRepository returnRequestRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ReturnShipmentRepository returnShipmentRepository;
    private final CoinTransactionRepository coinTransactionRepository;
    private final EmailService emailService;
    private final MembershipService membershipService;
    
    /**
     * 🗑️ CACHE EVICT: Clear return request cache when creating
     */
    @Override
    @CacheEvict(value = "returnRequests", allEntries = true)
    public ReturnRequest createReturnRequest(Long orderId, Long userId, ReturnReason reason, 
                                            String description, String images) {
        log.info("➕ Creating return request for order: {} by user: {}, clearing cache", orderId, userId);
        
        // 1. Validate order exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        // 2. Validate user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        // 3. Validate order belongs to user
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Order does not belong to this user");
        }
        
        // 4. Validate order status (chỉ đơn DELIVERED mới được trả)
        if (order.getStatus() != ShipmentStatus.DELIVERED) {
            throw new RuntimeException("Chỉ đơn hàng đã giao mới có thể trả hàng");
        }
        
        // 5. Check if return request already exists
        if (returnRequestRepository.existsByOrderId(orderId)) {
            throw new RuntimeException("Đơn hàng này đã có yêu cầu trả hàng");
        }
        
        // 6. Check time limit (ví dụ: trong vòng 7 ngày)
        // Note: Order entity không có deliveryDate, skip time check hoặc sử dụng createdDate
        if (order.getCreatedDate() != null) {
            LocalDateTime createdDate = new java.sql.Timestamp(order.getCreatedDate().getTime()).toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            long daysSinceCreated = java.time.Duration.between(createdDate, now).toDays();
            // Assume 14 days for return (7 days delivery + 7 days return window)
            if (daysSinceCreated > 14) {
                throw new RuntimeException("Đã quá thời hạn trả hàng (14 ngày kể từ ngày đặt)");
            }
        }
        
        // 7. Create return request
        ReturnRequest returnRequest = ReturnRequest.builder()
                .order(order)
                .user(user)
                .reason(reason)
                .description(description)
                .images(images)
                .status(ReturnStatus.PENDING)
                .build();
        
        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        log.info("Return request created successfully: {}", saved.getId());
        
        // 8. Update order status to RETURN
        order.setStatus(ShipmentStatus.RETURN);
        orderRepository.save(order);
        log.info("Order status updated to RETURN for order: {}", orderId);
        
        return saved;
    }
    
    /**
     * 🗑️ CACHE EVICT: Clear return request cache when approving
     */
    @Override
    @CacheEvict(value = "returnRequests", allEntries = true)
    public ReturnRequest approveReturnRequest(Long returnId, String adminNote) {
        log.info("✅ Approving return request: {}, clearing cache", returnId);
        
        ReturnRequest returnRequest = getReturnRequestById(returnId);
        
        // Validate status
        if (returnRequest.getStatus() != ReturnStatus.PENDING) {
            throw new RuntimeException("Return request is not in PENDING status");
        }
        
        // Update status
        returnRequest.setStatus(ReturnStatus.APPROVED);
        returnRequest.setAdminNote(adminNote);
        returnRequest.setApprovedDate(LocalDateTime.now());
        
        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        log.info("Return request approved: {}", returnId);
        
        return saved;
    }
    
    /**
     * 🗑️ CACHE EVICT: Clear return request cache when rejecting
     */
    @Override
    @CacheEvict(value = "returnRequests", allEntries = true)
    public ReturnRequest rejectReturnRequest(Long returnId, String adminNote) {
        log.info("❌ Rejecting return request: {}, clearing cache", returnId);
        
        ReturnRequest returnRequest = getReturnRequestById(returnId);
        
        // Validate status
        if (returnRequest.getStatus() != ReturnStatus.PENDING) {
            throw new RuntimeException("Return request is not in PENDING status");
        }
        
        // Update status
        returnRequest.setStatus(ReturnStatus.REJECTED);
        returnRequest.setAdminNote(adminNote);
        
        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        log.info("Return request rejected: {}", returnId);
        
        // Send email notification to user
        try {
            emailService.sendReturnRejectedEmail(saved);
            log.info("Rejection email notification sent to user: {}", saved.getUser().getEmail());
        } catch (Exception emailEx) {
            log.error("Failed to send rejection email notification: {}", emailEx.getMessage());
            // Don't fail the rejection process if email fails
        }
        
        return saved;
    }
    
    @Override
    public ReturnRequest markAsReceived(Long returnId, String adminNote) {
        log.info("Marking return request as received: {}", returnId);
        
        ReturnRequest returnRequest = getReturnRequestById(returnId);
        
        // Validate status
        if (returnRequest.getStatus() != ReturnStatus.SHIPPING) {
            throw new RuntimeException("Return request must be in SHIPPING status");
        }
        
        // Update status
        returnRequest.setStatus(ReturnStatus.RECEIVED);
        returnRequest.setAdminNote(adminNote);
        returnRequest.setReceivedDate(LocalDateTime.now());
        
        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        log.info("Return request marked as received: {}", returnId);
        
        return saved;
    }
    
    @Override
    public ReturnRequest processRefund(Long returnId, BigDecimal refundAmount) {
        log.info("Processing refund for return request: {} with amount: {}", returnId, refundAmount);
        
        ReturnRequest returnRequest = getReturnRequestById(returnId);
        
        // Validate status
        if (returnRequest.getStatus() != ReturnStatus.RECEIVED) {
            throw new RuntimeException("Return request must be RECEIVED before refunding");
        }
        
        // Validate refund amount
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid refund amount");
        }
        
        Order order = returnRequest.getOrder();
        BigDecimal orderTotal = BigDecimal.valueOf(order.getTotalPrice());
        
        if (refundAmount.compareTo(orderTotal) > 0) {
            throw new RuntimeException("Refund amount cannot exceed order total");
        }
        
        try {
            // Hoàn xu vào ví khách hàng
            Users user = returnRequest.getUser();
            
            // Cập nhật coins cho user
            Long currentCoins = user.getCoins() != null ? user.getCoins() : 0L;
            Long refundCoins = refundAmount.longValue();
            Long newBalance = currentCoins + refundCoins;
            user.setCoins(newBalance);
            userRepository.save(user);
            
            log.info("Refunded {} coins to user {}, new balance: {}", refundCoins, user.getId(), newBalance);
            
            // Create CoinTransaction record for tracking
            CoinTransaction coinTransaction = CoinTransaction.builder()
                    .user(user)
                    .transactionType(CoinTransactionType.EARNED)
                    .amount(refundCoins)
                    .balanceAfter(newBalance)  // ← Set balance after transaction
                    .referenceType("RETURN_REQUEST")
                    .referenceId(returnRequest.getId())
                    .description("Hoàn xu từ trả hàng đơn #" + order.getId())
                    .build();
            coinTransactionRepository.save(coinTransaction);
            log.info("Created coin transaction record for refund: {} coins to user {}, balance after: {}", refundCoins, user.getId(), newBalance);
            
            // 🪙 REFUND LOYALTY POINTS for returned order (same logic as cancel)
            try {
                membershipService.refundPointsFromCancelledOrder(user, order);
                log.info("🪙 Refunded loyalty points for returned order {}", order.getId());
            } catch (Exception e) {
                log.error("❌ Error refunding loyalty points for returned order: {}", e.getMessage());
                // Don't fail the refund process if points refund fails
            }
            
            // Update return request
            returnRequest.setStatus(ReturnStatus.REFUNDED);
            returnRequest.setRefundAmount(refundAmount);
            returnRequest.setCompletedDate(LocalDateTime.now());
            
            ReturnRequest saved = returnRequestRepository.save(returnRequest);
            log.info("Refund processed successfully for return request: {}", returnId);
            
            // Send email notification to user
            try {
                emailService.sendRefundCompletedEmail(saved);
                log.info("Refund email notification sent to user: {}", user.getEmail());
            } catch (Exception emailEx) {
                log.error("Failed to send refund email notification: {}", emailEx.getMessage());
                // Don't fail the refund process if email fails
            }
            
            return saved;
        } catch (Exception e) {
            log.error("Error processing refund for return request: {}", returnId, e);
            throw new RuntimeException("Failed to process refund: " + e.getMessage());
        }
    }
    
    @Override
    public ReturnRequest updateTrackingCode(Long returnId, String trackingCode) {
        log.info("Updating tracking code for return request: {}", returnId);
        
        ReturnRequest returnRequest = getReturnRequestById(returnId);
        
        // Validate status
        if (returnRequest.getStatus() != ReturnStatus.APPROVED) {
            throw new RuntimeException("Can only update tracking code for APPROVED requests");
        }
        
        // Update tracking code and status
        returnRequest.setShippingTrackingCode(trackingCode);
        returnRequest.setStatus(ReturnStatus.SHIPPING);
        
        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        log.info("Tracking code updated for return request: {}", returnId);
        
        return saved;
    }
    
    /**
     * ⚡ CACHED: Get return request by ID
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "returnRequests", key = "'detail:' + #id")
    public ReturnRequest getReturnRequestById(Long id) {
        log.info("📦 Loading return request {} from database", id);
        return returnRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return request not found: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequest> getUserReturnRequests(Users user) {
        return returnRequestRepository.findByUser(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequest> getReturnRequestsByOrder(Long orderId) {
        return returnRequestRepository.findByOrderId(orderId);
    }
    
    /**
     * ⚡ CACHED: Get all return requests with pagination
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "returnRequests", 
               key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize",
               unless = "#result == null")
    public Page<ReturnRequest> getAllReturnRequests(Pageable pageable) {
        log.info("📦 Loading return requests (page: {}, size: {})", 
                 pageable.getPageNumber(), pageable.getPageSize());
        return returnRequestRepository.findAllOrderByCreatedDateDesc(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReturnRequest> getReturnRequestsByStatus(ReturnStatus status, Pageable pageable) {
        return returnRequestRepository.findByStatus(status, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPendingRequests() {
        return returnRequestRepository.countPendingRequests();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByStatus(ReturnStatus status) {
        return returnRequestRepository.countByStatus(status);
    }

    @Override
    public ReturnRequest assignShipper(Long returnRequestId, Long shipperId) {
        log.info("Assigning shipper {} to return request {}", shipperId, returnRequestId);
        
        // 1. Validate return request exists
        ReturnRequest returnRequest = returnRequestRepository.findById(returnRequestId)
                .orElseThrow(() -> new RuntimeException("Return request not found: " + returnRequestId));
        
        // 2. Check status - only APPROVED can assign shipper
        if (returnRequest.getStatus() != ReturnStatus.APPROVED) {
            throw new RuntimeException("Chỉ có thể gán shipper cho yêu cầu đã được chấp nhận");
        }
        
        // 3. Validate shipper (user with role shipper) exists
        Users shipper = userRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found: " + shipperId));
        
        // Verify user is actually a shipper
        if (shipper.getRole() == null || !"shipper".equals(shipper.getRole().getRoleName())) {
            log.error("User {} has role: {}, expected 'shipper'", 
                     shipperId, 
                     shipper.getRole() != null ? shipper.getRole().getRoleName() : "NULL");
            throw new RuntimeException("User is not a shipper");
        }
        
        // 4. Create ReturnShipment for return pickup
        ReturnShipment returnShipment = ReturnShipment.builder()
                .returnRequest(returnRequest)
                .shipper(shipper)
                .pickupAddress(returnRequest.getOrder().getAddress())
                .status("PENDING")  // Waiting for shipper to pickup from customer
                .build();
        
        ReturnShipment savedReturnShipment = returnShipmentRepository.save(returnShipment);
        log.info("Created ReturnShipment {} for return request {}", savedReturnShipment.getId(), returnRequestId);
        
        // 5. Update return request status
        returnRequest.setStatus(ReturnStatus.SHIPPING);
        
        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        log.info("Shipper {} assigned to return request {}, ReturnShipment created, status changed to SHIPPING", 
                 shipperId, returnRequestId);
        
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCreateReturnRequest(Long orderId, Long userId) {
        // Check if order exists
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }
        
        // Check if order belongs to user
        if (!order.getUser().getId().equals(userId)) {
            return false;
        }
        
        // Check order status
        if (order.getStatus() != ShipmentStatus.DELIVERED) {
            return false;
        }
        
        // Check if return request already exists
        if (returnRequestRepository.existsByOrderId(orderId)) {
            return false;
        }
        
        // Check time limit (14 days from order creation)
        if (order.getCreatedDate() != null) {
            LocalDateTime createdDate = new java.sql.Timestamp(order.getCreatedDate().getTime()).toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            long daysSinceCreated = java.time.Duration.between(createdDate, now).toDays();
            if (daysSinceCreated > 14) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void cancelReturnRequest(Long returnId, Long userId) {
        log.info("Cancelling return request: {} by user: {}", returnId, userId);
        
        ReturnRequest returnRequest = getReturnRequestById(returnId);
        
        // Validate user owns this return request
        if (!returnRequest.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to cancel this return request");
        }
        
        // Validate can cancel
        if (!returnRequest.canCancel()) {
            throw new RuntimeException("Cannot cancel return request in status: " + returnRequest.getStatus());
        }
        
        // Delete return request
        returnRequestRepository.delete(returnRequest);
        log.info("Return request cancelled successfully: {}", returnId);
    }
}
