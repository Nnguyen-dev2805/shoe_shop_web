package com.dev.shoeshop.controller.user;

import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.entity.ReturnRequest;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ReturnReason;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.service.CloudinaryService;
import com.dev.shoeshop.service.ReturnRequestService;
import com.dev.shoeshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý Return Requests cho User
 */
@Controller
@RequestMapping("/user/returns")
@RequiredArgsConstructor
@Slf4j
public class UserReturnRequestController {
    
    private final ReturnRequestService returnRequestService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final OrderRepository orderRepository;
    
    /**
     * API: Get order detail for return request (User only sees their own orders)
     */
    @GetMapping("/api/order/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getOrderForReturn(
            @PathVariable Long orderId,
            HttpSession session) {
        
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập"
                ));
            }
            
            // Get real order from database
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            // Validate order belongs to current user
            if (!order.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền xem đơn hàng này"
                ));
            }
            
            // Convert OrderDetail to response format
            List<Map<String, Object>> products = new java.util.ArrayList<>();
            
            if (order.getOrderDetailSet() != null && !order.getOrderDetailSet().isEmpty()) {
                for (OrderDetail detail : order.getOrderDetailSet()) {
                    Map<String, Object> productMap = new HashMap<>();
                    
                    // Get ProductDetail
                    ProductDetail productDetail = detail.getProduct();
                    if (productDetail == null) {
                        continue; // Skip if no product detail
                    }
                    
                    // Get Product from ProductDetail
                    Product product = productDetail.getProduct();
                    
                    // Get product image
                    String image = "/img/product/default.jpg"; // fallback
                    if (product != null && product.getImage() != null && !product.getImage().isEmpty()) {
                        image = product.getImage();
                    }
                    productMap.put("image", image);
                    
                    // Get product name (field is 'title' not 'name')
                    String productName = "Sản phẩm";
                    if (product != null && product.getTitle() != null) {
                        productName = product.getTitle();
                    }
                    productMap.put("product_name", productName);
                    
                    // Get size (from ProductDetail)
                    String sizeName = "N/A";
                    if (productDetail.getSize() != null) {
                        sizeName = String.valueOf(productDetail.getSize());
                    }
                    productMap.put("size", sizeName);
                    
                    // Get quantity and price
                    productMap.put("quantity", detail.getQuantity());
                    productMap.put("price", detail.getPrice());
                    
                    products.add(productMap);
                    
                    log.info("Product added: {} - Size {} - Price {}", productName, sizeName, detail.getPrice());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("listOrderDetail", products);
            
            log.info("Returning {} products for order {}", products.size(), orderId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting order for return: {}", orderId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Trang danh sách return requests của user
     */
    @GetMapping
    public String myReturnRequests(HttpSession session, Model model) {
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/user/login";
            }
            
            List<ReturnRequest> returnRequests = returnRequestService.getUserReturnRequests(currentUser);
            model.addAttribute("returnRequests", returnRequests);
            
            return "user/returns/list";
        } catch (Exception e) {
            log.error("Error loading return requests", e);
            model.addAttribute("error", e.getMessage());
            return "user/returns/list";
        }
    }
    
    /**
     * Trang tạo return request mới
     */
    @GetMapping("/create")
    public String showCreateForm(
            @RequestParam Long orderId,
            HttpSession session,
            Model model) {
        
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/user/login";
            }
            
            // Check if can create return request
            boolean canCreate = returnRequestService.canCreateReturnRequest(orderId, currentUser.getId());
            if (!canCreate) {
                model.addAttribute("error", "Không thể tạo yêu cầu trả hàng cho đơn này");
                return "redirect:/user/orders";
            }
            
            model.addAttribute("orderId", orderId);
            model.addAttribute("reasons", ReturnReason.values());
            
            return "user/returns/create";
        } catch (Exception e) {
            log.error("Error showing create return form", e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/user/orders";
        }
    }
    
    /**
     * API: Tạo return request mới (với file upload lên Cloudinary)
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createReturnRequest(
            @RequestParam Long orderId,
            @RequestParam String reason,
            @RequestParam(required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            HttpSession session) {
        
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập"
                ));
            }
            
            // Validate description
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Vui lòng nhập mô tả"
                ));
            }
            
            // Validate image
            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Vui lòng tải lên ảnh sản phẩm"
                ));
            }
            
            // Validate file size (5MB)
            if (imageFile.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Ảnh quá lớn (tối đa 5MB)"
                ));
            }
            
            // Validate file type
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File không phải là ảnh"
                ));
            }
            
            log.info("Uploading return image to Cloudinary: {}", imageFile.getOriginalFilename());
            
            // Upload image to Cloudinary
            String imageUrl;
            try {
                imageUrl = cloudinaryService.uploadImage(imageFile, CloudinaryService.RETURN_FOLDER);
                log.info("Image uploaded successfully to Cloudinary: {}", imageUrl);
            } catch (Exception e) {
                log.error("Error uploading image to Cloudinary", e);
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi upload ảnh: " + e.getMessage()
                ));
            }
            
            ReturnReason returnReason = ReturnReason.valueOf(reason);
            
            ReturnRequest created = returnRequestService.createReturnRequest(
                orderId,
                currentUser.getId(),
                returnReason,
                description.trim(),
                imageUrl
            );
            
            log.info("Return request created successfully: {} by user: {} with image: {}", 
                    created.getId(), currentUser.getId(), imageUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo yêu cầu trả hàng thành công");
            response.put("returnRequestId", created.getId());
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid return reason", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lý do trả hàng không hợp lệ"
            ));
        } catch (Exception e) {
            log.error("Error creating return request", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Trang chi tiết return request
     */
    @GetMapping("/{id}")
    public String getReturnRequestDetail(
            @PathVariable Long id,
            HttpSession session,
            Model model) {
        
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/user/login";
            }
            
            ReturnRequest returnRequest = returnRequestService.getReturnRequestById(id);
            
            // Validate ownership
            if (!returnRequest.getUser().getId().equals(currentUser.getId())) {
                model.addAttribute("error", "Bạn không có quyền xem yêu cầu này");
                return "redirect:/user/returns";
            }
            
            model.addAttribute("returnRequest", returnRequest);
            return "user/returns/detail";
        } catch (Exception e) {
            log.error("Error getting return request detail: {}", id, e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/user/returns";
        }
    }
    
    /**
     * API: User cập nhật tracking code khi gửi hàng
     */
    @PostMapping("/{id}/tracking")
    @ResponseBody
    public ResponseEntity<?> updateTrackingCode(
            @PathVariable Long id,
            @RequestParam String trackingCode,
            HttpSession session) {
        
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập"
                ));
            }
            
            // Validate ownership
            ReturnRequest returnRequest = returnRequestService.getReturnRequestById(id);
            if (!returnRequest.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền cập nhật yêu cầu này"
                ));
            }
            
            ReturnRequest updated = returnRequestService.updateTrackingCode(id, trackingCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật mã vận đơn thành công");
            response.put("returnRequest", Map.of(
                "id", updated.getId(),
                "status", updated.getStatus(),
                "trackingCode", updated.getShippingTrackingCode()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating tracking code for return request: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * API: Hủy return request (chỉ khi PENDING)
     */
    @PostMapping("/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelReturnRequest(
            @PathVariable Long id,
            HttpSession session) {
        
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập"
                ));
            }
            
            returnRequestService.cancelReturnRequest(id, currentUser.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã hủy yêu cầu trả hàng"
            ));
        } catch (Exception e) {
            log.error("Error cancelling return request: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * API: Check if order can create return request
     */
    @GetMapping("/api/can-return")
    @ResponseBody
    public ResponseEntity<?> canReturnOrder(
            @RequestParam Long orderId,
            HttpSession session) {
        
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "canReturn", false,
                    "message", "Vui lòng đăng nhập"
                ));
            }
            
            boolean canReturn = returnRequestService.canCreateReturnRequest(orderId, currentUser.getId());
            
            return ResponseEntity.ok(Map.of(
                "canReturn", canReturn,
                "message", canReturn ? "Có thể trả hàng" : "Không thể trả hàng"
            ));
        } catch (Exception e) {
            log.error("Error checking if can return order: {}", orderId, e);
            return ResponseEntity.ok(Map.of(
                "canReturn", false,
                "message", e.getMessage()
            ));
        }
    }
}
