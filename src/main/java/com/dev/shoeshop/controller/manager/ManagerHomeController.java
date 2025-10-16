package com.dev.shoeshop.controller.manager;

import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.OrderService;
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
import java.util.Map;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerHomeController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;

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
        
        return "manager/order/order-detail";
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
    public String managerProductDetail(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "manager/product/product-detail";
    }
}
