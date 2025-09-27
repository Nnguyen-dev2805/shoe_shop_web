package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.entity.ShippingCompany;
import com.dev.shoeshop.service.ShippingCompanyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/shipping_company")
@RequiredArgsConstructor
@Slf4j
public class AdminShippingCompanyController {
    
    private final ShippingCompanyService shippingCompanyService;
    
    @GetMapping("/list")
    public String shippingCompanyList(HttpSession session) {
        // Kiểm tra đăng nhập
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "admin/shippingCompany/shippingCompany-list";
    }
    
    @GetMapping("/add")
    public String shippingCompanyAdd(HttpSession session) {
        // Kiểm tra đăng nhập
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "admin/shippingCompany/shippingCompany-add";
    }
    
    @GetMapping("/edit/{id}")
    public String shippingCompanyEdit(@PathVariable Integer id, Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Lấy thông tin công ty vận chuyển
        ShippingCompany shippingCompany = shippingCompanyService.getShippingCompanyById(id)
                .orElse(null);
        
        if (shippingCompany == null) {
            return "redirect:/admin/shipping_company/list";
        }
        
        model.addAttribute("shippingCompany", shippingCompany);
        return "admin/shippingCompany/shippingCompany-edit";
    }
    
    @PostMapping("/addNew")
    public String addShippingCompany(@RequestParam String name,
                                   @RequestParam(required = false) String hotline,
                                   @RequestParam(required = false) String email,
                                   @RequestParam(required = false) String address,
                                   @RequestParam(required = false) String website,
                                   @RequestParam Boolean isActive,
                                   RedirectAttributes redirectAttributes) {
        
        try {
            // Kiểm tra tên đã tồn tại chưa
            if (shippingCompanyService.existsByName(name)) {
                redirectAttributes.addFlashAttribute("error", "Tên công ty vận chuyển đã tồn tại!");
                return "redirect:/admin/shipping_company/add";
            }
            
            // Tạo đối tượng mới
            ShippingCompany shippingCompany = new ShippingCompany();
            shippingCompany.setName(name);
            shippingCompany.setHotline(hotline);
            shippingCompany.setEmail(email);
            shippingCompany.setAddress(address);
            shippingCompany.setWebsite(website);
            shippingCompany.setIsActive(isActive);
            
            // Lưu vào database
            shippingCompanyService.saveShippingCompany(shippingCompany);
            
            redirectAttributes.addFlashAttribute("success", "Thêm công ty vận chuyển thành công!");
            log.info("Created new shipping company: {}", name);
            
        } catch (Exception e) {
            log.error("Error creating shipping company: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm công ty vận chuyển!");
        }
        
        return "redirect:/admin/shipping_company/list";
    }
    
    @PostMapping("/edit")
    public String updateShippingCompany(@RequestParam Integer id,
                                      @RequestParam String name,
                                      @RequestParam(required = false) String hotline,
                                      @RequestParam(required = false) String email,
                                      @RequestParam(required = false) String address,
                                      @RequestParam(required = false) String website,
                                      @RequestParam Boolean isActive,
                                      RedirectAttributes redirectAttributes) {
        
        try {
            // Kiểm tra tên đã tồn tại chưa (trừ ID hiện tại)
            if (shippingCompanyService.existsByNameAndIdNot(name, id)) {
                redirectAttributes.addFlashAttribute("error", "Tên công ty vận chuyển đã tồn tại!");
                return "redirect:/admin/shipping_company/edit/" + id;
            }
            
            // Lấy thông tin hiện tại
            ShippingCompany existingShippingCompany = shippingCompanyService.getShippingCompanyById(id)
                    .orElse(null);
            
            if (existingShippingCompany == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy công ty vận chuyển!");
                return "redirect:/admin/shipping_company/list";
            }
            
            // Cập nhật thông tin
            existingShippingCompany.setName(name);
            existingShippingCompany.setHotline(hotline);
            existingShippingCompany.setEmail(email);
            existingShippingCompany.setAddress(address);
            existingShippingCompany.setWebsite(website);
            existingShippingCompany.setIsActive(isActive);
            
            // Lưu vào database
            shippingCompanyService.saveShippingCompany(existingShippingCompany);
            
            redirectAttributes.addFlashAttribute("success", "Cập nhật công ty vận chuyển thành công!");
            log.info("Updated shipping company: {} with id: {}", name, id);
            
        } catch (Exception e) {
            log.error("Error updating shipping company: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật công ty vận chuyển!");
        }
        
        return "redirect:/admin/shipping_company/list";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteShippingCompany(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            ShippingCompany shippingCompany = shippingCompanyService.getShippingCompanyById(id)
                    .orElse(null);
            
            if (shippingCompany == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy công ty vận chuyển!");
                return "redirect:/admin/shipping_company/list";
            }
            
            shippingCompanyService.deleteShippingCompany(id);
            redirectAttributes.addFlashAttribute("success", "Xóa công ty vận chuyển thành công!");
            log.info("Deleted shipping company with id: {}", id);
            
        } catch (Exception e) {
            log.error("Error deleting shipping company: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa công ty vận chuyển!");
        }
        
        return "redirect:/admin/shipping_company/list";
    }
}
