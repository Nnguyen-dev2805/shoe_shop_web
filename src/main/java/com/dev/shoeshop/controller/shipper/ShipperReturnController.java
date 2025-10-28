package com.dev.shoeshop.controller.shipper;

import com.dev.shoeshop.entity.ReturnShipment;
import com.dev.shoeshop.entity.UserAddress;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.ReturnShipmentRepository;
import com.dev.shoeshop.repository.UserAddressRepository;
import com.dev.shoeshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Shipper Return Pickup Management
 */
@Controller
@RequestMapping("/shipper/returns")
@RequiredArgsConstructor
@Slf4j
public class ShipperReturnController {
    
    private final ReturnShipmentRepository returnShipmentRepository;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    
    /**
     * Dashboard - Danh sách return pickups
     */
    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            Users shipper = userRepository.findByEmail(email);
            
            if (shipper == null) {
                return "redirect:/login";
            }
            
            // Get all return shipments for this shipper
            List<ReturnShipment> returnShipments = returnShipmentRepository.findByShipperId(shipper.getId());
            
            // Add recipient info for each shipment
            for (ReturnShipment rs : returnShipments) {
                if (rs.getReturnRequest().getOrder() != null && rs.getReturnRequest().getOrder().getAddress() != null) {
                    Long userId = rs.getReturnRequest().getUser().getId();
                    Long addressId = rs.getReturnRequest().getOrder().getAddress().getId();
                    
                    userAddressRepository.findByUserIdAndAddressId(userId, addressId)
                            .ifPresent(userAddress -> {
                                rs.getReturnRequest().getUser().setFullname(userAddress.getRecipientName());
                                rs.getReturnRequest().getUser().setPhone(userAddress.getRecipientPhone());
                            });
                }
            }
            
            // Count by status
            long pendingCount = returnShipments.stream()
                    .filter(rs -> "PENDING".equals(rs.getStatus()))
                    .count();
            long pickedUpCount = returnShipments.stream()
                    .filter(rs -> "PICKED_UP".equals(rs.getStatus()))
                    .count();
            long deliveredCount = returnShipments.stream()
                    .filter(rs -> "DELIVERED_TO_WAREHOUSE".equals(rs.getStatus()))
                    .count();
            
            model.addAttribute("returnShipments", returnShipments);
            model.addAttribute("shipper", shipper);
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("pickedUpCount", pickedUpCount);
            model.addAttribute("deliveredCount", deliveredCount);
            
            return "shipper/returns/dashboard";
        } catch (Exception e) {
            log.error("Error loading shipper dashboard", e);
            return "redirect:/";
        }
    }
    
    /**
     * Chi tiết return pickup
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            Users shipper = userRepository.findByEmail(email);
            
            ReturnShipment returnShipment = returnShipmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Return shipment not found"));
            
            // Verify this shipment belongs to current shipper
            if (!returnShipment.getShipper().getId().equals(shipper.getId())) {
                return "redirect:/shipper/returns?error=unauthorized";
            }
            
            // Get recipient info from UserAddress
            if (returnShipment.getReturnRequest().getOrder() != null && returnShipment.getReturnRequest().getOrder().getAddress() != null) {
                Long userId = returnShipment.getReturnRequest().getUser().getId();
                Long addressId = returnShipment.getReturnRequest().getOrder().getAddress().getId();
                
                userAddressRepository.findByUserIdAndAddressId(userId, addressId)
                        .ifPresent(userAddress -> {
                            model.addAttribute("recipientName", userAddress.getRecipientName());
                            model.addAttribute("recipientPhone", userAddress.getRecipientPhone());
                        });
            }
            
            model.addAttribute("returnShipment", returnShipment);
            model.addAttribute("shipper", shipper);
            
            return "shipper/returns/detail";
        } catch (Exception e) {
            log.error("Error loading return shipment detail: {}", id, e);
            return "redirect:/shipper/returns";
        }
    }
    
    /**
     * API: Xác nhận đã lấy hàng
     */
    @PostMapping("/{id}/pickup")
    @ResponseBody
    public ResponseEntity<?> confirmPickup(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName();
            Users shipper = userRepository.findByEmail(email);
            
            ReturnShipment returnShipment = returnShipmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Return shipment not found"));
            
            // Verify ownership
            if (!returnShipment.getShipper().getId().equals(shipper.getId())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Unauthorized"
                ));
            }
            
            // Update status
            returnShipment.setStatus("PICKED_UP");
            returnShipment.setPickupDate(new Date());
            returnShipmentRepository.save(returnShipment);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xác nhận lấy hàng thành công"
            ));
        } catch (Exception e) {
            log.error("Error confirming pickup: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * API: Xác nhận đã giao về kho
     */
    @PostMapping("/{id}/deliver")
    @ResponseBody
    public ResponseEntity<?> confirmDelivery(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName();
            Users shipper = userRepository.findByEmail(email);
            
            ReturnShipment returnShipment = returnShipmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Return shipment not found"));
            
            // Verify ownership
            if (!returnShipment.getShipper().getId().equals(shipper.getId())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Unauthorized"
                ));
            }
            
            // Update status
            returnShipment.setStatus("DELIVERED_TO_WAREHOUSE");
            returnShipment.setDeliveryDate(new Date());
            returnShipmentRepository.save(returnShipment);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xác nhận giao về kho thành công"
            ));
        } catch (Exception e) {
            log.error("Error confirming delivery: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
