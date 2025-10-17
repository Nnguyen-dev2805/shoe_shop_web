package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.flashsale.request.BulkAddItemsRequest;
import com.dev.shoeshop.dto.flashsale.request.PurchaseFlashSaleRequest;
import com.dev.shoeshop.dto.flashsale.response.FlashSaleItemResponse;
import com.dev.shoeshop.dto.flashsale.response.FlashSaleResponse;
import com.dev.shoeshop.dto.flashsale.response.StockResponse;
import com.dev.shoeshop.dto.response.ProductWithFlashSaleResponse;
import com.dev.shoeshop.entity.*;
import com.dev.shoeshop.enums.FlashSaleStatus;
import com.dev.shoeshop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service xử lý business logic cho Flash Sale
 * Core layer - QUAN TRỌNG NHẤT của hệ thống Flash Sale
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleService {
    
    private final FlashSaleRepository flashSaleRepo;
    private final FlashSaleItemRepository flashSaleItemRepo;
    private final ProductDetailRepository productDetailRepo;
    private final InventoryRepository inventoryRepo;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    
    /**
     * Lấy flash sale đang ACTIVE (đang diễn ra)
     * 
     * @return FlashSaleResponse hoặc null nếu không có flash sale active
     * 
     * Dùng cho: Homepage hiển thị flash sale đang diễn ra
     * 
     * Flow:
     * 1. Query flash sale có status=ACTIVE và trong khoảng thời gian
     * 2. Load items của flash sale
     * 3. Convert entity → DTO
     * 4. Return response
     */
    public FlashSaleResponse getActiveFlashSale() {
        log.info("Getting active flash sale");
        
        FlashSale flashSale = flashSaleRepo.findActiveFlashSale(
            FlashSaleStatus.ACTIVE,
            LocalDateTime.now()
        ).orElse(null);
        
        if (flashSale == null) {
            log.info("No active flash sale found");
            return null;
        }
        
        // Load items của flash sale
        List<FlashSaleItem> items = flashSaleItemRepo.findByFlashSaleIdWithProduct(flashSale.getId());
        
        // Convert sang DTO
        return convertToResponse(flashSale, items);
    }
    
    /**
     * Lấy flash sale sắp diễn ra (upcoming)
     * 
     * @return FlashSaleResponse hoặc null nếu không có
     * 
     * Dùng cho: Homepage hiển thị countdown "Sắp bắt đầu"
     * 
     * Flow:
     * 1. Query flash sale có status=SCHEDULED và startTime > now
     * 2. Lấy flash sale gần nhất
     * 3. Convert entity → DTO
     */
    public FlashSaleResponse getUpcomingFlashSale() {
        log.info("Getting upcoming flash sale");
        
        FlashSale flashSale = flashSaleRepo.findUpcomingFlashSale(
            FlashSaleStatus.SCHEDULED,
            LocalDateTime.now()
        ).orElse(null);
        
        if (flashSale == null) {
            log.info("No upcoming flash sale found");
            return null;
        }
        
        // Load items
        List<FlashSaleItem> items = flashSaleItemRepo.findByFlashSaleIdWithProduct(flashSale.getId());
        
        return convertToResponse(flashSale, items);
    }
    
    /**
     * MUA SẢN PHẨM FLASH SALE - METHOD QUAN TRỌNG NHẤT
     * 
     * @param request Request chứa flashSaleItemId và quantity
     * @param userId ID của user đang mua
     * @return Order đã tạo
     * @throws FlashSaleException nếu có lỗi
     * 
     * Flow xử lý (QUAN TRỌNG - ĐỌC KỸ):
     * 1. LOCK flash sale item (Pessimistic Lock) → Tránh overselling
     * 2. Validate flash sale còn ACTIVE không → Timeout protection
     * 3. Validate còn stock không → Prevent overselling
     * 4. CHECK INVENTORY thật còn đủ hàng không → Inventory sync
     * 5. TRỪ STOCK flash sale
     * 6. TẠO ORDER với giá flash sale
     * 7. LINK order với flash sale
     * 8. Update total sold của flash sale
     * 9. Return order
     * 
     * ⚠️ Transaction: Method này chạy trong transaction
     * ⚠️ Lock: Dùng pessimistic lock để tránh race condition
     */
    @Transactional
    public Order purchaseFlashSaleItem(PurchaseFlashSaleRequest request, Long userId) {
        log.info("User {} purchasing flash sale item {}, quantity: {}", 
                 userId, request.getFlashSaleItemId(), request.getQuantity());
        
        // BƯỚC 1: LOCK flash sale item (QUAN TRỌNG - Tránh overselling)
        FlashSaleItem item = flashSaleItemRepo.findByIdWithLock(request.getFlashSaleItemId())
            .orElseThrow(() -> new FlashSaleException("Sản phẩm flash sale không tồn tại"));
        
        // BƯỚC 2: VALIDATE flash sale còn active không (Timeout protection)
        FlashSale flashSale = item.getFlashSale();
        if (!flashSale.isActive()) {
            log.warn("Flash sale {} is not active, status: {}", flashSale.getId(), flashSale.getStatus());
            throw new FlashSaleException("Rất tiếc, Flash Sale đã kết thúc!");
        }
        
        // Kiểm tra thời gian
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(flashSale.getEndTime())) {
            log.warn("Flash sale {} has ended", flashSale.getId());
            throw new FlashSaleException("Flash Sale đã hết giờ! Vui lòng chọn sản phẩm khác.");
        }
        
        // BƯỚC 3: CHECK INVENTORY còn hàng không (Inventory sync)
        ProductDetail productDetail = item.getProductDetail();
        int availableStock = inventoryRepo.getTotalQuantityByProductDetail(productDetail);
        
        if (availableStock < request.getQuantity()) {
            log.warn("Not enough inventory for product detail {}, available: {}, requested: {}",
                     productDetail.getId(), availableStock, request.getQuantity());
            throw new FlashSaleException(
                String.format("Không đủ hàng! Chỉ còn %d sản phẩm", availableStock)
            );
        }
        
        // BƯỚC 4: TẠO ORDER (inventory sẽ tự động trừ trong OrderService)
        
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new FlashSaleException("User không tồn tại"));
        
        // Tính giá
        double itemTotalPrice = item.getFlashSalePrice() * request.getQuantity();
        double originalTotalPrice = item.getOriginalPrice() * request.getQuantity();
        
        // Tạo order
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(itemTotalPrice);
        order.setOriginalTotalPrice(originalTotalPrice);
        // -- Đoạn này fix đau đầu
