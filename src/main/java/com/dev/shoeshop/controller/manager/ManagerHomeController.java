package com.dev.shoeshop.controller.manager;

import com.dev.shoeshop.converter.OrderDTOConverter;
import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderPaymentDTO;
import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.OrderDetailService;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.ShipmentService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerHomeController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final ShipmentService shipmentService;
    private final OrderDTOConverter orderDTOConverter;

    @GetMapping
    public String managerHome(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Kiểm tra role manager
        if (!"manager".equalsIgnoreCase(user.getRole().getRoleName())) {
            return "redirect:/access-denied";
        }
        
        model.addAttribute("user", user);
        return "manager/manager_home";
    }

    @GetMapping("/profile")
    public String managerProfile(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("title", "Hồ Sơ");
        return "manager/pages-profile";
    }

    @GetMapping("/orders")
    public String managerOrders(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "manager/order/orders-list";
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

    @GetMapping("/orders/{id}")
    public String managerOrderDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Load order from database
            Order order = orderService.findById(id);
            
            if (order == null) {
                model.addAttribute("error", "Không tìm thấy đơn hàng");
                return "redirect:/manager/orders";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("order", order);
            model.addAttribute("title", "Chi Tiết Đơn Hàng #" + id);
            
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải đơn hàng: " + e.getMessage());
            return "redirect:/manager/orders";
        }
        
        return "manager/order/manager-order-detail-full";
    }
    
    @GetMapping("/order/detail/{id}")
    @ResponseBody
    public ResponseEntity<?> getManagerOrderDetail(@PathVariable("id") Long id) {
        List<OrderDetailDTO> list = orderDetailService.findAllOrderDetailById(id);
        Map<String, Object> response = new HashMap<>();

        response.put("listOrderDetail", list);

        OrderPaymentDTO orderPaymentDto = orderDetailService.getOrderPayment(id);
        response.put("orderPayment", orderPaymentDto);

        Order order = orderService.findById(id);

        // Map sang DTO thay vì trả entity
        OrderDTO orderDTO = orderDTOConverter.toOrderDTO(order);
        response.put("order", orderDTO);

        ShipmentDTO shipment = shipmentService.findShipmentByOrderId(id);
        response.put("shipment", shipment);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/order/shipping")
    public String managerAddShipping(@RequestParam("orderid") Long orderid,
                                     @RequestParam("userid") Long userid) {
        shipmentService.insertShipment(orderid, userid);
        return "redirect:/manager/orders/" + orderid;
    }
    
    @PostMapping("/order/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> managerCancelOrder(@RequestParam("orderId") Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderService.findById(orderId);

            if (order == null || order.getStatus() != ShipmentStatus.IN_STOCK) {
                response.put("success", false);
                response.put("message", "Chỉ những đơn hàng đang ở trạng thái IN_STOCK mới được hủy.");
                return ResponseEntity.badRequest().body(response);
            }

            // Hủy đơn hàng
            orderService.cancelOrder(orderId);
            response.put("success", true);
            response.put("message", "Đơn hàng " + orderId + " đã được hủy thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/products")
    public String managerProducts(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "manager/product/product-list";
    }

    @GetMapping("/products/{id}")
    public String managerProductDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Kiểm tra role manager
        if (!"manager".equalsIgnoreCase(user.getRole().getRoleName())) {
            return "redirect:/access-denied";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("productId", id);
        return "manager/product/product-detail";
    }
}
