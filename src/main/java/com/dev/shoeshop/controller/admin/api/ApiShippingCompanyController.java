package com.dev.shoeshop.controller.admin.api;

import com.dev.shoeshop.dto.shippingcompany.ShippingCompanyCreateRequest;
import com.dev.shoeshop.dto.shippingcompany.ShippingCompanyResponse;
import com.dev.shoeshop.dto.shippingcompany.ShippingCompanyUpdateRequest;
import com.dev.shoeshop.entity.ShippingCompany;
import com.dev.shoeshop.mapper.ShippingCompanyMapper;
import com.dev.shoeshop.service.ShippingCompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping-company")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ApiShippingCompanyController {
    
    private final ShippingCompanyService shippingCompanyService;
    private final ShippingCompanyMapper shippingCompanyMapper;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllShippingCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive) {
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<ShippingCompany> shippingCompanies;
            
            if (search != null && !search.trim().isEmpty() && isActive != null) {
                // Tìm kiếm theo tên và trạng thái
                shippingCompanies = shippingCompanyService.searchByNameAndIsActive(search.trim(), isActive, pageable);
            } else if (search != null && !search.trim().isEmpty()) {
                // Tìm kiếm theo tên
                shippingCompanies = shippingCompanyService.searchByName(search.trim(), pageable);
            } else if (isActive != null) {
                // Lọc theo trạng thái
                shippingCompanies = shippingCompanyService.getByIsActive(isActive, pageable);
            } else {
                // Lấy tất cả
                shippingCompanies = shippingCompanyService.getAllShippingCompanies(pageable);
            }
            
            List<ShippingCompanyResponse> responseList = shippingCompanyMapper.toResponseList(shippingCompanies.getContent());
            
            Map<String, Object> response = new HashMap<>();
            response.put("shippingCompanies", responseList);
            response.put("currentPage", shippingCompanies.getNumber());
            response.put("totalPages", shippingCompanies.getTotalPages());
            response.put("totalElements", shippingCompanies.getTotalElements());
            response.put("hasNext", shippingCompanies.hasNext());
            response.put("hasPrevious", shippingCompanies.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting shipping companies: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Có lỗi xảy ra khi lấy danh sách công ty vận chuyển");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getShippingCompanyById(@PathVariable Integer id) {
        try {
            ShippingCompany shippingCompany = shippingCompanyService.getShippingCompanyById(id)
                    .orElse(null);
            
            if (shippingCompany == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Không tìm thấy công ty vận chuyển");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            ShippingCompanyResponse response = shippingCompanyMapper.toResponse(shippingCompany);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("shippingCompany", response);
            
            return ResponseEntity.ok(successResponse);
            
        } catch (Exception e) {
            log.error("Error getting shipping company by id {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Có lỗi xảy ra khi lấy thông tin công ty vận chuyển");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createShippingCompany(
            @Valid @RequestBody ShippingCompanyCreateRequest request,
            BindingResult bindingResult) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", "Dữ liệu không hợp lệ");
            response.put("errors", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Kiểm tra tên đã tồn tại chưa
            if (shippingCompanyService.existsByName(request.getName())) {
                response.put("success", false);
                response.put("message", "Tên công ty vận chuyển đã tồn tại");
                return ResponseEntity.badRequest().body(response);
            }
            
            ShippingCompany shippingCompany = shippingCompanyMapper.toEntity(request);
            ShippingCompany savedShippingCompany = shippingCompanyService.saveShippingCompany(shippingCompany);
            ShippingCompanyResponse shippingCompanyResponse = shippingCompanyMapper.toResponse(savedShippingCompany);
            
            response.put("success", true);
            response.put("message", "Thêm công ty vận chuyển thành công");
            response.put("shippingCompany", shippingCompanyResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error creating shipping company: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi thêm công ty vận chuyển");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateShippingCompany(
            @PathVariable Integer id,
            @Valid @RequestBody ShippingCompanyUpdateRequest request,
            BindingResult bindingResult) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", "Dữ liệu không hợp lệ");
            response.put("errors", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            ShippingCompany existingShippingCompany = shippingCompanyService.getShippingCompanyById(id)
                    .orElse(null);
            
            if (existingShippingCompany == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy công ty vận chuyển");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Kiểm tra tên đã tồn tại chưa (trừ ID hiện tại)
            if (shippingCompanyService.existsByNameAndIdNot(request.getName(), id)) {
                response.put("success", false);
                response.put("message", "Tên công ty vận chuyển đã tồn tại");
                return ResponseEntity.badRequest().body(response);
            }
            
            shippingCompanyMapper.updateEntity(existingShippingCompany, request);
            ShippingCompany savedShippingCompany = shippingCompanyService.saveShippingCompany(existingShippingCompany);
            ShippingCompanyResponse shippingCompanyResponse = shippingCompanyMapper.toResponse(savedShippingCompany);
            
            response.put("success", true);
            response.put("message", "Cập nhật công ty vận chuyển thành công");
            response.put("shippingCompany", shippingCompanyResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error updating shipping company: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi cập nhật công ty vận chuyển");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteShippingCompany(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ShippingCompany shippingCompany = shippingCompanyService.getShippingCompanyById(id)
                    .orElse(null);
            
            if (shippingCompany == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy công ty vận chuyển");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            shippingCompanyService.deleteShippingCompany(id);
            
            response.put("success", true);
            response.put("message", "Xóa công ty vận chuyển thành công");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error deleting shipping company: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi xóa công ty vận chuyển");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getShippingCompanyStats() {
        try {
            long totalCount = shippingCompanyService.countAllShippingCompanies();
            long activeCount = shippingCompanyService.countByIsActive(true);
            long inactiveCount = shippingCompanyService.countByIsActive(false);
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalCount", totalCount);
            response.put("activeCount", activeCount);
            response.put("inactiveCount", inactiveCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting shipping company stats: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Có lỗi xảy ra khi lấy thống kê công ty vận chuyển");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
