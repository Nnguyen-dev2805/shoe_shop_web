package com.dev.shoeshop.controller.shipper;

import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderPaymentDTO;
import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.UserAddress;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.UserAddressRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.OrderDetailService;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.ShipmentService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/shipper")
@RequiredArgsConstructor
public class ShipperController {

    private final OrderService orderService;
    private final ShipmentService shipmentService;
    private final OrderDetailService orderDetailService;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/order/list")
    public String orderList(
            @RequestParam(value = "page-num", defaultValue = "0") int pageNum,
            @RequestParam(value = "status", required = false) String status,
            Model model,
            HttpSession session
    ) {
        // Check if user is logged in
        Users loggedInUser = (Users) session.getAttribute(Constant.SESSION_USER);
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // Setup pagination (Sort đã có trong @Query)
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        
        Long shipperId = loggedInUser.getId();
        
        Page<Order> orderPage;
        
        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            try {
                ShipmentStatus shipmentStatus = ShipmentStatus.valueOf(status);
                orderPage = shipmentService.getOrdersByShipperIdAndStatus(shipperId, shipmentStatus, pageable);
            } catch (IllegalArgumentException e) {
                // If invalid status, get all orders of this shipper
                orderPage = shipmentService.getOrdersByShipperId(shipperId, pageable);
            }
        } else {
            // Lấy tất cả orders của shipper
            orderPage = shipmentService.getOrdersByShipperId(shipperId, pageable);
        }

        // Calculate statistics - Gọi Service thay vì Repository
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("cancel", shipmentService.countOrdersByShipperIdAndStatus(shipperId, ShipmentStatus.CANCEL));
        statistics.put("shipping", shipmentService.countOrdersByShipperIdAndStatus(shipperId, ShipmentStatus.SHIPPED));
        statistics.put("delivered", shipmentService.countOrdersByShipperIdAndStatus(shipperId, ShipmentStatus.DELIVERED));
        statistics.put("preturn", shipmentService.countOrdersByShipperIdAndStatus(shipperId, ShipmentStatus.RETURN));

        // Setup pagination
        int totalPages = orderPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("title", "Orders List");
        model.addAttribute("listOrder", orderPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("static", statistics);
        model.addAttribute("stt", status != null ? status : "");

        return "shipper/orders-list";
    }

    @GetMapping("/order/detail/{id}")
    public String orderDetail(@PathVariable("id") Long orderId, Model model, HttpSession session) {
        // Check if user is logged in
        Users loggedInUser = (Users) session.getAttribute(Constant.SESSION_USER);
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/shipper/order/list";
        }

        // Get order details
        List<OrderDetailDTO> orderDetails = orderDetailService.findAllOrderDetailById(orderId);
        
        // Get payment info
        OrderPaymentDTO payment = orderDetailService.getOrderPayment(orderId);
        
        // Get shipment info if exists
        ShipmentDTO shipment = null;
        try {
            shipment = shipmentService.findShipmentByOrderId(orderId);
        } catch (Exception e) {
            // Shipment not found, ignore
        }

        // Get recipient info from UserAddress
        UserAddress userAddress = null;
        String recipientName = null;
        String recipientPhone = null;
        
        if (order.getUser() != null && order.getAddress() != null) {
            userAddress = userAddressRepository.findByUserIdAndAddressId(
                order.getUser().getId(), 
                order.getAddress().getId()
            ).orElse(null);
            
            if (userAddress != null) {
                recipientName = userAddress.getRecipientName();
                recipientPhone = userAddress.getRecipientPhone();
            }
        }

        model.addAttribute("title", "Chi Tiết Đơn Hàng #" + orderId);
        model.addAttribute("order", order);
        model.addAttribute("list", orderDetails);
        model.addAttribute("payment", payment);
        model.addAttribute("shipper", shipment);
        model.addAttribute("user", order.getUser());
        model.addAttribute("recipientName", recipientName);
        model.addAttribute("recipientPhone", recipientPhone);

