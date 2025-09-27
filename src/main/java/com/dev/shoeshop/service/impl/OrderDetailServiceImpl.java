package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderPaymentDTO;
import com.dev.shoeshop.entity.Order;
import com.dev.shoeshop.entity.OrderDetail;
import com.dev.shoeshop.repository.OrderDetailRepository;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    OrderRepository orderRepository;

    @Override
    public List<OrderDetailDTO> findAllOrderDetailById(Long id) {
        List<OrderDetail> optionalOrderDetail = orderDetailRepository.findOrderDetailsByOrderId(id);
        List<OrderDetailDTO> listDetailRes = new ArrayList<>();
        for (var item: optionalOrderDetail.stream().toList()){
            OrderDetailDTO detailDto = new OrderDetailDTO();
            //map data from OrderDetail to OrderDetailDTO
            detailDto.setSize(item.getProduct().getSize());
            detailDto.setProduct_name(item.getProduct().getProduct().getTitle());
            detailDto.setImage(item.getProduct().getProduct().getImage());
            detailDto.setPrice(item.getPrice());
            detailDto.setQuantity(item.getQuantity());
            detailDto.setAmount(item.getPrice() * item.getQuantity());

            listDetailRes.add(detailDto);
        }
        return listDetailRes;
    }

    @Override
    public OrderPaymentDTO getOrderPayment(Long id) {
        List<OrderDetail> optionalOrderDetail = orderDetailRepository.findOrderDetailsByOrderId(id);
        OrderPaymentDTO orderPaymentDto = new OrderPaymentDTO();
        double total = 0, discount = 0, payment = 0;
        for (var item: optionalOrderDetail.stream().toList()){
            total += (item.getProduct().getPriceadd() + item.getProduct().getProduct().getPrice()) * item.getQuantity();
        }
//        total += 5;
        Order order = orderRepository.findOrderById(id);
        payment = order.getTotalPrice();
        discount = total - payment;

        orderPaymentDto.setDiscount(discount);
        orderPaymentDto.setTotalpay(payment);
        orderPaymentDto.setSubtotal(total);
        return orderPaymentDto;
    }
}
