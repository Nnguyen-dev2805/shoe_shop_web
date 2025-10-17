package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.entity.ShippingRate;
import com.dev.shoeshop.repository.ShippingRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/shipping-rates")
@RequiredArgsConstructor
@Slf4j
public class AdminShippingRateController {
    
    private final ShippingRateRepository shippingRateRepository;
    
    /**
     * Show shipping rates list
     */
    @GetMapping
    public String showRates(Model model) {
        List<ShippingRate> rates = shippingRateRepository.findAllByOrderByMinDistanceKmAsc();
        model.addAttribute("rates", rates);
        model.addAttribute("rate", new ShippingRate());
        return "admin/shipping-rates";
    }
    
    /**
     * Save or update shipping rate
     */
    @PostMapping("/save")
    public String saveRate(@ModelAttribute ShippingRate rate,
                          RedirectAttributes redirectAttributes) {
        try {
            shippingRateRepository.save(rate);
            redirectAttributes.addFlashAttribute("successMessage", 
                "✅ Đã lưu mức phí ship thành công!");
            log.info("Saved shipping rate: {} - {} km = {} VND", 
                     rate.getMinDistanceKm(), rate.getMaxDistanceKm(), rate.getFee());
        } catch (Exception e) {
            log.error("Error saving shipping rate", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "❌ Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/shipping-rates";
    }
    
    /**
     * Delete shipping rate
     */
    @PostMapping("/delete/{id}")
    public String deleteRate(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            shippingRateRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "✅ Đã xóa mức phí ship thành công!");
            log.info("Deleted shipping rate id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting shipping rate", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "❌ Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/shipping-rates";
    }
    
    /**
     * Toggle active status
     */
    @PostMapping("/toggle-active/{id}")
    public String toggleActive(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            ShippingRate rate = shippingRateRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rate not found"));
            
            rate.setIsActive(!rate.getIsActive());
            shippingRateRepository.save(rate);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "✅ Đã cập nhật trạng thái!");
            log.info("Toggled active status for rate id: {}", id);
        } catch (Exception e) {
            log.error("Error toggling rate status", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "❌ Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/shipping-rates";
    }
}
