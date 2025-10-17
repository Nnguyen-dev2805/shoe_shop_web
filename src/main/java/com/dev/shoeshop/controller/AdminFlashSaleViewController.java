package com.dev.shoeshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Admin Flash Sale Views
 * Routes cho các trang quản lý Flash Sale trong Admin Panel
 */
@Controller
@RequestMapping("/admin")
public class AdminFlashSaleViewController {
    
    /**
     * Danh sách Flash Sale
     * URL: /admin/flash-sale-list
     */
    @GetMapping("/flash-sale-list")
    public String flashSaleList() {
        return "admin/flashsale/flash_sale_list";
    }
    
    /**
     * Trang thêm Flash Sale mới
     * URL: /admin/flash-sale-add
     */
    @GetMapping("/flash-sale-add")
    public String flashSaleAdd() {
        return "admin/flashsale/flash_sale_add";
    }
    
    /**
     * Trang chỉnh sửa Flash Sale
     * URL: /admin/flash-sale-edit/{id}
     */
    @GetMapping("/flash-sale-edit/{id}")
    public String flashSaleEdit(@PathVariable Long id) {
        return "admin/flashsale/flash_sale_edit";
    }
}
