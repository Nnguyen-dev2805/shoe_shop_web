package com.dev.shoeshop.controller.user;

import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.UserService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserHomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/shop")
    public String userHome(HttpSession session, Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "6") int size) {
        Users u = (Users) session.getAttribute(Constant.SESSION_USER);
        if (u != null) {
//            List<Category> categories = categoryService.findAll();
//            List<Product> wishlist = wishListService.getWishlist(u.getId());
//            int cateCount = categoryService.count();
//            if (categories != null && cateCount > 0) {
//                Pageable pageable = PageRequest.of(page, size);
//                Page<Product> productsPage = productService.findAllPage(pageable);
//
//                model.addAttribute("cate", categories);
//            } else {
//                model.addAttribute("cate", null);
//            }
//            model.addAttribute("wishlist", wishlist);
//            model.addAttribute("user", u);
//            Page<Product> productPage = productService.getPaginatedProducts(PageRequest.of(page, size));
//
//            model.addAttribute("products", productPage.getContent());
//            model.addAttribute("currentPage", page);
//            model.addAttribute("totalPages", productPage.getTotalPages());
            return "user/shop";
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
