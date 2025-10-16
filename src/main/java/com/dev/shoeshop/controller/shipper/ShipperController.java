package com.dev.shoeshop.controller.shipper;

import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderPaymentDTO;
import com.dev.shoeshop.dto.ShipmentDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.OrderRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/shipper")
@RequiredArgsConstructor
public class ShipperController {

    private final OrderRepository orderRepository;
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

        // Setup pagination
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdDate").descending());
        
        Page<Order> orderPage;
        
        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            try {
                ShipmentStatus shipmentStatus = ShipmentStatus.valueOf(status);
                orderPage = orderRepository.findByStatus(shipmentStatus, pageable);
            } catch (IllegalArgumentException e) {
                // If invalid status, get all orders
                orderPage = orderRepository.findAll(pageable);
            }
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        // Calculate statistics
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("cancel", orderRepository.countByStatus(ShipmentStatus.CANCEL));
        statistics.put("shipping", orderRepository.countByStatus(ShipmentStatus.SHIPPED));
        statistics.put("delivered", orderRepository.countByStatus(ShipmentStatus.DELIVERED));
        statistics.put("preturn", orderRepository.countByStatus(ShipmentStatus.RETURN));

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
}
