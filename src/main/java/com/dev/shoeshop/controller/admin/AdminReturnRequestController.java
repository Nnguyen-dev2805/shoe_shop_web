package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.entity.ReturnRequest;
import com.dev.shoeshop.entity.ReturnShipment;
import com.dev.shoeshop.entity.UserAddress;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ReturnStatus;
import com.dev.shoeshop.repository.ReturnShipmentRepository;
import com.dev.shoeshop.repository.UserAddressRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.EmailService;
import com.dev.shoeshop.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý Return Requests cho Admin
 */
@Controller
@RequestMapping("/admin/returns")
@RequiredArgsConstructor
@Slf4j
public class AdminReturnRequestController {
    
    private final ReturnRequestService returnRequestService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ReturnShipmentRepository returnShipmentRepository;
    private final UserAddressRepository userAddressRepository;
    
    /**
     * Trang danh sách return requests
     */
    @GetMapping
    public String listReturnRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReturnRequest> returnRequests;
        
        if (status != null && !status.isEmpty()) {
            try {
                ReturnStatus returnStatus = ReturnStatus.valueOf(status);
                returnRequests = returnRequestService.getReturnRequestsByStatus(returnStatus, pageable);
            } catch (IllegalArgumentException e) {
                returnRequests = returnRequestService.getAllReturnRequests(pageable);
            }
        } else {
            returnRequests = returnRequestService.getAllReturnRequests(pageable);
        }
        
        // Count by status for statistics
        long pendingCount = returnRequestService.countPendingRequests();
        long approvedCount = returnRequestService.countByStatus(ReturnStatus.APPROVED);
        long shippingCount = returnRequestService.countByStatus(ReturnStatus.SHIPPING);
        long receivedCount = returnRequestService.countByStatus(ReturnStatus.RECEIVED);
        long refundedCount = returnRequestService.countByStatus(ReturnStatus.REFUNDED);
        long rejectedCount = returnRequestService.countByStatus(ReturnStatus.REJECTED);
        
        model.addAttribute("returnRequests", returnRequests);
        model.addAttribute("currentStatus", status);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("shippingCount", shippingCount);
        model.addAttribute("receivedCount", receivedCount);
        model.addAttribute("refundedCount", refundedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", returnRequests.getTotalPages());
        
        return "admin/returns/list";
    }
    
