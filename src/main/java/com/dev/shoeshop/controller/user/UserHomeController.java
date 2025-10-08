package com.dev.shoeshop.controller.user;

import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.UserService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User Home Controller - Handles view rendering only
 * API endpoints are in ApiHomeController (/api/shop/*)
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserHomeController {

    private final UserService userService;

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

    @GetMapping("/my_account")
    public String myAccount(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
//            List<Long> viewedProductIds = (List<Long>) session.getAttribute(Constant.VIEW_PRODUCT);
//            if (viewedProductIds == null || viewedProductIds.isEmpty()) {
//                redirectAttributes.addFlashAttribute("message", "No products viewed yet.");
//            }else {
//                // Lọc ra các sản phẩm hợp lệ từ danh sách ID đã xem và loại bỏ sản phẩm đã bị xóa
//                List<Product> viewedProducts = productService.getProductsByIds(viewedProductIds);
//                // Lọc các sản phẩm đã bị xóa (isDelete == true) và loại bỏ chúng khỏi danh sách
//                viewedProducts = viewedProducts.stream()
//                        .filter(product -> product != null && !product.isDelete())  // Loại bỏ sản phẩm null và đã xóa
//                        .collect(Collectors.toList());
//
//                // Cập nhật lại sản phẩm đã xem trong session
//                viewedProductIds = viewedProducts.stream()
//                        .map(Product::getId)  // Lấy ID của các sản phẩm còn lại
//                        .collect(Collectors.toList());
//
//                // Lưu lại danh sách các sản phẩm hợp lệ vào session
//                session.setAttribute(Constant.VIEW_PRODUCT, viewedProductIds);
//                model.addAttribute("viewedProducts", viewedProducts);
//            }
//            // Lấy thông tin sản phẩm từ danh sách ID đã xem
//            List<Product> viewedProducts = productService.getProductsByIds(viewedProductIds);
//            model.addAttribute("viewedProducts", viewedProducts);
//            model.addAttribute("user", u);
//            List<Address> adr = addressService.getAddressesByID(u.getId());
//            model.addAttribute("adr", adr);
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

}
