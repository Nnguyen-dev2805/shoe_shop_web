package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.OrderPaymentDTO;

import java.util.List;

public interface OrderDetailService {
   public List<OrderDetailDTO> findAllOrderDetailById(Long id);
   public OrderPaymentDTO getOrderPayment(Long id);
}