    /**
     * Trang chi tiết return request
     */
    @GetMapping("/{id}")
    public String getReturnRequestDetail(@PathVariable Long id, Model model) {
        try {
            ReturnRequest returnRequest = returnRequestService.getReturnRequestById(id);
            model.addAttribute("returnRequest", returnRequest);
            
            // Get ReturnShipment if exists
            returnShipmentRepository.findByReturnRequestId(id)
                    .ifPresent(returnShipment -> model.addAttribute("returnShipment", returnShipment));
            
            // Get recipient info from UserAddress
            if (returnRequest.getOrder() != null && returnRequest.getOrder().getAddress() != null) {
                Long userId = returnRequest.getUser().getId();
                Long addressId = returnRequest.getOrder().getAddress().getId();
                
                userAddressRepository.findByUserIdAndAddressId(userId, addressId)
                        .ifPresent(userAddress -> {
                            model.addAttribute("recipientName", userAddress.getRecipientName());
                            model.addAttribute("recipientPhone", userAddress.getRecipientPhone());
                        });
            }
            
            return "admin/returns/detail";
        } catch (Exception e) {
            log.error("Error getting return request: {}", id, e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/returns";
        }
    }
    
    /**
     * API: Approve return request
     */
    @PostMapping("/{id}/approve")
    @ResponseBody
    public ResponseEntity<?> approveReturnRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String adminNote) {
        
        try {
            ReturnRequest updated = returnRequestService.approveReturnRequest(id, adminNote);
            
            // Send email notification to user
            try {
                emailService.sendReturnApprovedEmail(updated);
                log.info("Return approved email sent for request #{}", id);
            } catch (Exception e) {
                log.error("Failed to send return approved email for request #{}: {}", id, e.getMessage());
                // Don't fail the approval if email fails
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã chấp nhận yêu cầu trả hàng và gửi thông báo email");
            response.put("returnRequest", Map.of(
                "id", updated.getId(),
                "status", updated.getStatus(),
                "approvedDate", updated.getApprovedDate()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error approving return request: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * API: Reject return request
     */
    @PostMapping("/{id}/reject")
    @ResponseBody
    public ResponseEntity<?> rejectReturnRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String adminNote) {
        
        try {
            ReturnRequest updated = returnRequestService.rejectReturnRequest(id, adminNote);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã từ chối yêu cầu trả hàng");
            response.put("returnRequest", Map.of(
                "id", updated.getId(),
                "status", updated.getStatus()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error rejecting return request: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * API: Mark as received (Shop đã nhận hàng)
     */
    @PostMapping("/{id}/received")
    @ResponseBody
    public ResponseEntity<?> markAsReceived(
            @PathVariable Long id,
            @RequestParam(required = false) String adminNote) {
        
        try {
            ReturnRequest updated = returnRequestService.markAsReceived(id, adminNote);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xác nhận nhận hàng");
            response.put("returnRequest", Map.of(
                "id", updated.getId(),
                "status", updated.getStatus(),
                "receivedDate", updated.getReceivedDate()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking return request as received: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * API: Process refund (Hoàn xu)
     */
    @PostMapping("/{id}/refund")
    @ResponseBody
    public ResponseEntity<?> processRefund(
            @PathVariable Long id,
            @RequestParam BigDecimal refundAmount) {
        
        try {
            ReturnRequest updated = returnRequestService.processRefund(id, refundAmount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã hoàn xu thành công");
            response.put("returnRequest", Map.of(
                "id", updated.getId(),
                "status", updated.getStatus(),
                "refundAmount", updated.getRefundAmount(),
                "completedDate", updated.getCompletedDate()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing refund for return request: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * API: Assign shipper to pickup return
     */
    @PostMapping("/{id}/assign-shipper")
    @ResponseBody
    public ResponseEntity<?> assignShipper(
            @PathVariable Long id,
            @RequestParam Long shipperId) {
        
        try {
            ReturnRequest updated = returnRequestService.assignShipper(id, shipperId);
            
            // Get ReturnShipment info
            ReturnShipment returnShipment = returnShipmentRepository.findByReturnRequestId(id)
                    .orElseThrow(() -> new RuntimeException("ReturnShipment not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã gán shipper thành công");
            response.put("returnRequest", Map.of(
                "id", updated.getId(),
                "status", updated.getStatus(),
                "shipperId", returnShipment.getShipper().getId(),
                "shipperName", returnShipment.getShipper().getFullname()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error assigning shipper to return request: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * API: Get available shippers (users với role = SHIPPER)
     */
    @GetMapping("/api/shippers")
    @ResponseBody
    public ResponseEntity<?> getAvailableShippers() {
        try {
            // Lấy tất cả users có role = SHIPPER
            List<Users> shippers = userRepository.findAllShippers();
            
            List<Map<String, Object>> shipperList = shippers.stream()
                .map(shipper -> Map.of(
                    "id", (Object) shipper.getId(),
                    "name", shipper.getFullname() != null ? shipper.getFullname() : "Unknown",
                    "phone", shipper.getPhone() != null ? shipper.getPhone() : "",
                    "email", shipper.getEmail() != null ? shipper.getEmail() : ""
                ))
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "shippers", shipperList
            ));
        } catch (Exception e) {
            log.error("Error getting shippers: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * API: Get return request statistics
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<?> getStatistics() {
        try {
            long pendingCount = returnRequestService.countPendingRequests();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("pending", pendingCount);
            stats.put("success", true);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting return request statistics", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
