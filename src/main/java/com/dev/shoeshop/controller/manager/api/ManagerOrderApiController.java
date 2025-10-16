package com.dev.shoeshop.controller.manager.api;

import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderStaticDTO;
import com.dev.shoeshop.dto.manager.OrderResponseDTO;
import com.dev.shoeshop.dto.manager.OrderStatisticsDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ManagerOrderApiController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            List<OrderDTO> orders;
            
            if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                ShipmentStatus shipmentStatus = ShipmentStatus.valueOf(status.toUpperCase());
                orders = orderService.getOrderByStatus(shipmentStatus);
            } else {
                orders = orderService.getAllOrders();
            }
            
            // Convert to ResponseDTO and apply pagination
            List<OrderResponseDTO> orderDTOs = orders.stream()
                    .map(this::convertFromOrderDTO)
                    .collect(Collectors.toList());
            
            // Manual pagination
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), orderDTOs.size());
            
            List<OrderResponseDTO> pageContent = orderDTOs.subList(start, end);
            Page<OrderResponseDTO> orderDTOPage = new PageImpl<>(pageContent, pageable, orderDTOs.size());
            
            return ResponseEntity.ok(orderDTOPage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<OrderStatisticsDTO> getOrderStatistics() {
        try {
            // Use the existing getStatic method
            OrderStaticDTO staticData = orderService.getStatic();
            
            // Convert to our DTO
            OrderStatisticsDTO statistics = OrderStatisticsDTO.builder()
                    .inStock(staticData.getInStock())
                    .shipping(staticData.getShipping())
                    .delivered(staticData.getDelivered())
                    .cancel(staticData.getCancel())
                    .preturn(staticData.getPreturn())
                    .total(staticData.getInStock() + staticData.getShipping() + 
                           staticData.getDelivered() + staticData.getCancel() + staticData.getPreturn())
                    .totalRevenue(0.0) // Not available in OrderStaticDTO
                    .build();

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.findById(id);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok("Hủy đơn hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi hủy đơn hàng: " + e.getMessage());
        }
    }

    private OrderResponseDTO convertFromOrderDTO(OrderDTO orderDTO) {
        return OrderResponseDTO.builder()
                .id(orderDTO.getId())
                .customerName(orderDTO.getUserName())
                .customerEmail(orderDTO.getUser() != null ? orderDTO.getUser().getEmail() : "")
                .customerPhone(orderDTO.getUser() != null ? orderDTO.getUser().getPhone() : "")
                .totalPrice(orderDTO.getTotalPrice())
                .createdDate(orderDTO.getCreatedDate())
                .status(orderDTO.getStatus())
                .payOption(orderDTO.getPayOption())
                .deliveryAddress(orderDTO.getUser() != null ? orderDTO.getUser().getAddress() : "")
                .itemCount(0) // Not available in OrderDTO
                .build();
    }
}
