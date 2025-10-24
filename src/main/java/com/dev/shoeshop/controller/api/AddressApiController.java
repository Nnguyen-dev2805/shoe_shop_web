package com.dev.shoeshop.controller.api;

import com.dev.shoeshop.dto.AddressDTO;
import com.dev.shoeshop.dto.AddressRequestDTO;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.AddressService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressApiController {
    
    private final AddressService addressService;
    
    /**
     * Get all addresses for current user
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserAddresses(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để xem địa chỉ.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<AddressDTO> addresses = addressService.getUserAddresses(user.getId());
            
            response.put("success", true);
            response.put("addresses", addresses);
            response.put("total", addresses.size());
            
            log.info("Retrieved {} addresses for user {}", addresses.size(), user.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting user addresses", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi tải danh sách địa chỉ.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Add new address
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addAddress(
            @Valid @RequestBody AddressRequestDTO addressRequest,
            BindingResult bindingResult,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để thêm địa chỉ.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Check validation errors
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getAllErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.joining(", "));
                
                response.put("success", false);
                response.put("message", errorMessage);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Add address
            AddressDTO savedAddress = addressService.addAddress(user.getId(), addressRequest);
            
            response.put("success", true);
            response.put("message", "Địa chỉ đã được thêm thành công!");
            response.put("address", savedAddress);
            
            log.info("Address added successfully for user {}", user.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error adding address", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi thêm địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Update existing address
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDTO addressRequest,
            BindingResult bindingResult,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để cập nhật địa chỉ.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Check validation errors
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getAllErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.joining(", "));
                
                response.put("success", false);
                response.put("message", errorMessage);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Update address
            AddressDTO updatedAddress = addressService.updateAddress(user.getId(), addressId, addressRequest);
            
            response.put("success", true);
            response.put("message", "Địa chỉ đã được cập nhật thành công!");
            response.put("address", updatedAddress);
            
            log.info("Address {} updated successfully for user {}", addressId, user.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error updating address", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi cập nhật địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Set default address
     */
    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
            @PathVariable Long addressId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            addressService.setDefaultAddress(user.getId(), addressId);
            
            response.put("success", true);
            response.put("message", "Đã đặt địa chỉ mặc định thành công!");
            
            log.info("Default address set for user {}", user.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error setting default address", e);
            response.put("success", false);
            response.put("message", "Có lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Delete address
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @PathVariable Long addressId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            addressService.deleteAddress(user.getId(), addressId);
            
            response.put("success", true);
            response.put("message", "Đã xóa địa chỉ thành công!");
            
            log.info("Address {} deleted for user {}", addressId, user.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error deleting address", e);
            response.put("success", false);
            response.put("message", "Có lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
