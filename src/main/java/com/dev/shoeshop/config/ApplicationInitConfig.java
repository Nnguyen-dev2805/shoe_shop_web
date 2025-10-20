package com.dev.shoeshop.config;

import com.dev.shoeshop.entity.Brand;
import com.dev.shoeshop.entity.Category;
import com.dev.shoeshop.entity.Discount;
import com.dev.shoeshop.entity.Inventory;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.entity.Role;
import com.dev.shoeshop.entity.ShippingCompany;
import com.dev.shoeshop.entity.ShippingRate;
import com.dev.shoeshop.entity.ShopWarehouse;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.DiscountValueType;
import com.dev.shoeshop.enums.VoucherType;
import com.dev.shoeshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Tự động init data khi app khởi động lần đầu
 * Chỉ chạy 1 lần khi database rỗng
 */
@Component
@RequiredArgsConstructor
public class ApplicationInitConfig implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final ShippingCompanyRepository shippingCompanyRepository;
    private final ShopWarehouseRepository shopWarehouseRepository;
    private final ShippingRateRepository shippingRateRepository;
    private final DiscountRepository discountRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Chỉ init nếu database rỗng
        if (roleRepository.count() > 0) {
            System.out.println("✅ Database đã có data, skip initialization");
            return;
        }

        System.out.println("🚀 Bắt đầu khởi tạo data cơ bản...");

        // 1. Tạo Roles
        initRoles();

        // 2. Tạo Categories
        initCategories();

        // 3. Tạo Brands
        initBrands();

        // 4. Tạo Products & ProductDetails
        initProducts();

        // 5. Tạo Inventory (tồn kho)
        initInventory();

        // 6. Tạo Users
        initUsers();

        // 7. Tạo Shipping Companies
        initShippingCompanies();

        // 8. Tạo Shop Warehouse
        initWarehouse();

        // 9. Tạo Shipping Rates
        initShippingRates();

        // 10. Tạo Discounts/Vouchers
        initDiscounts();

        // 11. Tạo Triggers
        initTriggers();

        System.out.println("✅ Khởi tạo data thành công!");
    }

    private void initRoles() {
        Role admin = new Role();
        admin.setRoleName("admin");
        roleRepository.save(admin);

        Role manager = new Role();
        manager.setRoleName("manager");
        roleRepository.save(manager);

        Role user = new Role();
        user.setRoleName("user");
        roleRepository.save(user);

        Role shipper = new Role();
        shipper.setRoleName("shipper");
        roleRepository.save(shipper);

        System.out.println("  → Đã tạo 4 roles");
    }

    private void initCategories() {
        String[][] categories = {
            {"Giày Thể Thao", "Giày dành cho hoạt động thể thao, chạy bộ, gym, training"},
            {"Giày Sneaker", "Giày sneaker phong cách streetwear, thời trang đường phố"},
            {"Giày Bóng Đá", "Giày chuyên dụng cho cầu thủ bóng đá, hỗ trợ bám sân và di chuyển nhanh"},
            {"Giày Bóng Rổ", "Giày thể thao chuyên cho bóng rổ, hỗ trợ bật nhảy và bảo vệ cổ chân"},
            {"Sandals & Dép", "Dép, sandals thoáng mát, tiện dụng hàng ngày"}
        };

        for (int i = 0; i < categories.length; i++) {
            Category category = new Category();
            category.setName(categories[i][0]);
            category.setDescription(categories[i][1]);
            categoryRepository.save(category);
        }

        System.out.println("  → Đã tạo 5 categories");
    }

    private void initBrands() {
        String[] brandNames = {"Nike", "Adidas", "Puma", "Converse", "Vans", 
                               "New Balance", "Reebok", "Fila", "Under Armour", "Skechers"};

        for (int i = 0; i < brandNames.length; i++) {
            Brand brand = new Brand();
            brand.setName(brandNames[i]);
            brandRepository.save(brand);
        }

        System.out.println("  → Đã tạo 10 brands");
    }

    private void initProducts() {
        // Product 1: Giày Thể Thao Nam
        Product p1 = createProduct("Giày Thể Thao Nam",
            "Giày thể thao thiết kế hiện đại, phù hợp cho chạy bộ và tập gym", 1L, 1L, 1890000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-1_ofpkf9.jpg");
        createProductDetails(p1);

        // Product 2: Running Pro
        Product p2 = createProduct("Giày Thể Thao Running Pro", 
            "Giày chạy bộ chuyên nghiệp, đệm khí êm ái", 1L, 2L, 2350000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-2_wnj5lk.jpg");
        createProductDetails(p2);

        // Product 3: Training All Day
        Product p3 = createProduct("Giày Training All Day", 
            "Giày tập luyện đa năng, bám sân tốt", 1L, 3L, 1750000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893901/the-thao-3_rcptay.jpg");
        createProductDetails(p3);

        // Product 4-6: Sneakers
        Product p4 = createProduct("Sneaker Street Style Đỏ", 
            "Sneaker phong cách đường phố, màu đỏ nổi bật", 2L, 4L, 1590000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sneaker-1_ibssp8.jpg");
        createProductDetails(p4);

        Product p5 = createProduct("Sneaker Low-Top Trắng", 
            "Sneaker trắng basic, dễ phối đồ", 2L, 5L, 1450000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg");
        createProductDetails(p5);

        Product p6 = createProduct("Sneaker High-Top Canvas", 
            "Sneaker cổ cao vải canvas, style vintage", 2L, 4L, 1690000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/sneaker-3_pwjfu2.jpg");
        createProductDetails(p6);

        // Product 7-9: Bóng Đá
        Product p7 = createProduct("Giày Bóng Đá Sân Cỏ TF", 
            "Giày bóng đá sân cỏ nhân tạo, đế TF bám sân cực tốt", 3L, 1L, 2890000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/bong-da-1_jhx4pr.jpg");
        createProductDetails(p7);

        Product p8 = createProduct("Giày Bóng Đá Mercurial", 
            "Giày bóng đá tốc độ, thiết kế khí động học", 3L, 1L, 3250000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/bong-da-2_edzip1.jpg");
        createProductDetails(p8);

        Product p9 = createProduct("Giày Đá Banh Predator", 
            "Giày sút bóng chuẩn xác, công nghệ Control Frame", 3L, 2L, 3150000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/bong-da-3_dw2ngl.jpg");
        createProductDetails(p9);

        // Product 10-12: Bóng Rổ
        Product p10 = createProduct("Giày Bóng Rổ Air Jordan",
            "Giày bóng rổ cổ cao, bảo vệ cổ chân tối ưu", 4L, 1L, 4590000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893899/bong-ro-1_tpxjas.jpg");
        createProductDetails(p10);

        Product p11 = createProduct("Basketball Shoes Pro", 
            "Giày bóng rổ chuyên nghiệp, đế cao su chống trơn", 4L, 2L, 4150000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/bong-ro-2_bpgxk1.jpg");
        createProductDetails(p11);

        Product p12 = createProduct("Giày Bóng Rổ Harden Style", 
            "Thiết kế năng động, hỗ trợ bật nhảy", 4L, 2L, 3790000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/bong-ro-3_wnzw5d.jpg");
        createProductDetails(p12);

        // Product 13-15: Sandals & Dép
        Product p13 = createProduct("Dép Quai Ngang Thời Trang", 
            "Dép quai ngang êm ái, phù hợp mùa hè", 5L, 1L, 590000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/dep-1_irwrep.jpg");
        createProductDetails(p13);

        Product p14 = createProduct("Dép Adilette Classic", 
            "Dép thể thao iconic, thoáng mát", 5L, 2L, 750000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893895/dep-2_na32oy.jpg");
        createProductDetails(p14);

        Product p15 = createProduct("Sandal Outdoor Adventure", 
            "Sandal dã ngoại, đi phượt, leo núi", 5L, 9L, 1150000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p15);

        System.out.println("  → Đã tạo 15 products với 120 product details");
    }

    private Product createProduct(String title, String description, 
                                  Long categoryId, Long brandId, Integer price, String imageUrl) {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setImage(imageUrl);
        
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        Brand brand = brandRepository.findById(brandId).orElseThrow();
        
        product.setCategory(category);
        product.setBrand(brand);
        product.setPrice(price);
        
        return productRepository.save(product);
    }

    private void createProductDetails(Product product) {
        int[] sizes = {38, 39, 40, 41, 42, 43, 44, 45};
        int[] priceAdds = {0, 0, 0, 50000, 50000, 100000, 100000, 150000};

        for (int i = 0; i < sizes.length; i++) {
            ProductDetail detail = new ProductDetail();
            detail.setProduct(product);
            detail.setSize(sizes[i]);
            detail.setPriceadd(priceAdds[i]);
            productDetailRepository.save(detail);
        }
    }

    private void initInventory() {
        // Lấy tất cả ProductDetails đã tạo (15 products × 8 sizes = 120 product details)
        var allProductDetails = productDetailRepository.findAll();
        
        int inventoryCount = 0;
        
        // Tạo inventory cho mỗi ProductDetail với quantity = 10
        for (ProductDetail productDetail : allProductDetails) {
            Inventory inventory = new Inventory();
            inventory.setProductDetail(productDetail);
            inventory.setQuantity(30);  // Mỗi size có 10 sản phẩm tồn kho
            inventoryRepository.save(inventory);
            inventoryCount++;
        }
        
        System.out.println("  → Đã tạo " + inventoryCount + " inventory records (mỗi size 30 items)");
    }

    private void initUsers() {
        // Password: 123456
        String encodedPassword = passwordEncoder.encode("123456");

        // Admin
        Users admin = new Users();
        admin.setEmail("admin@admin");
        admin.setPassword(encodedPassword);
        admin.setFullname("Admin");
        admin.setPhone("0901234567");
        admin.setRole(roleRepository.findByRoleName("admin").orElseThrow());
        admin.setIsActive(true);
        admin.setProvider("LOCAL");
        userRepository.save(admin);

        // Manager
        Users manager = new Users();
        manager.setEmail("manager@manager");
        manager.setPassword(encodedPassword);
        manager.setFullname("Manager");
        manager.setPhone("0902345678");
        manager.setRole(roleRepository.findByRoleName("manager").orElseThrow());
        manager.setIsActive(true);
        manager.setProvider("LOCAL");
        userRepository.save(manager);

        // User
        Users user = new Users();
        user.setEmail("user@user");
        user.setPassword(encodedPassword);
        user.setFullname("User");
        user.setPhone("0903456789");
        user.setRole(roleRepository.findByRoleName("user").orElseThrow());
        user.setIsActive(true);
        user.setProvider("LOCAL");
        userRepository.save(user);

        // Shipper
        Users shipper = new Users();
        shipper.setEmail("shipper@shipper");
        shipper.setPassword(encodedPassword);
        shipper.setFullname("Shipper");
        shipper.setPhone("0904567890");
        shipper.setRole(roleRepository.findByRoleName("shipper").orElseThrow());
        shipper.setIsActive(true);
        shipper.setProvider("LOCAL");
        userRepository.save(shipper);

        System.out.println("  → Đã tạo 4 users (password: 123456)");
    }

    private void initShippingCompanies() {
        // Các công ty vận chuyển phổ biến tại Việt Nam
        
        ShippingCompany ghn = new ShippingCompany();
        ghn.setName("Giao Hàng Nhanh (GHN)");
        ghn.setHotline("1900 636677");
        ghn.setEmail("hotro@ghn.vn");
        ghn.setAddress("Tầng 6, Toà nhà Ladeco, 266 Đội Cấn, Ba Đình, Hà Nội");
        ghn.setWebsite("https://ghn.vn");
        ghn.setIsActive(true);
        shippingCompanyRepository.save(ghn);

        ShippingCompany ghtk = new ShippingCompany();
        ghtk.setName("Giao Hàng Tiết Kiệm (GHTK)");
        ghtk.setHotline("1900 636677");
        ghtk.setEmail("hotro@giaohangtietkiem.vn");
        ghtk.setAddress("Tầng 6, Số 1 Trần Hữu Dực, Mỹ Đình, Nam Từ Liêm, Hà Nội");
        ghtk.setWebsite("https://giaohangtietkiem.vn");
        ghtk.setIsActive(true);
        shippingCompanyRepository.save(ghtk);

        ShippingCompany jnt = new ShippingCompany();
        jnt.setName("J&T Express");
        jnt.setHotline("1900 1088");
        jnt.setEmail("cskh@jtexpress.vn");
        jnt.setAddress("Tầng 6, Mapletree Business Centre, 1060 Nguyễn Văn Linh, Quận 7, TP.HCM");
        jnt.setWebsite("https://jtexpress.vn");
        jnt.setIsActive(true);
        shippingCompanyRepository.save(jnt);

        ShippingCompany viettelPost = new ShippingCompany();
        viettelPost.setName("Viettel Post");
        viettelPost.setHotline("1900 8095");
        viettelPost.setEmail("cskh@viettelpost.vn");
        viettelPost.setAddress("Tầng 6, Toà Hà Nội Paragon, 86 Duy Tân, Cầu Giấy, Hà Nội");
        viettelPost.setWebsite("https://viettelpost.vn");
        viettelPost.setIsActive(true);
        shippingCompanyRepository.save(viettelPost);

        ShippingCompany vnpost = new ShippingCompany();
        vnpost.setName("Bưu Điện Việt Nam (VNPost)");
        vnpost.setHotline("1900 54 54 81");
        vnpost.setEmail("cskh@vnpost.vn");
        vnpost.setAddress("6B Phạm Hùng, Nam Từ Liêm, Hà Nội");
        vnpost.setWebsite("https://vnpost.vn");
        vnpost.setIsActive(true);
        shippingCompanyRepository.save(vnpost);

        ShippingCompany ninjavan = new ShippingCompany();
        ninjavan.setName("Ninja Van");
        ninjavan.setHotline("1900 886");
        ninjavan.setEmail("support.vn@ninjavan.co");
        ninjavan.setAddress("Tầng 5, Toà nhà Waseco, 10 Phổ Quang, Tân Bình, TP.HCM");
        ninjavan.setWebsite("https://ninjavan.co/vi-vn");
        ninjavan.setIsActive(true);
        shippingCompanyRepository.save(ninjavan);

        ShippingCompany bestExpress = new ShippingCompany();
        bestExpress.setName("Best Express");
        bestExpress.setHotline("1900 888 870");
        bestExpress.setEmail("cskh@best-inc.vn");
        bestExpress.setAddress("Tầng 3, Toà nhà The Sun Avenue, 28 Mai Chí Thọ, Quận 2, TP.HCM");
        bestExpress.setWebsite("https://www.best-inc.vn");
        bestExpress.setIsActive(true);
        shippingCompanyRepository.save(bestExpress);

        System.out.println("  → Đã tạo 7 shipping companies");
    }

    private void initWarehouse() {
        // Tạo kho mặc định tại TP.HCM
        ShopWarehouse mainWarehouse = new ShopWarehouse();
        mainWarehouse.setName("Kho Trung Tâm DeeG Shop");
        mainWarehouse.setAddress("ĐH Sư Phạm Kỹ Thuật TP, HCM, 1 Võ Văn Ngân, P, Linh Chiểu, Q, Thủ Đức");
        mainWarehouse.setLatitude(10.850231800892672);  // Tọa độ thực tế khu vực Nguyễn Huệ
        mainWarehouse.setLongitude(106.77203051676167);
        mainWarehouse.setCity("Hồ Chí Minh");
        mainWarehouse.setPhone("028 3822 5678");
        mainWarehouse.setIsActive(true);
        mainWarehouse.setIsDefault(true);  // Kho mặc định
        shopWarehouseRepository.save(mainWarehouse);

        System.out.println("  → Đã tạo 1 shop warehouse (kho mặc định)");
    }

    private void initShippingRates() {
        // Tạo bảng giá ship theo khoảng cách (theo km)
        
        ShippingRate rate1 = new ShippingRate();
        rate1.setMinDistanceKm(new BigDecimal("0.00"));
        rate1.setMaxDistanceKm(new BigDecimal("3.00"));
        rate1.setFee(15000);
        rate1.setDescription("Nội thành - dưới 3km");
        rate1.setIsActive(true);
        shippingRateRepository.save(rate1);

        ShippingRate rate2 = new ShippingRate();
        rate2.setMinDistanceKm(new BigDecimal("3.01"));
        rate2.setMaxDistanceKm(new BigDecimal("5.00"));
        rate2.setFee(20000);
        rate2.setDescription("Nội thành - 3-5km");
        rate2.setIsActive(true);
        shippingRateRepository.save(rate2);

        ShippingRate rate3 = new ShippingRate();
        rate3.setMinDistanceKm(new BigDecimal("5.01"));
        rate3.setMaxDistanceKm(new BigDecimal("10.00"));
        rate3.setFee(30000);
        rate3.setDescription("Ngoại thành - 5-10km");
        rate3.setIsActive(true);
        shippingRateRepository.save(rate3);

        ShippingRate rate4 = new ShippingRate();
        rate4.setMinDistanceKm(new BigDecimal("10.01"));
        rate4.setMaxDistanceKm(new BigDecimal("20.00"));
        rate4.setFee(45000);
        rate4.setDescription("Ngoại thành xa - 10-20km");
        rate4.setIsActive(true);
        shippingRateRepository.save(rate4);

        ShippingRate rate5 = new ShippingRate();
        rate5.setMinDistanceKm(new BigDecimal("20.01"));
        rate5.setMaxDistanceKm(new BigDecimal("50.00"));
        rate5.setFee(60000);
        rate5.setDescription("Vùng xa - 20-50km");
        rate5.setIsActive(true);
        shippingRateRepository.save(rate5);

        ShippingRate rate6 = new ShippingRate();
        rate6.setMinDistanceKm(new BigDecimal("50.01"));
        rate6.setMaxDistanceKm(new BigDecimal("999999.00"));
        rate6.setFee(80000);
        rate6.setDescription("Vùng rất xa - trên 50km");
        rate6.setIsActive(true);
        shippingRateRepository.save(rate6);

        System.out.println("  → Đã tạo 6 shipping rates");
    }

    private void initDiscounts() {
        LocalDate today = LocalDate.now();
        
        // 1. VOUCHER GIẢM GIÁ ĐƠN HÀNG - Giảm %
        Discount orderPercent = new Discount();
        orderPercent.setName("Giảm 20% đơn hàng đầu tiên");
        orderPercent.setQuantity(500);
        orderPercent.setPercent(0.20);  // 20%
        orderPercent.setStatus("ACTIVE");
        orderPercent.setMinOrderValue(500000.0);  // Đơn tối thiểu 500k
        orderPercent.setStartDate(today);
        orderPercent.setEndDate(today.plusMonths(1));
        orderPercent.setType(VoucherType.ORDER_DISCOUNT);
        orderPercent.setDiscountValueType(DiscountValueType.PERCENTAGE);
        orderPercent.setMaxDiscountAmount(100000.0);  // Giảm tối đa 100k
        orderPercent.setIsDelete(false);
        discountRepository.save(orderPercent);

        // 2. VOUCHER GIẢM GIÁ ĐƠN HÀNG - Giảm cố định
        Discount orderFixed = new Discount();
        orderFixed.setName("Giảm 50.000đ cho đơn từ 300k");
        orderFixed.setQuantity(1000);
        orderFixed.setPercent(50000.0);  // Số tiền giảm (field percent dùng chung)
        orderFixed.setStatus("ACTIVE");
        orderFixed.setMinOrderValue(300000.0);
        orderFixed.setStartDate(today);
        orderFixed.setEndDate(today.plusMonths(3));
        orderFixed.setType(VoucherType.ORDER_DISCOUNT);
        orderFixed.setDiscountValueType(DiscountValueType.FIXED_AMOUNT);
        orderFixed.setIsDelete(false);
        discountRepository.save(orderFixed);

        // 3. VOUCHER MIỄN PHÍ SHIP - 100%
        Discount freeShip = new Discount();
        freeShip.setName("Freeship 100% đơn từ 500k");
        freeShip.setQuantity(300);
        freeShip.setPercent(1.0);  // 100%
        freeShip.setStatus("ACTIVE");
        freeShip.setMinOrderValue(500000.0);
        freeShip.setStartDate(today);
        freeShip.setEndDate(today.plusDays(15));
        freeShip.setType(VoucherType.SHIPPING_DISCOUNT);
        freeShip.setDiscountValueType(DiscountValueType.PERCENTAGE);
        freeShip.setMaxDiscountAmount(50000.0);  // Giảm tối đa 50k
        freeShip.setIsDelete(false);
        discountRepository.save(freeShip);

        // 4. VOUCHER GIẢM PHÍ SHIP - Giảm %
        Discount shipPercent = new Discount();
        shipPercent.setName("Giảm 50% phí ship (tối đa 20k)");
        shipPercent.setQuantity(800);
        shipPercent.setPercent(0.50);  // 50%
        shipPercent.setStatus("ACTIVE");
        shipPercent.setMinOrderValue(200000.0);
        shipPercent.setStartDate(today);
        shipPercent.setEndDate(today.plusMonths(2));
        shipPercent.setType(VoucherType.SHIPPING_DISCOUNT);
        shipPercent.setDiscountValueType(DiscountValueType.PERCENTAGE);
        shipPercent.setMaxDiscountAmount(20000.0);
        shipPercent.setIsDelete(false);
        discountRepository.save(shipPercent);

        // 5. VOUCHER GIẢM PHÍ SHIP - Giảm cố định
        Discount shipFixed = new Discount();
        shipFixed.setName("Giảm 30.000đ phí ship");
        shipFixed.setQuantity(600);
        shipFixed.setPercent(30000.0);  // Giảm cố định 30k
        shipFixed.setStatus("ACTIVE");
        shipFixed.setMinOrderValue(0.0);  // Không yêu cầu tối thiểu
        shipFixed.setStartDate(today);
        shipFixed.setEndDate(today.plusMonths(1));
        shipFixed.setType(VoucherType.SHIPPING_DISCOUNT);
        shipFixed.setDiscountValueType(DiscountValueType.FIXED_AMOUNT);
        shipFixed.setIsDelete(false);
        discountRepository.save(shipFixed);

        // 6. VOUCHER SẮP DIỄN RA - Flash Sale
        Discount comingSoon = new Discount();
        comingSoon.setName("Flash Sale - Giảm 30% đơn từ 1 triệu");
        comingSoon.setQuantity(100);
        comingSoon.setPercent(0.30);  // 30%
        comingSoon.setStatus("COMING");
        comingSoon.setMinOrderValue(1000000.0);
        comingSoon.setStartDate(today.plusDays(3));
        comingSoon.setEndDate(today.plusDays(10));
        comingSoon.setType(VoucherType.ORDER_DISCOUNT);
        comingSoon.setDiscountValueType(DiscountValueType.PERCENTAGE);
        comingSoon.setMaxDiscountAmount(200000.0);
        comingSoon.setIsDelete(false);
        discountRepository.save(comingSoon);

        System.out.println("  → Đã tạo 6 vouchers (3 order discount, 3 shipping discount)");
    }

    private void initTriggers() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            System.out.println("  🔧 Đang tạo triggers...");

            // Drop existing triggers trước
            dropTriggersIfExist(statement);

            // 1. INVENTORY TRIGGERS
            createInventoryTriggers(statement);

            // 2. ORDER STATUS TRIGGERS  
            createOrderStatusTriggers(statement);

            // 3. RATING TRIGGERS
            createRatingTriggers(statement);

            System.out.println("  → Đã tạo tất cả triggers thành công!");

        } catch (Exception e) {
            System.err.println("  ⚠️ Không thể tạo triggers: " + e.getMessage());
            // Không throw exception, vì triggers không critical cho app startup
        }
    }

    private void dropTriggersIfExist(Statement statement) throws Exception {
        String[] triggers = {
            "after_order_detail_insert",
            "handle_order_status_change",
            "validate_inventory_before_shipped",
            "tr_rating_insert"
        };

        for (String trigger : triggers) {
            try {
                statement.execute("DROP TRIGGER IF EXISTS " + trigger);
            } catch (Exception e) {
                // Ignore nếu trigger không tồn tại
            }
        }
    }

    private void createInventoryTriggers(Statement statement) throws Exception {
        // Trigger 1: Trừ kho ngay khi đặt hàng
        String trigger1 = """
            CREATE TRIGGER after_order_detail_insert
            BEFORE INSERT ON order_detail
            FOR EACH ROW
            BEGIN
                DECLARE v_current_stock BIGINT;
                DECLARE v_product_name VARCHAR(255);
                DECLARE v_error_message VARCHAR(500);
                
                SELECT i.quantity INTO v_current_stock
                FROM inventory i
                WHERE i.product_detail_id = NEW.productdetail_id
                LIMIT 1;
                
                IF v_current_stock IS NULL THEN
                    SELECT p.title INTO v_product_name
                    FROM product_detail pd
                    JOIN product p ON pd.product_id = p.id
                    WHERE pd.id = NEW.productdetail_id;
                    
                    SET v_error_message = CONCAT('Không tìm thấy sản phẩm trong kho: ', COALESCE(v_product_name, 'Unknown'));
                    
                    SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = v_error_message;
                    
                ELSEIF v_current_stock < NEW.quantity THEN
                    SELECT p.title INTO v_product_name
                    FROM product_detail pd
                    JOIN product p ON pd.product_id = p.id
                    WHERE pd.id = NEW.productdetail_id;
                    
                    SET v_error_message = CONCAT('Không đủ hàng trong kho! Sản phẩm: ', COALESCE(v_product_name, 'Unknown'), 
                                                  ' - Tồn kho: ', v_current_stock, 
                                                  ' - Yêu cầu: ', NEW.quantity);
                    
                    SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = v_error_message;
                END IF;
                
                UPDATE inventory
                SET quantity = quantity - NEW.quantity
                WHERE product_detail_id = NEW.productdetail_id;
            END
            """;

        statement.execute(trigger1);
        System.out.println("    ✓ after_order_detail_insert");
    }

    private void createOrderStatusTriggers(Statement statement) throws Exception {
        // Trigger 2: Xử lý khi status thay đổi
        String trigger2 = """
            CREATE TRIGGER handle_order_status_change
            AFTER UPDATE ON orders
            FOR EACH ROW
            BEGIN
                IF NEW.status = 'DELIVERED' AND OLD.status != 'DELIVERED' THEN
                    UPDATE product p
                    INNER JOIN product_detail pd ON p.id = pd.product_id
                    INNER JOIN order_detail od ON pd.id = od.productdetail_id
                    SET p.sold_quantity = p.sold_quantity + od.quantity
                    WHERE od.order_id = NEW.id;
                END IF;
                
                IF NEW.status = 'CANCEL' AND OLD.status != 'CANCEL' THEN
                    UPDATE inventory i
                    INNER JOIN order_detail od ON i.product_detail_id = od.productdetail_id
                    SET i.quantity = i.quantity + od.quantity
                    WHERE od.order_id = NEW.id;
                END IF;
                
                IF NEW.status = 'RETURN' AND OLD.status = 'DELIVERED' THEN
                    UPDATE inventory i
                    INNER JOIN order_detail od ON i.product_detail_id = od.productdetail_id
                    SET i.quantity = i.quantity + od.quantity
                    WHERE od.order_id = NEW.id;
                    
                    UPDATE product p
                    INNER JOIN product_detail pd ON p.id = pd.product_id
                    INNER JOIN order_detail od ON pd.id = od.productdetail_id
                    SET p.sold_quantity = GREATEST(0, p.sold_quantity - od.quantity)
                    WHERE od.order_id = NEW.id;
                END IF;
            END
            """;

        statement.execute(trigger2);
        System.out.println("    ✓ handle_order_status_change");
    }

    private void createRatingTriggers(Statement statement) throws Exception {
        // Trigger 3: Cập nhật rating khi có đánh giá mới
        String trigger3 = """
            CREATE TRIGGER tr_rating_insert
            AFTER INSERT ON rating
            FOR EACH ROW
            BEGIN
                UPDATE product 
                SET 
                    total_reviewers = total_reviewers + 1,
                    total_stars = total_stars + NEW.star,
                    average_rating = CASE 
                        WHEN (total_reviewers + 1) > 0 THEN (total_stars + NEW.star) / (total_reviewers + 1)
                        ELSE 0.0
                    END
                WHERE id = NEW.product_id;
            END
            """;

        statement.execute(trigger3);
        System.out.println("    ✓ tr_rating_insert");
    }
}
