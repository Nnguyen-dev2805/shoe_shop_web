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
        return "admin/inventory/inventory-list";
    }

    @GetMapping("/insert")
    public String insertInventoryPage() {
        return "admin/inventory/inventory-add";
    }

    @GetMapping("/add")
    public String addInventoryPage() {
        return "admin/inventory/inventory-add";
    }

    @GetMapping("/edit/{id}")
    public String inventoryEditPage(@PathVariable Long id) {
        return "admin/inventory/inventory-edit";
    }

}
