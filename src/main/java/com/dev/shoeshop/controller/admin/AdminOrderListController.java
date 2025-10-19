package com.dev.shoeshop.controller.admin;


import com.dev.shoeshop.converter.OrderDTOConverter;
import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderPaymentDTO;
import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Shipment;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.service.OrderDetailService;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderListController {


    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    OrderDTOConverter  orderDTOConverter;
    @Autowired
    ShipmentService shipmentService;




    // 1️⃣ Endpoint hiển thị giao diện
    @GetMapping("/list")
    public String listPage() {
        // map tới templates/admin/order/testList.html
        return "admin/order/testList";
    }


    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable("id") Long id){

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


        System.out.println("Hoangha");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail-page/{id}")
    public String orderDetailPage(@PathVariable("id") Long id) {

        return "admin/order/test"; // file templates/admin/order/order-detail.html
    }


    @GetMapping("/list-data")
    public ResponseEntity<?> GetAllOrders(
            @RequestParam(name = "status", defaultValue = "") String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdDate") String sortField,
            @RequestParam(name = "direction", defaultValue = "DESC") String sortDirection) {

        // Create Pageable with sorting
        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<OrderDTO> orderPage;
        
        if (status.isEmpty()) {
            // Get all orders with pagination
            orderPage = orderService.getAllOrdersWithPagination(pageable);
        } else {
            // Get orders by status with pagination
            orderPage = orderService.getOrderByStatusWithPagination(ShipmentStatus.valueOf(status), pageable);
        }

        // Build response with pagination metadata
        Map<String, Object> response = new HashMap<>();
        response.put("orderStatic", orderService.getStatic());
        response.put("orders", orderPage.getContent()); // List of orders
        response.put("currentPage", orderPage.getNumber());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());
        response.put("pageSize", orderPage.getSize());
        response.put("hasNext", orderPage.hasNext());
        response.put("hasPrevious", orderPage.hasPrevious());
        
        return ResponseEntity.ok(response);
    }
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(@RequestParam("orderId") Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderService.findById(orderId);

            // Kiểm tra trạng thái shipment (giả sử order có shipment đi kèm)

            if (order == null || order.getStatus() != ShipmentStatus.IN_STOCK) {
                response.put("success", false);
                response.put("message", "Chỉ những đơn hàng đang ở trạng thái IN_STOCK mới được hủy.");
                return ResponseEntity.badRequest().body(response);
            }

            // Nếu hợp lệ thì hủy
            orderService.cancelOrder(orderId);
            response.put("success", true);
            response.put("message", "Order " + orderId + " was canceled successfully");
            System.out.println("hoangha");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/shipping")
    public String addShipping(@RequestParam("orderid") Long orderid,
                              @RequestParam("userid") Long userid){
        shipmentService.insertShipment(orderid, userid);
        System.out.println("hoangha");
        return "redirect:/admin/order/detail-page/" + orderid;
    }


}
