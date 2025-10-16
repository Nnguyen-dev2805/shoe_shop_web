package com.dev.shoeshop.controller.user;

import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.UserService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User Home Controller - Handles view rendering only
 * API endpoints are in ApiHomeController (/api/shop/*)
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserHomeController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/shop")
    public String userShop(HttpSession session) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
            return "user/shop";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/blog")
    public String blog(){
        return "/user/blog";
    }

    @GetMapping("/my_account")
    public String myAccount(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
//            List<Long> viewedProductIds = (List<Long>) session.getAttribute(Constant.VIEW_PRODUCT);
//            if (viewedProductIds == null || viewedProductIds.isEmpty()) {
//                redirectAttributes.addFlashAttribute("message", "No products viewed yet.");
//            }else {
//                // Lọc ra các sản phẩm hợp lệ từ danh sách ID đã xem và loại bỏ sản phẩm đã bị xóa
//                List<Product> viewedProducts = productService.getProductsByIds(viewedProductIds);
//                // Lọc các sản phẩm đã bị xóa (isDelete == true) và loại bỏ chúng khỏi danh sách
//                viewedProducts = viewedProducts.stream()
//                        .filter(product -> product != null && !product.isDelete())  // Loại bỏ sản phẩm null và đã xóa
//                        .collect(Collectors.toList());
//
//                // Cập nhật lại sản phẩm đã xem trong session
//                viewedProductIds = viewedProducts.stream()
//                        .map(Product::getId)  // Lấy ID của các sản phẩm còn lại
//                        .collect(Collectors.toList());
//
//                // Lưu lại danh sách các sản phẩm hợp lệ vào session
//                session.setAttribute(Constant.VIEW_PRODUCT, viewedProductIds);
//                model.addAttribute("viewedProducts", viewedProducts);
//            }
//            // Lấy thông tin sản phẩm từ danh sách ID đã xem
//            List<Product> viewedProducts = productService.getProductsByIds(viewedProductIds);
//            model.addAttribute("viewedProducts", viewedProducts);
//            model.addAttribute("user", u);
//            List<Address> adr = addressService.getAddressesByID(u.getId());
//            model.addAttribute("adr", adr);
            return "user/my-account";
        } else {
            return "redirect:/login";
        }
    }



    @GetMapping("/shipper/all")
    public ResponseEntity<?> getAllShipper(){
        List<UserDTO> listShipper = userService.getAllShipper(4L);
        Map<String, Object> response = new HashMap<>();

        response.put("listShipper", listShipper);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shipper/search")
    public ResponseEntity<?> getShipperByName(@RequestParam(value = "name") String name){
        List<UserDTO> listShipper = userService.findByFullnameAndRole(name,4L);
        Map<String, Object> response = new HashMap<>();

        response.put("listShipper", listShipper);
        return ResponseEntity.ok(response);
    }
    
    // Trang xem đơn hàng theo trạng thái
    @GetMapping("/order/view")
    public String orderView(HttpSession session, Model model) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
            model.addAttribute("user", u);
            return "user/order-view";
        } else {
            return "redirect:/login";
        }
    }
    
    // Trang chi tiết đơn hàng
    @GetMapping("/order-detail/{orderId}")
    public String orderDetail(@PathVariable Long orderId, HttpSession session, Model model) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
            try {
                OrderDTO orderDetail = orderService.getOrderDetailById(orderId, u.getId());
                model.addAttribute("order", orderDetail);
                model.addAttribute("user", u);
                return "user/order-detail";
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                return "user/order-detail";
            }
        } else {
            return "redirect:/login";
        }
    }
    
    
    // API endpoint để lấy orders theo status
    @GetMapping("/api/orders/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status, HttpSession session) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not logged in"));
        }
        
        try {
            ShipmentStatus shipmentStatus = ShipmentStatus.valueOf(status.toUpperCase());
            List<OrderDTO> orders = orderService.getOrdersByUserIdAndStatus(user.getId(), shipmentStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid status: " + status);
            response.put("success", false);
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to load orders: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(500).body(response);
        }
    }

}
