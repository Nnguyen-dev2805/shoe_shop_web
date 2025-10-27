package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.OrderDTO;
import com.dev.shoeshop.entity.Brand;
import com.dev.shoeshop.entity.Category;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.BrandRepository;
import com.dev.shoeshop.repository.CategoryRepository;
import com.dev.shoeshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service helper để query dữ liệu cho Gemini AI
 * Cung cấp các function mà Gemini có thể gọi
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiDataQueryService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final OrderService orderService;
    
    /**
     * Lấy tất cả sản phẩm để AI recommendation (giới hạn 10 sản phẩm)
     */
    public String getAllProductsForRecommendation() {
        try {
            List<Product> products = productRepository.findByIsDeleteFalse();
            
            if (products.isEmpty()) {
                return "Hiện tại chưa có sản phẩm nào.";
            }
            
            // Lấy tối đa 10 sản phẩm đa dạng
            List<Product> selectedProducts = products.stream()
                    .limit(10)
                    .toList();
            
            StringBuilder result = new StringBuilder();
            for (Product p : selectedProducts) {
                result.append("• ").append(p.getTitle());
                
                if (p.getBrand() != null) {
                    result.append(" - Thương hiệu: ").append(p.getBrand().getName());
                }
                
                if (p.getCategory() != null) {
                    result.append(" - Loại: ").append(p.getCategory().getName());
                }
                
                result.append(" - Giá: ").append(formatPrice(p.getPrice()));
                
                // Thêm mô tả ngắn nếu có
                if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                    String shortDesc = p.getDescription().length() > 100 
                        ? p.getDescription().substring(0, 100) + "..." 
                        : p.getDescription();
                    result.append(" - Mô tả: ").append(shortDesc);
                }
                
                result.append("\n\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting all products: ", e);
            return "Lỗi khi lấy danh sách sản phẩm";
        }
    }
    
    /**
     * Tìm kiếm sản phẩm theo tên
     */
    public String searchProducts(String keyword) {
        try {
            List<Product> products = productRepository.findByIsDeleteFalse();
            
            // Filter by keyword
            List<Product> filtered = products.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .limit(5) // Giới hạn 5 kết quả
                    .toList();
            
            if (filtered.isEmpty()) {
                return "Không tìm thấy sản phẩm nào phù hợp với từ khóa: " + keyword;
            }
            
            StringBuilder result = new StringBuilder("Tìm thấy " + filtered.size() + " sản phẩm:\n\n");
            for (Product p : filtered) {
                result.append("- ").append(p.getTitle())
                      .append(" (").append(p.getBrand() != null ? p.getBrand().getName() : "N/A").append(")")
                      .append(" - Giá: ").append(formatPrice(p.getPrice()))
                      .append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error searching products: ", e);
            return "Lỗi khi tìm kiếm sản phẩm";
        }
    }
    
    /**
     * Lấy thông tin chi tiết sản phẩm theo ID
     */
    public String getProductDetail(Long productId) {
        try {
            var productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                return "Không tìm thấy sản phẩm với ID: " + productId;
            }
            
            Product p = productOpt.get();
            StringBuilder result = new StringBuilder();
            result.append("📦 Sản phẩm: ").append(p.getTitle()).append("\n");
            result.append("🏷️ Thương hiệu: ").append(p.getBrand() != null ? p.getBrand().getName() : "N/A").append("\n");
            result.append("📂 Danh mục: ").append(p.getCategory() != null ? p.getCategory().getName() : "N/A").append("\n");
            result.append("💰 Giá: ").append(formatPrice(p.getPrice())).append("\n");
            
            if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                result.append("📝 Mô tả: ").append(p.getDescription().substring(0, Math.min(200, p.getDescription().length()))).append("...\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting product detail: ", e);
            return "Lỗi khi lấy thông tin sản phẩm";
        }
    }
    
    /**
     * Lấy danh sách danh mục
     */
    public String getCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            
            if (categories.isEmpty()) {
                return "Chưa có danh mục nào";
            }
            
            StringBuilder result = new StringBuilder("Danh sách danh mục giày:\n\n");
            for (Category c : categories) {
                result.append("- ").append(c.getName()).append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting categories: ", e);
            return "Lỗi khi lấy danh sách danh mục";
        }
    }
    
    /**
     * Lấy danh sách thương hiệu
     */
    public String getBrands() {
        try {
            List<Brand> brands = brandRepository.findAll();
            
            if (brands.isEmpty()) {
                return "Chưa có thương hiệu nào";
            }
            
            StringBuilder result = new StringBuilder("Danh sách thương hiệu:\n\n");
            for (Brand b : brands) {
                result.append("- ").append(b.getName()).append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting brands: ", e);
            return "Lỗi khi lấy danh sách thương hiệu";
        }
    }
    
    /**
     * Lấy sản phẩm theo danh mục
     */
    public String getProductsByCategory(String categoryName) {
        try {
            List<Product> products = productRepository.findByIsDeleteFalse();
            
            List<Product> filtered = products.stream()
                    .filter(p -> p.getCategory() != null && 
                                p.getCategory().getName().toLowerCase().contains(categoryName.toLowerCase()))
                    .limit(5)
                    .toList();
            
            if (filtered.isEmpty()) {
                return "Không tìm thấy sản phẩm nào trong danh mục: " + categoryName;
            }
            
            StringBuilder result = new StringBuilder("Sản phẩm thuộc danh mục " + categoryName + ":\n\n");
            for (Product p : filtered) {
                result.append("- ").append(p.getTitle())
                      .append(" - Giá: ").append(formatPrice(p.getPrice()))
                      .append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting products by category: ", e);
            return "Lỗi khi lấy sản phẩm theo danh mục";
        }
    }
    
    /**
     * Lấy sản phẩm theo thương hiệu
     */
    public String getProductsByBrand(String brandName) {
        try {
            List<Product> products = productRepository.findByIsDeleteFalse();
            
            List<Product> filtered = products.stream()
                    .filter(p -> p.getBrand() != null && 
                                p.getBrand().getName().toLowerCase().contains(brandName.toLowerCase()))
                    .limit(5)
                    .toList();
            
            if (filtered.isEmpty()) {
                return "Không tìm thấy sản phẩm nào của thương hiệu: " + brandName;
            }
            
            StringBuilder result = new StringBuilder("Sản phẩm của thương hiệu " + brandName + ":\n\n");
            for (Product p : filtered) {
                result.append("- ").append(p.getTitle())
                      .append(" - Giá: ").append(formatPrice(p.getPrice()))
                      .append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting products by brand: ", e);
            return "Lỗi khi lấy sản phẩm theo thương hiệu";
        }
    }
    
    /**
     * Lấy đơn hàng của user
     */
    public String getUserOrders(Long userId) {
        try {
            if (userId == null) {
                return "Bạn cần đăng nhập để xem đơn hàng";
            }
            
            List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
            
            if (orders.isEmpty()) {
                return "Bạn chưa có đơn hàng nào";
            }
            
            StringBuilder result = new StringBuilder("Danh sách đơn hàng của bạn:\n\n");
            for (OrderDTO order : orders.stream().limit(5).toList()) {
                result.append("- Đơn hàng #").append(order.getId())
                      .append(" - Tổng: ").append(formatPrice(order.getTotalPrice()))
                      .append(" - Trạng thái: ").append(getStatusText(order.getStatus()))
                      .append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting user orders: ", e);
            return "Lỗi khi lấy danh sách đơn hàng";
        }
    }
    
    /**
     * Lấy thông tin chung về cửa hàng
     */
    public String getStoreInfo() {
        return """
                🏪 DeeG Shoe Shop
                
                📍 Địa chỉ: 1 Võ Văn Ngân, Thủ Đức, TP.HCM
                📞 Hotline: 1900 xxxx
                ⏰ Giờ mở cửa: 8:00 - 22:00 (Thứ 2 - Chủ Nhật)
                
                ✨ Chúng tôi chuyên cung cấp giày dép thể thao chính hãng với nhiều thương hiệu nổi tiếng.
                🚚 Giao hàng toàn quốc
                💳 Thanh toán COD hoặc chuyển khoản
                🔄 Đổi trả trong 7 ngày
                """;
    }
    
    /**
     * Lấy chính sách đổi trả
     */
    public String getReturnPolicy() {
        return """
                🔄 CHÍNH SÁCH ĐỔI TRẢ
                
                ✅ Điều kiện đổi trả:
                - Sản phẩm còn nguyên vẹn, chưa qua sử dụng
                - Còn đầy đủ hộp, phụ kiện, hóa đơn
                - Trong vòng 7 ngày kể từ ngày nhận hàng
                
                📝 Quy trình:
                1. Liên hệ hotline hoặc chat với chúng tôi
                2. Gửi sản phẩm về cửa hàng (hoặc chúng tôi đến lấy)
                3. Kiểm tra sản phẩm
                4. Đổi size/màu hoặc hoàn tiền
                
                ⚠️ Lưu ý: Không áp dụng cho sản phẩm sale/khuyến mãi
                """;
    }
    
    // Helper methods
    private String formatPrice(Double price) {
        if (price == null) return "N/A";
        return String.format("%,.0f đ", price);
    }
    
    private String getStatusText(ShipmentStatus status) {
        return switch (status) {
            case IN_STOCK -> "Chờ xác nhận";
            case SHIPPED -> "Đang giao hàng";
            case DELIVERED -> "Đã giao thành công";
            case CANCEL -> "Đã hủy";
            case RETURN -> "Đã trả hàng";
        };
    }
}

