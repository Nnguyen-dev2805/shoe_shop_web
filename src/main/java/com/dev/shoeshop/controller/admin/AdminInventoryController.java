package com.dev.shoeshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/inventory")
public class AdminInventoryController {
    @GetMapping("")
    public String showInventory() {
        return "admin/inventory/inventory-warehouse";
    }

    @GetMapping("/insert")
    public String insertInventoryPage() {
        return "admin/inventory/inventory-add";
    }

}
