package com.dev.shoeshop.controller.user;

import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.dto.OrderDetailDTO;
import com.dev.shoeshop.dto.RatingRequestDTO;
import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.*;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.CoinTransactionRepository;
import com.dev.shoeshop.repository.OrderDetailRepository;
import com.dev.shoeshop.repository.RatingRepository;
import com.dev.shoeshop.repository.ReturnRequestRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.CloudinaryService;
import com.dev.shoeshop.service.OrderService;
import com.dev.shoeshop.service.RatingService;
import com.dev.shoeshop.service.StorageService;
import com.dev.shoeshop.service.UserService;
import com.dev.shoeshop.utils.Constant;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
/**
 * User Home Controller - Handles view rendering only
 * API endpoints are in ApiHomeController (/api/shop/*)
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserHomeController {
    private final StorageService storageService;
    private final OrderService orderService;
    private final UserService userService;
    private final RatingService ratingService;
    private final CloudinaryService cloudinaryService;
    private final OrderDetailRepository orderDetailRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final CoinTransactionRepository coinTransactionRepository;
    private final ReturnRequestRepository returnRequestRepository;

    @GetMapping("/shop")
    public String userShop(HttpSession session) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
            return "user/shop";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/blog")
    public String blog(){
        return "/user/blog";
    }
    
    /**
     * Wishlist page - Load via Ajax
     */
    @GetMapping("/wishlist")
    public String wishlist(HttpSession session) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
            return "user/wishlist";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/my_account")
    public String myAccount(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
            // Reload user từ database để get coins balance mới nhất
            Users user = userRepository.findById(u.getId()).orElse(u);
            model.addAttribute("user", user);
            model.addAttribute("walletBalance", user.getCoins() != null ? user.getCoins() : 0L);
            
            // Load coin statistics
            Long totalSpent = coinTransactionRepository.getTotalSpentByUserId(user.getId());
            Long totalEarned = coinTransactionRepository.getTotalEarnedByUserId(user.getId());
            model.addAttribute("totalSpent", Math.abs(totalSpent != null ? totalSpent : 0L));
            model.addAttribute("totalEarned", totalEarned != null ? totalEarned : 0L);
            
            // Load recent coin transactions (top 10)
            List<CoinTransaction> recentTransactions = coinTransactionRepository.findByUserIdOrderByCreatedDateDesc(
                user.getId(), 
                org.springframework.data.domain.PageRequest.of(0, 10)
            );
            model.addAttribute("coinTransactions", recentTransactions);
            
            return "user/my-account";
        } else {
            return "redirect:/login";
        }
    }



    @GetMapping("/shipper/all")
    public ResponseEntity<?> getAllShipper(){
        List<UserDTO> listShipper = userService.getAllShipper(4L);
        Map<String, Object> response = new HashMap<>();

        response.put("listShipper", listShipper);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shipper/search")
    public ResponseEntity<?> getShipperByName(@RequestParam(value = "name") String name){
        List<UserDTO> listShipper = userService.findByFullnameAndRole(name,4L);
        Map<String, Object> response = new HashMap<>();

        response.put("listShipper", listShipper);
        return ResponseEntity.ok(response);
    }
    
    // Trang xem đơn hàng theo trạng thái
    @GetMapping("/order/view")
    public String orderView(HttpSession session, Model model) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
            model.addAttribute("user", u);
            return "user/order-view";
        } else {
            return "redirect:/login";
        }
    }
    
    // Trang chi tiết đơn hàng
    @GetMapping("/order-detail/{orderId}")
    public String orderDetail(@PathVariable Long orderId, HttpSession session, Model model) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
        try {
            OrderDTO orderDetail = orderService.getOrderDetailById(orderId, u.getId());
            
            // Check rating status for each order detail
            if (orderDetail.getOrderDetails() != null) {
                for (OrderDetailDTO detail : orderDetail.getOrderDetails()) {
                    // Check if this order detail has been rated by user
                    try {
                        OrderDetail orderDetailEntity = orderDetailRepository.findById(detail.getId().intValue()).orElse(null);
                        if (orderDetailEntity != null) {
                            boolean hasRating = ratingService.hasRating(orderDetailEntity, u);
                            detail.setHasRating(hasRating);
                            System.out.println("OrderDetail " + detail.getId() + " hasRating: " + hasRating);
                        } else {
                            detail.setHasRating(false);
                        }
                    } catch (Exception e) {
                        System.err.println("Error checking rating status: " + e.getMessage());
                        detail.setHasRating(false);
                    }
                }
            }
            
            // Load return request if exists
            returnRequestRepository.findByOrderIdAndUserId(orderId, u.getId())
                    .ifPresent(returnRequest -> {
                        model.addAttribute("returnRequest", returnRequest);
                        System.out.println("✅ Found return request #" + returnRequest.getId() + " with status: " + returnRequest.getStatus());
                    });
            
            model.addAttribute("order", orderDetail);
            model.addAttribute("user", u);
            return "user/order-detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "user/order-detail";
        }
        } else {
            return "redirect:/login";
        }
    }
    
    /**
     * API endpoint để submit single rating (with image support)
     */
    @PostMapping("/api/rating/submit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitSingleRating(
            @RequestParam Long orderDetailId,
            @RequestParam Integer rating,
            @RequestParam String comment,
            @RequestParam(required = false) MultipartFile[] images,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== Single Rating Request Received ===");
            System.out.println("Order Detail ID: " + orderDetailId);
            System.out.println("Rating: " + rating);
            System.out.println("Comment: " + comment);
            System.out.println("Images count: " + (images != null ? images.length : 0));
            
            // Lấy user từ session
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để đánh giá");
                return ResponseEntity.status(401).body(response);
            }
            
            // Validate rating value
            if (rating < 1 || rating > 5) {
                response.put("success", false);
                response.put("message", "Đánh giá phải từ 1-5 sao");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate orderDetailId belongs to user's order
            OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId.intValue())
                .orElse(null);
            
            if (orderDetail == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy chi tiết đơn hàng");
                return ResponseEntity.status(404).body(response);
            }
            
            // Check if order belongs to user
            if (!orderDetail.getOrder().getUser().getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "Bạn không có quyền đánh giá đơn hàng này");
                return ResponseEntity.status(403).body(response);
            }
            
            // Check if order is DELIVERED
            if (orderDetail.getOrder().getStatus() != ShipmentStatus.DELIVERED) {
                response.put("success", false);
                response.put("message", "Chỉ có thể đánh giá đơn hàng đã giao thành công");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if already rated
            boolean alreadyRated = ratingService.hasRating(orderDetail, user);
            if (alreadyRated) {
                response.put("success", false);
                response.put("message", "Bạn đã đánh giá sản phẩm này rồi");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Upload images to Cloudinary
            List<String> imageUrls = new ArrayList<>();
            if (images != null && images.length > 0) {
                System.out.println("Uploading " + images.length + " images to Cloudinary...");
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        try {
                            String imageUrl = cloudinaryService.uploadImage(image, CloudinaryService.RATING_FOLDER);
                            imageUrls.add(imageUrl);
                            System.out.println("✅ Uploaded: " + imageUrl);
                        } catch (Exception e) {
                            System.err.println("❌ Failed to upload image: " + e.getMessage());
                        }
                    }
                }
            }
            
            System.out.println("Total images uploaded: " + imageUrls.size());
            
            // Save rating to database
            Rating newRating = Rating.builder()
                .star(rating)
                .comment(comment)
                .image(imageUrls.isEmpty() ? null : String.join(",", imageUrls))
                .user(user)
                .product(orderDetail.getProduct().getProduct())
                .productDetail(orderDetail.getProduct())
                .orderDetail(orderDetail)
                .build();
            
            ratingRepository.save(newRating);
            System.out.println("✅ Rating saved to database with ID: " + newRating.getId());
            
            response.put("success", true);
            response.put("message", "Đánh giá đã được gửi thành công");
            response.put("uploadedImages", imageUrls.size());
            System.out.println("✅ Rating submitted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * API endpoint để submit ratings (batch)
     */
    @PostMapping("/api/ratings")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitRatings(
            @RequestBody RatingRequestDTO ratingRequest,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== Rating Request Received ===");
            System.out.println("Order ID: " + ratingRequest.getOrderId());
            System.out.println("Ratings count: " + (ratingRequest.getRatings() != null ? ratingRequest.getRatings().size() : 0));
            
            // Lấy user từ session
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                System.out.println("User not found in session");
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để đánh giá");
                return ResponseEntity.status(401).body(response);
            }
            
            System.out.println("User ID: " + user.getId());
            
            // Validate order belongs to user
            OrderDTO order = orderService.getOrderDetailById(ratingRequest.getOrderId(), user.getId());
            if (order == null) {
                System.out.println("Order not found or not belongs to user");
                response.put("success", false);
                response.put("message", "Đơn hàng không tồn tại hoặc không thuộc về bạn");
                return ResponseEntity.status(404).body(response);
            }
            
            System.out.println("Order found - Status: " + order.getStatus());
            
            // Check if order is DELIVERED
            if (order.getStatus() != ShipmentStatus.DELIVERED) {
                response.put("success", false);
                response.put("message", "Chỉ có thể đánh giá đơn hàng đã giao");
                return ResponseEntity.status(400).body(response);
            }
            
            // Submit ratings
            System.out.println("Submitting ratings...");
            ratingService.submitRatings(ratingRequest, user.getId());
            System.out.println("Ratings submitted successfully");
            
            response.put("success", true);
            response.put("message", "Đánh giá đã được gửi thành công");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * API endpoint để kiểm tra trạng thái đánh giá của order details
     */
    @GetMapping("/api/order/{orderId}/rating-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRatingStatus(@PathVariable Long orderId, HttpSession session) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Bạn cần đăng nhập"));
        }
        
        try {
            OrderDTO order = orderService.getOrderDetailById(orderId, user.getId());
            Map<String, Boolean> ratingStatus = new HashMap<>();
            
            if (order.getOrderDetails() != null) {
                for (OrderDetailDTO detail : order.getOrderDetails()) {
                    try {
                        com.dev.shoeshop.entity.OrderDetail orderDetailEntity = 
                            orderDetailRepository.findById(detail.getId().intValue()).orElse(null);
                        
                        if (orderDetailEntity != null) {
                            boolean hasRating = ratingService.hasRating(orderDetailEntity, user);
                            ratingStatus.put(detail.getId().toString(), hasRating);
                        } else {
                            ratingStatus.put(detail.getId().toString(), false);
                        }
                    } catch (Exception e) {
                        System.out.println("Error checking rating for order detail " + detail.getId() + ": " + e.getMessage());
                        ratingStatus.put(detail.getId().toString(), false);
                    }
                }
            }
            
            return ResponseEntity.ok(Map.of("success", true, "ratingStatus", ratingStatus));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }
    
    
    // API endpoint để lấy orders theo status
    @GetMapping("/api/orders/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status, HttpSession session) {
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not logged in"));
        }
        
        try {
            ShipmentStatus shipmentStatus = ShipmentStatus.valueOf(status.toUpperCase());
            List<OrderDTO> orders = orderService.getOrdersByUserIdAndStatus(user.getId(), shipmentStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid status: " + status);
            response.put("success", false);
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to load orders: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * API endpoint để user hủy đơn hàng
     */
    @PostMapping("/api/orders/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long orderId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra user đã đăng nhập
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để hủy đơn hàng");
                return ResponseEntity.status(401).body(response);
            }
            
            // Kiểm tra đơn hàng có thuộc về user không
            OrderDTO order = orderService.getOrderDetailById(orderId, user.getId());
            if (order == null) {
                response.put("success", false);
                response.put("message", "Đơn hàng không tồn tại hoặc không thuộc về bạn");
                return ResponseEntity.status(404).body(response);
            }
            
            // Kiểm tra trạng thái đơn hàng - chỉ cho phép hủy đơn ở trạng thái IN_STOCK
            if (order.getStatus() != ShipmentStatus.IN_STOCK) {
                response.put("success", false);
                response.put("message", "Chỉ có thể hủy đơn hàng đang ở trạng thái chờ xác nhận");
                return ResponseEntity.status(400).body(response);
            }
            
            // Hủy đơn hàng
            orderService.cancelOrder(orderId);
            
            response.put("success", true);
            response.put("message", "Hủy đơn hàng thành công");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * API endpoint để upload ảnh đánh giá
     */
    @PostMapping("/api/upload/rating-image")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadRatingImage(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra user đã đăng nhập
            Users user = (Users) session.getAttribute(Constant.SESSION_USER);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để upload ảnh");
                return ResponseEntity.status(401).body(response);
            }
            
            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn file ảnh");
                return ResponseEntity.status(400).body(response);
            }
            
            // Kiểm tra định dạng file
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "Chỉ chấp nhận file ảnh (JPG, PNG, GIF)");
                return ResponseEntity.status(400).body(response);
            }
            
            // Kiểm tra kích thước file (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "Kích thước file không được vượt quá 5MB");
                return ResponseEntity.status(400).body(response);
            }
            
            // ✅ Upload using storeRatingImage (uploads to shoe_shop/ratings folder)
            String imageUrl = storageService.storeRatingImage(file);
            
            response.put("success", true);
            response.put("imageUrl", imageUrl);
            response.put("message", "Upload ảnh thành công");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error uploading rating image: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi khi upload ảnh: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}
