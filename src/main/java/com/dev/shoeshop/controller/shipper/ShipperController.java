package com.dev.shoeshop.controller.shipper;

import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderPaymentDTO;
import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.service.OrderDetailService;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.ShipmentService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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

        model.addAttribute("title", "Order Detail #" + orderId);
        model.addAttribute("order", order);
        model.addAttribute("list", orderDetails);
        model.addAttribute("payment", payment);
        model.addAttribute("shipper", shipment);
        model.addAttribute("user", order.getUser());

        return "shipper/order-detail";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        // Check if user is logged in
        Users loggedInUser = (Users) session.getAttribute(Constant.SESSION_USER);
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("title", "Profile");
        model.addAttribute("user", loggedInUser);

        return "shipper/pages-profile";
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
