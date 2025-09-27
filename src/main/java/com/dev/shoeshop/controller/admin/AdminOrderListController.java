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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<?> GetAllOrders(@RequestParam(name = "status", defaultValue = "") String status) {

//        List<Order>

        List<OrderDTO> orders = new ArrayList<>();
        if (status.isEmpty()) {
            orders = orderService.getAllOrders();
        }
        else{
            orders = orderService.getOrderByStatus(ShipmentStatus.valueOf(status));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orderStatic", orderService.getStatic());
        response.put("orders", orders);
        System.out.println("hoangha");
        return ResponseEntity.ok(response);// sẽ map tới templates/manager/order/test.html
    }
    
    
    

}
