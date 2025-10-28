package com.dev.shoeshop.config;

import com.dev.shoeshop.entity.Address;
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
import com.dev.shoeshop.entity.UserAddress;
import com.dev.shoeshop.entity.UserAddressId;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.enums.DiscountValueType;
import com.dev.shoeshop.enums.VoucherType;
import com.dev.shoeshop.repository.*;
import com.dev.shoeshop.service.InventoryHistoryService;
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
    private final InventoryHistoryService inventoryHistoryService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;
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
        // initInventory();

        // 6. Tạo Users
        initUsers();

        // 7. Tạo Test Addresses & Link với Users
        initTestAddresses();

        // 8. Tạo Shipping Companies
        initShippingCompanies();

        // 9. Tạo Shop Warehouse
        initWarehouse();

        // 10. Tạo Shipping Rates
        initShippingRates();

        // 11. Tạo Discounts/Vouchers
        initDiscounts();

        // 12. Tạo Triggers
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
                {"Sandals & Dép", "Dép, sandals thoáng mát, tiện dụng hàng ngày"},
                {"Giày Cao Gót", "Giày cao gót thời trang, sang trọng cho nữ giới"},
                {"Giày Boot", "Giày boot phong cách, bảo vệ tốt, phù hợp mùa đông"},
                {"Giày Lười", "Giày lười tiện lợi, dễ mang, phù hợp đi làm"},
                {"Giày Chạy Bộ", "Giày chạy bộ chuyên nghiệp, công nghệ đệm tiên tiến"},
                {"Giày Đá Cầu", "Giày cầu lông, đá cầu, bóng chuyền chuyên dụng"}
        };

        for (int i = 0; i < categories.length; i++) {
            Category category = new Category();
            category.setName(categories[i][0]);
            category.setDescription(categories[i][1]);
            categoryRepository.save(category);
        }

        System.out.println("  → Đã tạo 10 categories");
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
            "Sneaker phong cách đường phố, màu đỏ nổi bật", 2L, 4L, 1590000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1761638308/giay-21_sbvuog.jpg");
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

        // Product 16-21: Giày Cao Gót (Category 6)
        Product p16 = createProduct("Giày Cao Gót Công Sở",
                "Giày cao gót thanh lịch cho công sở", 6L, 1L, 890000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1761638301/giay-1_pazzqv.jpg");
        createProductDetails(p16);

        Product p17 = createProduct("Giày Cao Gót Đi Tiệc",
                "Giày cao gót sang trọng cho dự tiệc", 6L, 2L, 1290000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1761638302/giay-4_b1irsx.jpg");
        createProductDetails(p17);

        Product p18 = createProduct("Giày Cao Gót Quai Mảnh",
                "Thiết kế quai mảnh quyến rũ", 6L, 3L, 950000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1761638302/giay-8_vs95cq.jpg");
        createProductDetails(p18);

        Product p19 = createProduct("Giày Cao Gót Mũi Nhọn",
                "Mũi nhọn thời trang, tôn dáng", 6L, 4L, 1150000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1761638302/giay-3_csufqn.jpg");
        createProductDetails(p19);

        Product p20 = createProduct("Giày Cao Gót Nữ Đẹp",
                "Giày cao gót nữ thiết kế đẹp", 6L, 5L, 990000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1761638303/giay-6_rxzop5.jpg");
        createProductDetails(p20);

        Product p21 = createProduct("Giày Cao Gót Gót Nhọn",
                "Gót nhọn cao 9cm sang trọng", 6L, 6L, 1350000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p21);

        // Product 22-27: Giày Boot (Category 7)
        Product p22 = createProduct("Boot Cổ Cao Da Thật",
                "Boot cổ cao da thật cao cấp", 7L, 7L, 2490000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p22);

        Product p23 = createProduct("Boot Chelsea Cổ Điển",
                "Boot Chelsea phong cách Anh Quốc", 7L, 8L, 1890000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p23);

        Product p24 = createProduct("Boot Chiến Binh Nam",
                "Boot chiến binh mạnh mẽ", 7L, 9L, 2190000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p24);

        Product p25 = createProduct("Boot Nữ Cổ Ngắn",
                "Boot nữ cổ ngắn thời trang", 7L, 10L, 1590000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p25);

        Product p26 = createProduct("Boot Martin Cá Tính",
                "Boot Martin đế cao su", 7L, 1L, 1790000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p26);

        Product p27 = createProduct("Boot Cao Cổ Mùa Đông",
                "Boot giữ ấm mùa đông", 7L, 2L, 2290000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p27);

        // Product 28-33: Giày Lười (Category 8)
        Product p28 = createProduct("Giày Lười Nam Da Bò",
                "Giày lười da bò cao cấp", 8L, 3L, 1250000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p28);

        Product p29 = createProduct("Giày Lười Công Sở",
                "Giày lười thanh lịch đi làm", 8L, 4L, 950000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p29);

        Product p30 = createProduct("Giày Lười Moccasin",
                "Giày lười kiểu Moccasin", 8L, 5L, 890000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p30);

        Product p31 = createProduct("Giày Lười Nữ Đế Bệt",
                "Giày lười nữ thoải mái", 8L, 6L, 750000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p31);

        Product p32 = createProduct("Giày Lười Đế Cao Su",
                "Giày lười đế cao su bền bỉ", 8L, 7L, 850000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p32);

        Product p33 = createProduct("Giày Lười Phong Cách",
                "Giày lười thiết kế độc đáo", 8L, 8L, 1150000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p33);

        // Product 34-39: Giày Chạy Bộ (Category 9)
        Product p34 = createProduct("Giày Chạy Bộ Marathon",
                "Giày chạy marathon chuyên nghiệp", 9L, 9L, 2890000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p34);

        Product p35 = createProduct("Giày Chạy Bộ Nike React",
                "Công nghệ React Foam êm ái", 9L, 1L, 3250000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p35);

        Product p36 = createProduct("Giày Chạy Bộ Ultraboost",
                "Adidas Ultraboost tối ưu năng lượng", 9L, 2L, 4150000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p36);

        Product p37 = createProduct("Giày Chạy Bộ Puma Velocity",
                "Puma Velocity cho tốc độ", 9L, 3L, 2650000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p37);

        Product p38 = createProduct("Giày Chạy Bộ Nữ Hafele",
                "Giày chạy bộ nữ nhẹ nhàng", 9L, 10L, 1990000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p38);

        Product p39 = createProduct("Giày Chạy Bộ Under Armour",
                "Under Armour Charged hỗ trợ tốt", 9L, 9L, 2450000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p39);

        // Product 40-45: Giày Đá Cầu (Category 10)
        Product p40 = createProduct("Giày Cầu Lông Yonex",
                "Giày cầu lông Yonex chuyên nghiệp", 10L, 4L, 1890000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p40);

        Product p41 = createProduct("Giày Cầu Lông Lining",
                "Lining chống trơn tuyệt vời", 10L, 5L, 1650000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p41);

        Product p42 = createProduct("Giày Bóng Chuyền Mizuno",
                "Mizuno hỗ trợ bật nhảy", 10L, 6L, 2150000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p42);

        Product p43 = createProduct("Giày Cầu Lông Victor",
                "Victor siêu nhẹ, di chuyển nhanh", 10L, 7L, 1750000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p43);

        Product p44 = createProduct("Giày Đá Cầu Kawasaki",
                "Kawasaki bền bỉ, giá tốt", 10L, 8L, 950000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p44);

        Product p45 = createProduct("Giày Bóng Chuyền Asics",
                "Asics Gel công nghệ đệm", 10L, 10L, 2350000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p45);

        // Product 46-51: Thêm cho các danh mục 1-5 (mỗi danh mục thêm 3 sản phẩm nữa)
        // Category 1: Giày Thể Thao
        Product p46 = createProduct("Giày Gym Training Pro",
                "Giày tập gym đa năng", 1L, 6L, 1950000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p46);

        Product p47 = createProduct("Giày Thể Thao CrossFit",
                "Giày CrossFit chuyên nghiệp", 1L, 7L, 2250000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p47);

        Product p48 = createProduct("Giày Tập Aerobic Nữ",
                "Giày aerobic nhẹ nhàng", 1L, 8L, 1650000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p48);

        // Category 2: Sneaker
        Product p49 = createProduct("Sneaker Old School Vans",
                "Vans Old School classic", 2L, 5L, 1850000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p49);

        Product p50 = createProduct("Sneaker Retro Style",
                "Sneaker phong cách retro", 2L, 6L, 1590000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p50);

        Product p51 = createProduct("Sneaker Platform Nữ",
                "Sneaker đế cao năng động", 2L, 7L, 1750000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p51);

        // Category 3: Bóng Đá
        Product p52 = createProduct("Giày Bóng Đá Copa Mundial",
                "Copa Mundial huyền thoại", 3L, 2L, 3550000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p52);

        Product p53 = createProduct("Giày Bóng Đá Tiempo",
                "Nike Tiempo da kangaroo", 3L, 1L, 3890000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p53);

        Product p54 = createProduct("Giày Bóng Đá Puma Future",
                "Puma Future công nghệ mới", 3L, 3L, 3350000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p54);

        // Category 4: Bóng Rổ
        Product p55 = createProduct("Giày Bóng Rổ LeBron",
                "Nike LeBron series cao cấp", 4L, 1L, 4990000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p55);

        Product p56 = createProduct("Giày Bóng Rổ Dame Series",
                "Adidas Dame hỗ trợ tốt", 4L, 2L, 4250000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p56);

        Product p57 = createProduct("Giày Bóng Rổ Kyrie",
                "Nike Kyrie linh hoạt", 4L, 1L, 4450000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p57);

        // Category 5: Sandals & Dép
        Product p58 = createProduct("Dép Flip Flop Classic",
                "Dép xỏ ngón thoải mái", 5L, 8L, 350000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p58);

        Product p59 = createProduct("Sandal Trekking Nam",
                "Sandal đi phượt chắc chắn", 5L, 9L, 1250000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p59);

        Product p60 = createProduct("Dép Massage Chân",
                "Dép massage giúp thư giãn", 5L, 10L, 450000, "https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg");
        createProductDetails(p60);

        System.out.println("  → Đã tạo 60 products với 480 product details (60 products x 8 sizes)");
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
        
        // Tạo inventory cho mỗi ProductDetail với quantity = 30
        // ✅ CHỈ GỌI recordImport - nó sẽ tự tạo Inventory và History
        for (ProductDetail productDetail : allProductDetails) {
            int qty = 30;
            
            // ⭐ Giá nhập mẫu - TẤT CẢ SIZE CÓ GIÁ NHẬP GIỐNG NHAU
            // Khi nhập 1 lô hàng, giá nhập mỗi size đều như nhau
            Product product = productDetail.getProduct();
            double costPrice = product.getPrice() * 0.7; // Giá vốn = 70% giá cơ bản (không tính phụ phí size)
            
            // Gọi recordImport sẽ:
            // 1. Tạo hoặc tìm Inventory
            // 2. Cộng qty vào remainingQuantity và totalQuantity
            // 3. Tạo InventoryHistory record với costPrice
            inventoryHistoryService.recordImport(
                    productDetail, 
                    qty,
                    costPrice,  // ⭐ Giá nhập cho 1 đôi
                    "Hàng nhập lô đầu tiên - Init data"
            );
            
            inventoryCount++;
        }
        
        System.out.println("  → Đã tạo " + inventoryCount + " inventory records (mỗi size 30 items với giá vốn)");
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

        // ⭐ Thêm 5 customers mới để test
        Users customer1 = new Users();
        customer1.setEmail("customer1@test.com");
        customer1.setPassword(encodedPassword);
        customer1.setFullname("Nguyễn Văn An");
        customer1.setPhone("0905234567");
        customer1.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer1.setIsActive(true);
        customer1.setProvider("LOCAL");
        userRepository.save(customer1);

        Users customer2 = new Users();
        customer2.setEmail("customer2@test.com");
        customer2.setPassword(encodedPassword);
        customer2.setFullname("Trần Thị Bình");
        customer2.setPhone("0906345678");
        customer2.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer2.setIsActive(true);
        customer2.setProvider("LOCAL");
        userRepository.save(customer2);

        Users customer3 = new Users();
        customer3.setEmail("customer3@test.com");
        customer3.setPassword(encodedPassword);
        customer3.setFullname("Lê Văn Cường");
        customer3.setPhone("0907456789");
        customer3.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer3.setIsActive(true);
        customer3.setProvider("LOCAL");
        userRepository.save(customer3);

        Users customer4 = new Users();
        customer4.setEmail("customer4@test.com");
        customer4.setPassword(encodedPassword);
        customer4.setFullname("Phạm Thị Dung");
        customer4.setPhone("0908567890");
        customer4.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer4.setIsActive(true);
        customer4.setProvider("LOCAL");
        userRepository.save(customer4);

        Users customer5 = new Users();
        customer5.setEmail("customer5@test.com");
        customer5.setPassword(encodedPassword);
        customer5.setFullname("Hoàng Văn Em");
        customer5.setPhone("0909678901");
        customer5.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer5.setIsActive(true);
        customer5.setProvider("LOCAL");
        userRepository.save(customer5);

        Users customer6 = new Users();
        customer6.setEmail("customer6@test.com");
        customer6.setPassword(encodedPassword);
        customer6.setFullname("Đặng Thị Hoa");
        customer6.setPhone("0910123456");
        customer6.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer6.setIsActive(true);
        customer6.setProvider("LOCAL");
        userRepository.save(customer6);

        Users customer7 = new Users();
        customer7.setEmail("customer7@test.com");
        customer7.setPassword(encodedPassword);
        customer7.setFullname("Võ Minh Khang");
        customer7.setPhone("0911234567");
        customer7.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer7.setIsActive(true);
        customer7.setProvider("LOCAL");
        userRepository.save(customer7);

        Users customer8 = new Users();
        customer8.setEmail("customer8@test.com");
        customer8.setPassword(encodedPassword);
        customer8.setFullname("Bùi Thị Lan");
        customer8.setPhone("0912345678");
        customer8.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer8.setIsActive(true);
        customer8.setProvider("LOCAL");
        userRepository.save(customer8);

        Users customer9 = new Users();
        customer9.setEmail("customer9@test.com");
        customer9.setPassword(encodedPassword);
        customer9.setFullname("Đinh Văn Nam");
        customer9.setPhone("0913456789");
        customer9.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer9.setIsActive(true);
        customer9.setProvider("LOCAL");
        userRepository.save(customer9);

        Users customer10 = new Users();
        customer10.setEmail("customer10@test.com");
        customer10.setPassword(encodedPassword);
        customer10.setFullname("Mai Thị Phương");
        customer10.setPhone("0914567890");
        customer10.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer10.setIsActive(true);
        customer10.setProvider("LOCAL");
        userRepository.save(customer10);

        Users customer11 = new Users();
        customer11.setEmail("customer11@test.com");
        customer11.setPassword(encodedPassword);
        customer11.setFullname("Ngô Văn Quân");
        customer11.setPhone("0915678901");
        customer11.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer11.setIsActive(true);
        customer11.setProvider("LOCAL");
        userRepository.save(customer11);

        Users customer12 = new Users();
        customer12.setEmail("customer12@test.com");
        customer12.setPassword(encodedPassword);
        customer12.setFullname("Dương Thị Thảo");
        customer12.setPhone("0916789012");
        customer12.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer12.setIsActive(true);
        customer12.setProvider("LOCAL");
        userRepository.save(customer12);

        Users customer13 = new Users();
        customer13.setEmail("customer13@test.com");
        customer13.setPassword(encodedPassword);
        customer13.setFullname("Phan Văn Sơn");
        customer13.setPhone("0917890123");
        customer13.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer13.setIsActive(true);
        customer13.setProvider("LOCAL");
        userRepository.save(customer13);

        Users customer14 = new Users();
        customer14.setEmail("customer14@test.com");
        customer14.setPassword(encodedPassword);
        customer14.setFullname("Lý Thị Tuyết");
        customer14.setPhone("0918901234");
        customer14.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer14.setIsActive(true);
        customer14.setProvider("LOCAL");
        userRepository.save(customer14);

        Users customer15 = new Users();
        customer15.setEmail("customer15@test.com");
        customer15.setPassword(encodedPassword);
        customer15.setFullname("Trịnh Văn Vũ");
        customer15.setPhone("0919012345");
        customer15.setRole(roleRepository.findByRoleName("user").orElseThrow());
        customer15.setIsActive(true);
        customer15.setProvider("LOCAL");
        userRepository.save(customer15);

        // ⭐ Thêm 2 shippers nữa
        Users shipper2 = new Users();
        shipper2.setEmail("shipper2@test.com");
        shipper2.setPassword(encodedPassword);
        shipper2.setFullname("Nguyễn Văn Ship");
        shipper2.setPhone("0910789012");
        shipper2.setRole(roleRepository.findByRoleName("shipper").orElseThrow());
        shipper2.setIsActive(true);
        shipper2.setProvider("LOCAL");
        userRepository.save(shipper2);

        Users shipper3 = new Users();
        shipper3.setEmail("shipper3@test.com");
        shipper3.setPassword(encodedPassword);
        shipper3.setFullname("Trần Thị Giao");
        shipper3.setPhone("0911890123");
        shipper3.setRole(roleRepository.findByRoleName("shipper").orElseThrow());
        shipper3.setIsActive(true);
        shipper3.setProvider("LOCAL");
        userRepository.save(shipper3);

        System.out.println("  → Đã tạo 21 users: 1 admin, 1 manager, 16 customers, 3 shippers (password: 123456)");
    }

    private void initTestAddresses() {
        // Tạo 1 địa chỉ chung để test cho users 3-18 (16 customers)
        Address sharedAddress = new Address();
        sharedAddress.setAddress_line("38 Hẻm 268 Nguyễn Văn Quá, Đông Hưng Thuận, Quận 12");
        sharedAddress.setCity("Hồ Chí Minh");
        sharedAddress.setCountry("Việt Nam");
        sharedAddress.setLatitude(10.833233157151525);
        sharedAddress.setLongitude(106.6296313338766);
        sharedAddress.setAddressType("HOME");
        addressRepository.save(sharedAddress);

        // Liên kết địa chỉ với users 3-18
        // User 3: user@user
        // User 4-18: customer1-15
        for (int userId = 3; userId <= 18; userId++) {
            Users user = userRepository.findById((long) userId).orElse(null);
            if (user != null) {
                UserAddress userAddress = new UserAddress();
                userAddress.setId(new UserAddressId(user.getId(), sharedAddress.getId()));
                userAddress.setUser(user);
                userAddress.setAddress(sharedAddress);
                userAddress.setRecipientName(user.getFullname());
                userAddress.setRecipientPhone(user.getPhone());
                userAddress.setLabel("Nhà riêng");
                userAddress.setIsDefault(true);
                userAddress.setIsDelete(false);
                userAddressRepository.save(userAddress);
            }
        }

        System.out.println("  → Đã tạo 1 địa chỉ chung và liên kết với 16 users (ID 3-18)");
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

            // 1. INVENTORY TRIGGERS - Trừ kho tự động khi đặt hàng
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
        // Trigger 1: Trừ kho ngay khi đặt hàng (BEFORE INSERT)
        // ✅ CẦN THIẾT - OrderService không gọi InventoryDeductionService
        // Trigger này tự động trừ kho khi insert order_detail
        String trigger1 = """
            CREATE TRIGGER after_order_detail_insert
            BEFORE INSERT ON order_detail
            FOR EACH ROW
            BEGIN
                DECLARE v_current_stock BIGINT;
                DECLARE v_product_name VARCHAR(255);
                DECLARE v_error_message VARCHAR(500);
                
                SELECT i.remaining_quantity INTO v_current_stock
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
                SET remaining_quantity = remaining_quantity - NEW.quantity
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
                    SET i.remaining_quantity = i.remaining_quantity + od.quantity
                    WHERE od.order_id = NEW.id;
                END IF;
                
                IF NEW.status = 'RETURN' AND OLD.status = 'DELIVERED' THEN
                    UPDATE inventory i
                    INNER JOIN order_detail od ON i.product_detail_id = od.productdetail_id
                    SET i.remaining_quantity = i.remaining_quantity + od.quantity
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
