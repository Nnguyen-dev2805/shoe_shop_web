package com.dev.shoeshop.controller.admin;

import com.dev.shoeshop.dto.discount.DiscountCreateRequest;
import com.dev.shoeshop.dto.discount.DiscountResponse;
import com.dev.shoeshop.dto.discount.DiscountUpdateRequest;
import com.dev.shoeshop.entity.Discount;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.service.DiscountService;
import com.dev.shoeshop.utils.Constant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final DiscountService discountService;

    /**
     * Hiển thị danh sách discount
     * URL: /admin/discount-list
     */
    /*  
        HttpSession session: lưu trữ thông tin đăng nhập của người dùng
        Model: lưu trữ dữ liệu hiển thị trên trang web
        page: trang hiển thị
        size: số lượng discount hiển thị trên mỗi trang
        status: trạng thái discount
    */
    @GetMapping("/discount-list")
    public String discountList(HttpSession session, Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "4") int size,
                              @RequestParam(required = false) String status) {
        
        // Kiểm tra đăng nhập
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<DiscountResponse> discountPage;
            
            // Lấy danh sách discount theo status hoặc tất cả
            if (status != null && !status.trim().isEmpty()) {
                discountPage = discountService.getDiscountsByStatus(status, pageable);
            } 
            else {
                discountPage = discountService.getAllDiscounts(pageable);
            }
            
            model.addAttribute("disco", discountPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", discountPage.getTotalPages());
            model.addAttribute("totalElements", discountPage.getTotalElements());
            model.addAttribute("selectedStatus", status);
            
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách discount: " + e.getMessage());
            model.addAttribute("disco", java.util.Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
        }
        
        return "admin/discount/discount_list";
    }

    /**
     * Hiển thị form tạo discount mới
     * URL: /admin/discount-add
     */
    @GetMapping("/discount-add")
    public String discountAdd(HttpSession session, Model model) {
        
        // Kiểm tra đăng nhập
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Khởi tạo discount mới cho form
        Discount discount = new Discount();
        discount.setStatus("INACTIVE");
        discount.setQuantity(1000);
        discount.setPercent(0.0);
        discount.setMinOrderValue(0.0);
        discount.setStartDate(java.time.LocalDate.now());
        discount.setEndDate(java.time.LocalDate.now().plusDays(30));
        
        model.addAttribute("discount", discount);
        
        return "admin/discount/discount_add";
    }

    /**
     * Hiển thị form chỉnh sửa discount
     * URL: /admin/discount-edit/{id}
     */
    @GetMapping("/discount-edit/{id}")
    public String discountEdit(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        
        // Kiểm tra đăng nhập
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            Discount discount = discountService.getDiscountById(id);
            if (discount == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy discount với ID: " + id);
                return "redirect:/admin/discount-list";
            }
            
            model.addAttribute("discount", discount);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải discount: " + e.getMessage());
            return "redirect:/admin/discount-list";
        }
        
        return "admin/discount/discount_edit";
    }

    /**
     * Xử lý xóa discount
     * URL: /admin/discount-delete/{id}
     */
    @GetMapping("/discount-delete/{id}")
    public String discountDelete(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        
        // Kiểm tra đăng nhập
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            Discount discount = discountService.getDiscountById(id);
            if (discount == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy discount với ID: " + id);
                return "redirect:/admin/discount-list";
            }
            
            discountService.deleteDiscount(id);
            redirectAttributes.addFlashAttribute("success", "Xóa discount '" + discount.getName() + "' thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa discount: " + e.getMessage());
        }
        
        return "redirect:/admin/discount-list";
    }

    /**
     * Xử lý form edit discount
     * URL: POST /admin/discount/edit
     */
    @PostMapping("/discount/edit")
    public String updateDiscount(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam(required = false) Integer quantity,
            @RequestParam Double percent,
            @RequestParam String status,
            @RequestParam(required = false) Double minOrderValue,
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Kiểm tra đăng nhập
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            System.out.println("=== UPDATE DISCOUNT DEBUG ===");
            System.out.println("ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Percent: " + percent);
            System.out.println("Status: " + status);
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            
            // Tạo DiscountUpdateRequest
            DiscountUpdateRequest updateRequest = new DiscountUpdateRequest();
            updateRequest.setId(id);
            updateRequest.setName(name);
            updateRequest.setPercent(percent / 100.0); // Convert từ phần trăm sang decimal
            updateRequest.setStatus(status);
            updateRequest.setMinOrderValue(minOrderValue);
            updateRequest.setQuantity(1000); // Default quantity (có thể lấy từ form nếu cần)
            updateRequest.setStartDate(java.time.LocalDate.parse(startDate));
            updateRequest.setEndDate(java.time.LocalDate.parse(endDate));
            
            // Sử dụng service method với mapper
            Discount savedDiscount = discountService.updateDiscount(id, updateRequest, user.getId());
            
            System.out.println("Successfully saved discount: " + savedDiscount.getName());
            redirectAttributes.addFlashAttribute("success", "Cập nhật discount '" + name + "' thành công!");
            
        } catch (Exception e) {
            System.out.println("ERROR updating discount: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật discount: " + e.getMessage());
        }
        
        return "redirect:/admin/discount-list";
    }

    /**
     * Xử lý form add discount
     * URL: POST /admin/discount/add
     */
    @PostMapping("/discount/add")
    public String addDiscount(
            @RequestParam String name,
            @RequestParam(required = false) Integer quantity,
            @RequestParam Double percent,
            @RequestParam String status,
            @RequestParam(required = false) Double minOrderValue,
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Kiểm tra đăng nhập
        Users user = (Users) session.getAttribute(Constant.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Tạo DiscountCreateRequest
            DiscountCreateRequest createRequest = new DiscountCreateRequest();
            createRequest.setName(name);
            createRequest.setPercent(percent / 100.0); // Convert từ phần trăm sang decimal
            createRequest.setStatus(status);
            createRequest.setMinOrderValue(minOrderValue);
            createRequest.setQuantity(quantity != null ? quantity : 1000);
            createRequest.setStartDate(java.time.LocalDate.parse(startDate));
            createRequest.setEndDate(java.time.LocalDate.parse(endDate));
            
            // Sử dụng service method với mapper
            discountService.createDiscount(createRequest, user.getId());
            
            redirectAttributes.addFlashAttribute("success", "Tạo discount '" + name + "' thành công!");
            
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo discount: " + e.getMessage());
        }
        return "redirect:/admin/discount-list";
    }
}
