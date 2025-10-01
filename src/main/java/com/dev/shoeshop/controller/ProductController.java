package com.dev.shoeshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {
    @GetMapping("/details/{id}")
    public String getProductDetails(@PathVariable long id, Model model, HttpSession session) {
//        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
//
//
//        List<Long> viewedProductIds = (List<Long>) session.getAttribute(Constant.VIEW_PRODUCT);
//
//        if (viewedProductIds == null) {
//            viewedProductIds = new ArrayList<>();
//        }
//        // Thêm sản phẩm vào danh sách nếu chưa có
//        if (!viewedProductIds.contains(id)) {
//            viewedProductIds.add(id);
//        }
//
//        if(u != null) {
//
//            // Lưu lại vào session
//            session.setAttribute(Constant.VIEW_PRODUCT, viewedProductIds);
//            model.addAttribute("user", u);
//            List<Product> wishlist = wishListService.getWishlist(u.getId());
//            model.addAttribute("wishlist", wishlist);
//        }
//
//        // Truyền thông báo nếu có
//        String alert = (String) model.asMap().get("alert");
//
//        System.out.println(alert);
//        model.addAttribute("alert", alert);
//
//
//        Product product = productService.getProductById(id);
//        List<ProductDetail> productDetails = productDetailService.findProductByProductId(id);
//        model.addAttribute("productDetails", productDetails);
//        model.addAttribute("product", product);
//
//        int totalRating = ratingService.countRatingsByProductId(id);
//        model.addAttribute("totalRating", totalRating);
//
//        List<Rating> ratings = ratingService.getAllRatingsByProductId(id);
//
//        // Tính trung bình số sao
//        double averageStar = ratings.stream().mapToInt(Rating::getStar).average().orElse(0.0);
//        averageStar = Math.round(averageStar * 10) / 10.0;
//        model.addAttribute("avgrating", averageStar);
        return "user/single-product";
    }
}
