package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.entity.ShopWarehouse;
import com.dev.shoeshop.service.ShopWarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
@Slf4j
public class AdminWarehouseController {
    
    private final ShopWarehouseService warehouseService;
    
    /**
     * Show settings page with warehouse list
     */
    @GetMapping
    public String showSettings(Model model) {
        List<ShopWarehouse> warehouses = warehouseService.getAllWarehouses();
        model.addAttribute("warehouses", warehouses);
        
        // Create new warehouse with default values
        ShopWarehouse warehouse = new ShopWarehouse();
        warehouse.setIsActive(true);
        warehouse.setIsDefault(false);
        model.addAttribute("warehouse", warehouse);
        
        return "admin/settings";
    }
    
    /**
     * Add or update warehouse
     */
    @PostMapping("/warehouse/save")
    public String saveWarehouse(
            @ModelAttribute ShopWarehouse warehouse,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("Saving warehouse: {}", warehouse.getName());
            warehouseService.saveWarehouse(warehouse);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Đã lưu địa chỉ kho thành công!");
        } catch (Exception e) {
            log.error("Error saving warehouse", e);
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }
    
    /**
     * Set warehouse as default
     */
    @PostMapping("/warehouse/set-default/{id}")
    public String setDefaultWarehouse(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            warehouseService.setDefaultWarehouse(id);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Đã đặt làm kho mặc định!");
        } catch (Exception e) {
            log.error("Error setting default warehouse", e);
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }
    
    /**
     * Delete warehouse
     */
    @PostMapping("/warehouse/delete/{id}")
    public String deleteWarehouse(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            warehouseService.deleteWarehouse(id);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Đã xóa kho thành công!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting warehouse", e);
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }
}
