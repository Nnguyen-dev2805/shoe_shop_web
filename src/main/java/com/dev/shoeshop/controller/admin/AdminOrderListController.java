package com.dev.shoeshop.controller.admin;


import com.dev.shoeshop.entity.OrderEntity;
import com.dev.shoeshop.entity.OrderStaticDTO;
import com.dev.shoeshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderListController {


    @Autowired
    private OrderService orderService;


    // 1️⃣ Endpoint hiển thị giao diện
    @GetMapping("/list")
    public String listPage() {
        // map tới templates/admin/order/testList.html
        return "admin/order/testList";
    }


    @GetMapping("/list-data")
    public ResponseEntity<?> GetAllOrders() {

//        List<Order>

        Map<String, Object> response = new HashMap<>();
        response.put("orderStatic", orderService.getStatic());

        return ResponseEntity.ok(response);// sẽ map tới templates/manager/order/test.html
    }
}
