package com.dev.shoeshop.controller.web;

import com.dev.shoeshop.dto.product.ProductDetailResponse;
import com.dev.shoeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping("")
    public String home() {
        return "web/index";
    }

    @GetMapping("/blog")
    public String blog(){
        return "/user/blog";
    }

}
