package com.dev.shoeshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/product")
public class AdminProductController {

    @GetMapping("/insert")
    public String insertProductPage() {
        return "/admin/products/product-add";
    }

    @GetMapping("")
    public String getAllProduct() {
        return "/admin/products/product-list";
    }

//    @GetMapping("")
//    public String getAllProduct(@RequestParam(defaultValue = "0") int page,
//                                @RequestParam(defaultValue = "3") int size,
//                                Model model) {
//        Page<Product> productPage = productService.getPaginatedProducts(PageRequest.of(page, size));
//        model.addAttribute("products", productPage.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", productPage.getTotalPages());
//        return "/admin/products/product-list";
//    }
}
