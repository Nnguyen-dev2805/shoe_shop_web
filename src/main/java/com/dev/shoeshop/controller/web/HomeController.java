package com.dev.shoeshop.controller.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("")
    public String getAllProducts(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "6") int size,
                                 Model model, HttpSession session) {

//        Users u = (Users) session.getAttribute(Constant.SESSION_USER);

//        if(u!=null){
//            if(u.getRole().getRoleId()==3)
//            {
//                return "redirect:/user/shop";
//            }
//        }
//        List<String> role = List.of("admin","manager","user","shipper");
//        for(int i = 0; i < role.size(); i++){
//            if(roleService.findRoleByName(role.get(i))==null){
//                Role ro = new Role();
//                ro.setRoleName(role.get(i));
//                roleService.insertRole(ro);
//            }
//        }
//        List<Category> categories = categoryService.findAll();
//        model.addAttribute("categories", categories);
//        if (!categories.isEmpty()) {
//            Category firstCategory = categories.get(0);
//            List<Product> products = productService.getProductByCategoryId(firstCategory.getId());
//            model.addAttribute("products", products);
//            model.addAttribute("selectedCategory", firstCategory.getId()); // Gửi danh mục được chọn
//        }
//        Page<Product> productPage = productService.getPaginatedProducts(PageRequest.of(page, size));
//        model.addAttribute("products", productPage.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", productPage.getTotalPages());
//
//
//        // lay ra list 20 san pham co rating cao nhat
//        List<Product> ratedProducts = productService.getTopRatedProducts();
//        model.addAttribute("ratedProducts", ratedProducts);
        return "web/index";
    }

    @GetMapping("/blog")
    public String blog(){
        return "/user/blog";
    }
}