//        order.setCreatedDate(LocalDateTime.now());
        order.setCreatedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        
        // LINK order với flash sale
        order.setAppliedFlashSale(flashSale);
        order.setDiscountAmount(originalTotalPrice - itemTotalPrice);
        
        // Save order
        order = orderRepository.save(order);
        
        // Update flash sale total sold
        flashSale.incrementSold(request.getQuantity());
        flashSaleRepo.save(flashSale);
        
        log.info("Order created successfully for user {}, order total: {}", userId, itemTotalPrice);
        
        return order;
    }
    
    /**
     * Lấy thông tin stock của một flash sale item
     * 
     * @param itemId ID của flash sale item
     * @return StockResponse chứa thông tin stock
     * 
     * Dùng cho: AJAX polling - update stock real-time trên UI
     * 
     * Frontend sẽ gọi API này mỗi 3-5 giây để update progress bar
     */
    public StockResponse getStockInfo(Long itemId) {
        FlashSaleItem item = flashSaleItemRepo.findById(itemId)
            .orElseThrow(() -> new FlashSaleException("Flash sale item không tồn tại"));
        
        // Lấy stock từ inventory
        ProductDetail pd = item.getProductDetail();
        int totalStock = inventoryRepo.getTotalQuantityByProductDetail(pd);
        
        return new StockResponse(
            totalStock,
            0, // Sold không track nữa
            totalStock,
            0.0 // Sold percentage không track
        );
    }
    
    /**
     * BULK ADD - Thêm nhiều sản phẩm vào flash sale cùng lúc
     * 
     * @param flashSaleId ID của flash sale
     * @param request Request chứa danh sách product IDs và discount
     * @return Số lượng sản phẩm đã thêm
     * 
     * Dùng cho: Admin thêm sản phẩm nhanh chóng
     * 
     * Flow:
     * 1. Validate flash sale tồn tại
     * 2. Loop qua từng productDetailId
     * 3. Lấy thông tin product detail
     * 4. Tính giá flash sale từ discountPercent
     * 5. Tạo FlashSaleItem
     * 6. Bulk save tất cả items
     * 7. Update flash_sale.total_items
     * 
     * VD: Admin chọn 20 sản phẩm → Giảm 50% → Mỗi sản phẩm 10 đôi
     * → Thêm 20 items trong 1 lần
     */
    @Transactional
    public int bulkAddItems(Long flashSaleId, BulkAddItemsRequest request) {
        log.info("Bulk adding {} items to flash sale {}", 
                 request.getProductDetailIds().size(), flashSaleId);
        
        // Validate flash sale tồn tại
        FlashSale flashSale = flashSaleRepo.findById(flashSaleId)
            .orElseThrow(() -> new FlashSaleException("Flash sale không tồn tại"));
        
        List<FlashSaleItem> items = new ArrayList<>();
        int position = flashSaleItemRepo.countByFlashSaleId(flashSaleId).intValue();
        
        // Loop qua từng product
        for (Long productDetailId : request.getProductDetailIds()) {
            ProductDetail productDetail = productDetailRepo.findById(productDetailId)
                .orElseThrow(() -> new FlashSaleException("Product detail " + productDetailId + " không tồn tại"));
            
            // Tính giá gốc và giá flash sale
            // ✅ Flash Sale CHỈ áp dụng trên BASE PRICE, KHÔNG bao gồm size fee
            double basePrice = productDetail.getProduct().getPrice();
            double flashPrice = basePrice * (1 - request.getDiscountPercent() / 100.0);
            double originalPrice = basePrice;
            
            // Tạo flash sale item (không có stock/sold nữa)
            FlashSaleItem item = FlashSaleItem.builder()
                .flashSale(flashSale)
                .productDetail(productDetail)
                .originalPrice(originalPrice)
                .flashSalePrice(flashPrice)
                .discountPercent(request.getDiscountPercent())  // ✅ Thêm discount percent
                .position(position++)
                .build();
            
            items.add(item);
        }
        
        // Bulk save - NHANH hơn save từng cái
        flashSaleItemRepo.saveAll(items);
        
        // Update total items
        flashSale.setTotalItems(flashSale.getTotalItems() + items.size());
        flashSaleRepo.save(flashSale);
        
        log.info("Successfully added {} items to flash sale {}", items.size(), flashSaleId);
        return items.size();
    }
    
    /**
     * Lấy danh sách items của flash sale
     * 
     * @param flashSaleId ID của flash sale
     * @return List FlashSaleItemResponse
     * 
     * Dùng cho: Hiển thị danh sách sản phẩm
     */
    public List<FlashSaleItemResponse> getFlashSaleItems(Long flashSaleId) {
        List<FlashSaleItem> items = flashSaleItemRepo.findByFlashSaleIdWithProduct(flashSaleId);
        
        List<FlashSaleItemResponse> responses = new ArrayList<>();
        for (FlashSaleItem item : items) {
            responses.add(convertToItemResponse(item));
        }
        
        return responses;
    }
    
    // ========== PRIVATE HELPER METHODS ==========
    
    /**
     * Convert FlashSale entity → FlashSaleResponse DTO
     */
    private FlashSaleResponse convertToResponse(FlashSale flashSale, List<FlashSaleItem> items) {
        List<FlashSaleItemResponse> itemResponses = new ArrayList<>();
        for (FlashSaleItem item : items) {
            itemResponses.add(convertToItemResponse(item));
        }
        
        return FlashSaleResponse.builder()
            .id(flashSale.getId())
            .name(flashSale.getName())
            .description(flashSale.getDescription())
            .startTime(flashSale.getStartTime())
            .endTime(flashSale.getEndTime())
            .status(flashSale.getStatus().name())
            .totalItems(flashSale.getTotalItems())
            .totalSold(flashSale.getTotalSold())
            .bannerImage(flashSale.getBannerImage())
            .items(itemResponses)
            .build();
    }
    
    /**
     * Convert FlashSaleItem entity → FlashSaleItemResponse DTO
     */
    private FlashSaleItemResponse convertToItemResponse(FlashSaleItem item) {
        ProductDetail pd = item.getProductDetail();
        Product product = pd.getProduct();
        
        // Lấy stock từ inventory
        int totalStock = inventoryRepo.getTotalQuantityByProductDetail(pd);
        
        return FlashSaleItemResponse.builder()
            .id(item.getId())
            .productDetailId(pd.getId())
            .productName(product.getTitle())
            .productImage(product.getImage())
            .size(pd.getSize())
            .originalPrice(item.getOriginalPrice())
            .flashSalePrice(item.getFlashSalePrice())
            .discountPercent(item.getDiscountPercent())
            .stock(totalStock) // Từ inventory
            .sold(0) // Không track sold nữa
            .remaining(totalStock) // Từ inventory
            .build();
    }
    
    /**
     * ========================================
     * THÊM PRODUCT VÀO FLASH SALE
     * Tự động tạo FlashSaleItem cho TẤT CẢ sizes
     * ========================================
     * 
     * @param flashSaleId ID của flash sale
     * @param productId ID của product
     * @param discountPercent % giảm giá (VD: 50 = giảm 50%)
     * @return số lượng FlashSaleItem đã tạo
     * 
     * Flow:
     * 1. Lấy flash sale
     * 2. Lấy tất cả ProductDetails của Product
     * 3. Với mỗi ProductDetail:
     *    - Tính flash sale price = original price * (1 - discountPercent/100)
     *    - Tạo FlashSaleItem
     * 4. Save tất cả FlashSaleItems
     * 5. Update flash sale total_items
     */
    @Transactional
    public int addProductToFlashSale(Long flashSaleId, Long productId, Double discountPercent) {
        log.info("Adding product {} to flash sale {} with {}% discount", 
                 productId, flashSaleId, discountPercent);
        
        // 1. Validate flash sale tồn tại
        FlashSale flashSale = flashSaleRepo.findById(flashSaleId)
            .orElseThrow(() -> new FlashSaleException("Flash sale không tồn tại!"));
        
        // 2. Validate discount percent
        if (discountPercent == null || discountPercent <= 0 || discountPercent >= 100) {
            throw new FlashSaleException("Discount percent phải từ 1-99%!");
        }
        
        // 3. Lấy tất cả ProductDetails của Product
        List<ProductDetail> productDetails = productDetailRepo.findByProductId(productId);
        
        if (productDetails.isEmpty()) {
            throw new FlashSaleException("Product không có ProductDetail nào!");
        }
        
        log.info("Found {} product details for product {}", productDetails.size(), productId);
        
        // 4. Tạo FlashSaleItem cho từng ProductDetail
        List<FlashSaleItem> flashSaleItems = new ArrayList<>();
        int position = flashSaleItemRepo.countByFlashSaleId(flashSaleId).intValue();
        
        for (ProductDetail productDetail : productDetails) {
            // Kiểm tra đã tồn tại trong flash sale chưa
            boolean exists = flashSaleItemRepo.existsByFlashSaleIdAndProductDetailId(
                flashSaleId, productDetail.getId()
            );
            
            if (exists) {
                log.warn("ProductDetail {} đã tồn tại trong flash sale, skip", 
                         productDetail.getId());
                continue;
            }
            
            // Tính flash sale price
            // ✅ Flash Sale CHỈ áp dụng trên BASE PRICE, KHÔNG bao gồm size fee
            // Size fee sẽ được cộng thêm ở frontend khi user chọn size
            Double basePrice = productDetail.getProduct().getPrice(); // Base price only
            Double flashSalePrice = basePrice * (1 - discountPercent / 100); // Discount on base only
            
            // originalPrice cũng chỉ lưu base price để hiển thị đúng
            Double originalPrice = basePrice;
            
            // Tạo FlashSaleItem
            FlashSaleItem item = FlashSaleItem.builder()
                .flashSale(flashSale)
                .productDetail(productDetail)
                .originalPrice(originalPrice)
                .flashSalePrice(flashSalePrice)
                .discountPercent(discountPercent)
                .position(position++)
                .build();
            
            flashSaleItems.add(item);
            
            log.info("Created flash sale item: ProductDetail {} - Base: {} - Flash: {} (-{}%) [Size fee will be added at frontend]",
                     productDetail.getId(), originalPrice, flashSalePrice, discountPercent);
        }
        
        // 5. Save tất cả items
        if (!flashSaleItems.isEmpty()) {
            flashSaleItemRepo.saveAll(flashSaleItems);
            
            // Update flash sale total_items
            flashSale.setTotalItems(flashSale.getTotalItems() + flashSaleItems.size());
            flashSaleRepo.save(flashSale);
            
            log.info("Successfully added {} flash sale items for product {}", 
                     flashSaleItems.size(), productId);
        }
        
        return flashSaleItems.size();
    }
    
    /**
     * Lấy danh sách Products trong Flash Sale hiện tại
     * Trả về 1 Product duy nhất (không duplicate theo size)
     * Kèm thông tin flash sale (giá thấp nhất, stock tổng)
     * 
     * @param flashSaleId ID của flash sale
     * @return List các product (không duplicate)
     */
    public List<ProductWithFlashSaleResponse> getProductsInFlashSale(Long flashSaleId) {
        // TODO: Implement sau khi có ProductRepository methods
        return new ArrayList<>();
    }
}

/**
 * Custom exception cho Flash Sale
 */
class FlashSaleException extends RuntimeException {
    public FlashSaleException(String message) {
        super(message);
    }
}
