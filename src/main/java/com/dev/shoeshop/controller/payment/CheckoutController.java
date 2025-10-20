package com.dev.shoeshop.controller.payment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.io.IOException;
@Controller
public class CheckoutController {
  private final PayOS payOS;

  public CheckoutController(PayOS payOS) {
    super();
    this.payOS = payOS;
  }

  @RequestMapping(value = "/")
  public String Index() {
    return "index";
  }

  @RequestMapping(value = "/success")
  public String Success() {
    return "payment/success";
  }

  @RequestMapping(value = "/cancel")
  public String Cancel() {
    return "payment/cancel";
  }

  @GetMapping(value = "/create-payment-link")
  public void handleGetRequest(HttpServletResponse response) throws IOException {
    response.sendRedirect("/");
  }

  @PostMapping(
      value = "/create-payment-link",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public void checkout(HttpServletRequest request, HttpServletResponse httpServletResponse) {
    try {
      final String baseUrl = getBaseUrl(request);
      
      // Get parameters from request (with fallback to default values)
      String productName = request.getParameter("productName");
      if (productName == null || productName.trim().isEmpty()) {
        productName = "Đơn hàng từ DeeG Shop";
      }
      
      String description = request.getParameter("description");
      if (description == null || description.trim().isEmpty()) {
        description = "Đơn hàng";
      }
      
      // Ensure description is within PayOS limit (25 characters)
      if (description.length() > 25) {
        description = description.substring(0, 25);
      }
      final String returnUrl = request.getParameter("returnUrl") != null ? 
          request.getParameter("returnUrl") : baseUrl + "/success";
      final String cancelUrl = request.getParameter("cancelUrl") != null ? 
          request.getParameter("cancelUrl") : baseUrl + "/cancel";
      final long price = request.getParameter("price") != null ? 
          Long.parseLong(request.getParameter("price")) : 1000;
      final long orderCode = System.currentTimeMillis() / 1000;
      
      System.out.println("PayOS Payment: " + productName + " - " + price + " VND");
      
      // Validate data
      if (price <= 0) {
        throw new IllegalArgumentException("Price must be greater than 0");
      }
      if (productName == null || productName.trim().isEmpty()) {
        throw new IllegalArgumentException("Product name cannot be empty");
      }
      if (description == null || description.trim().isEmpty()) {
        throw new IllegalArgumentException("Description cannot be empty");
      }
      // Create payment item
      PaymentLinkItem item = PaymentLinkItem.builder()
          .name(productName)
          .quantity(1)
          .price(price)
          .build();
      
      CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
          .orderCode(orderCode)
          .amount(price)
          .description(description)
          .returnUrl(returnUrl)
          .cancelUrl(cancelUrl)
          .item(item)
          .build();
              
      CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);

      // Use checkout URL for redirect
      String redirectUrl = data.getCheckoutUrl();
      if (redirectUrl == null && data.getQrCode() != null) {
        // Fallback to QR code page if no checkout URL
        redirectUrl = "/user/payment/qr?qrCode=" + java.net.URLEncoder.encode(data.getQrCode(), "UTF-8") + 
                     "&orderCode=" + orderCode + "&amount=" + price;
      }
      
      if (redirectUrl != null) {
        // Return JavaScript redirect
        httpServletResponse.setContentType("text/html; charset=UTF-8");
        String htmlResponse = "<!DOCTYPE html><html><head><title>Redirecting...</title></head><body>" +
            "<script>window.location.href='" + redirectUrl + "';</script>" +
            "<p>Redirecting to payment page... <a href='" + redirectUrl + "'>Click here if not redirected automatically</a></p>" +
            "</body></html>";
        httpServletResponse.getWriter().write(htmlResponse);
      } else {
        httpServletResponse.setStatus(500);
        httpServletResponse.getWriter().write("{\"error\": \"No redirect URL available\"}");
      }
    } catch (Exception e) {
      System.out.println("PayOS Error: " + e.getMessage());
      try {
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(500);
        httpServletResponse.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
      } catch (Exception ex) {
        httpServletResponse.setStatus(500);
      }
    }
  }

  private String getBaseUrl(HttpServletRequest request) {
    String scheme = request.getScheme();
    String serverName = request.getServerName();
    int serverPort = request.getServerPort();
    String contextPath = request.getContextPath();

    String url = scheme + "://" + serverName;
    if ((scheme.equals("http") && serverPort != 80)
        || (scheme.equals("https") && serverPort != 443)) {
      url += ":" + serverPort;
    }
    url += contextPath;
    return url;
  }
}
