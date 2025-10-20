package com.dev.shoeshop.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("userPaymentController")
public class PaymentController {
    
    @GetMapping("/user/payment/qr")
    public String showPaymentQR(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String amount,
            @RequestParam(required = false) String qrCode,
            Model model) {
        
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        model.addAttribute("qrCode", qrCode);
        
        return "user/payment-qr";
    }
}