        return "shipper/order-detail";
    }
    
    /**
     * API: Check if order exists and belongs to this shipper
     */
    @GetMapping("/order/check/{orderId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkOrderExists(
            @PathVariable("orderId") Long orderId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users loggedInUser = (Users) session.getAttribute(Constant.SESSION_USER);
            if (loggedInUser == null) {
                response.put("exists", false);
                response.put("message", "Chưa đăng nhập");
                return ResponseEntity.ok(response);
            }
            
            Order order = orderService.findById(orderId);
            if (order == null) {
                response.put("exists", false);
                response.put("message", "Không tìm thấy đơn hàng");
                return ResponseEntity.ok(response);
            }
            
            // Check if this order belongs to this shipper
            ShipmentDTO shipment = shipmentService.findShipmentByOrderId(orderId);
            if (shipment != null && shipment.getShipperId().equals(loggedInUser.getId())) {
                response.put("exists", true);
                response.put("message", "Tìm thấy đơn hàng");
            } else {
                response.put("exists", false);
                response.put("message", "Đơn hàng không thuộc quyền quản lý của bạn");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("exists", false);
            response.put("message", "Không tìm thấy đơn hàng");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        // Check if user is logged in
        Users sessionUser = (Users) session.getAttribute(Constant.SESSION_USER);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Reload user from database to ensure all relationships are loaded
        Users user = userRepository.findById(sessionUser.getId()).orElse(sessionUser);
        
        // Load statistics for shipper
        Long shipperId = user.getId();
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("total", shipmentService.countOrdersByShipperId(shipperId));
        statistics.put("shipping", shipmentService.countOrdersByShipperIdAndStatus(shipperId, ShipmentStatus.SHIPPED));
        statistics.put("delivered", shipmentService.countOrdersByShipperIdAndStatus(shipperId, ShipmentStatus.DELIVERED));
        statistics.put("cancel", shipmentService.countOrdersByShipperIdAndStatus(shipperId, ShipmentStatus.CANCEL));
        statistics.put("return", shipmentService.countOrdersByShipperIdAndStatus(shipperId, ShipmentStatus.RETURN));

        model.addAttribute("title", "Hồ Sơ");
        model.addAttribute("user", user);
        model.addAttribute("statistics", statistics);

        return "shipper/shipper-profile";
    }
    
    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam("fullname") String fullname,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Phiên đăng nhập đã hết hạn");
                return ResponseEntity.ok(response);
            }

            // Cập nhật thông tin
            user.setFullname(fullname);
            user.setEmail(email);
            user.setPhone(phone);

            // Xử lý upload avatar nếu có
            if (avatar != null && !avatar.isEmpty()) {
                // TODO: Implement file upload logic
                // String avatarPath = fileUploadService.uploadFile(avatar);
                // user.setProfilePicture(avatarPath);
            }

            // Lưu vào database
            Users updatedUser = userRepository.save(user);
            
            // Cập nhật session
            session.setAttribute(Constant.SESSION_USER, updatedUser);

            response.put("success", true);
            response.put("message", "Cập nhật hồ sơ thành công");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile/change-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody Map<String, String> passwordData,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Phiên đăng nhập đã hết hạn");
                return ResponseEntity.ok(response);
            }

            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            // Kiểm tra mật khẩu hiện tại
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                response.put("success", false);
                response.put("message", "Mật khẩu hiện tại không đúng");
                return ResponseEntity.ok(response);
            }

            // Mã hóa mật khẩu mới
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedNewPassword);

            // Lưu vào database
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "Đổi mật khẩu thành công");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
    
    /**
     * API: Đánh dấu đơn hàng đã giao thành công
     */
    @GetMapping("/delivered")
    @ResponseBody
    public ResponseEntity<?> markAsDelivered(
            @RequestParam("orderid") Long orderId,
            HttpSession session
    ) {
        try {
            // Check authentication
            Users loggedInUser = (Users) session.getAttribute(Constant.SESSION_USER);
            if (loggedInUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Chưa đăng nhập"));
            }
            
            // Find order
            Order order = orderService.findById(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Không tìm thấy đơn hàng"));
            }
            
            // MVC: Gọi OrderService để update status
            orderService.markOrderAsDelivered(orderId);
            
            // Update shipment status và update date
            shipmentService.updateShipmentStatusAndDate(orderId, ShipmentStatus.DELIVERED);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã cập nhật trạng thái đơn hàng thành DELIVERED",
                "newStatus", "DELIVERED"
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }
    
    /**
     * API: Đánh dấu đơn hàng bị hoàn trả
     */
    @GetMapping("/return")
    @ResponseBody
    public ResponseEntity<?> markAsReturn(
            @RequestParam("orderid") Long orderId,
            HttpSession session
    ) {
        try {
            // Check authentication
            Users loggedInUser = (Users) session.getAttribute(Constant.SESSION_USER);
            if (loggedInUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Chưa đăng nhập"));
            }
            
            // Find order
            Order order = orderService.findById(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Không tìm thấy đơn hàng"));
            }
            
            // MVC: Gọi OrderService để update status
            orderService.markOrderAsReturn(orderId);
            
            // Update shipment status và update date
            shipmentService.updateShipmentStatusAndDate(orderId, ShipmentStatus.RETURN);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã cập nhật trạng thái đơn hàng thành RETURN",
                "newStatus", "RETURN"
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }
    
    /**
     * API: Lưu ghi chú của shipper
     */
    @PostMapping("/note")
    @ResponseBody
    public ResponseEntity<?> saveNote(
            @RequestParam("shipmentId") Long shipmentId,
            @RequestParam("note") String note,
            HttpSession session
    ) {
        try {
            // Check authentication
            Users loggedInUser = (Users) session.getAttribute(Constant.SESSION_USER);
            if (loggedInUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Chưa đăng nhập"));
            }
            
            // Save note
            shipmentService.saveNote(shipmentId, note);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã lưu ghi chú thành công"
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
