package com.dev.shoeshop.config;

import com.dev.shoeshop.entity.Brand;
import com.dev.shoeshop.entity.Category;
import com.dev.shoeshop.entity.Product;
import com.dev.shoeshop.entity.ProductDetail;
import com.dev.shoeshop.entity.Role;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

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
    private final UserRepository userRepository;
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

        // 5. Tạo Users
        initUsers();

        // 6. Tạo Triggers
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
            "Giày thể thao thiết kế hiện đại, phù hợp cho chạy bộ và tập gym", 1L, 1L, 1890000, "/images/the-thao-1.jpg");
        createProductDetails(p1);

        // Product 2: Running Pro
        Product p2 = createProduct("Giày Thể Thao Running Pro", 
            "Giày chạy bộ chuyên nghiệp, đệm khí êm ái", 1L, 2L, 2350000, "/images/the-thao-2.jpg");
        createProductDetails(p2);

        // Product 3: Training All Day
        Product p3 = createProduct("Giày Training All Day", 
            "Giày tập luyện đa năng, bám sân tốt", 1L, 3L, 1750000, "/images/the-thao-3.jpg");
        createProductDetails(p3);

        // Product 4-6: Sneakers
        Product p4 = createProduct("Sneaker Street Style Đỏ", 
            "Sneaker phong cách đường phố, màu đỏ nổi bật", 2L, 4L, 1590000, "/images/sneaker-1.jpg");
        createProductDetails(p4);

        Product p5 = createProduct("Sneaker Low-Top Trắng", 
            "Sneaker trắng basic, dễ phối đồ", 2L, 5L, 1450000, "/images/sneaker-2.jpg");
        createProductDetails(p5);

        Product p6 = createProduct("Sneaker High-Top Canvas", 
            "Sneaker cổ cao vải canvas, style vintage", 2L, 4L, 1690000, "/images/sneaker-3.jpg");
        createProductDetails(p6);

        // Product 7-9: Bóng Đá
        Product p7 = createProduct("Giày Bóng Đá Sân Cỏ TF", 
            "Giày bóng đá sân cỏ nhân tạo, đế TF bám sân cực tốt", 3L, 1L, 2890000, "/images/bong-da-1.jpg");
        createProductDetails(p7);

        Product p8 = createProduct("Giày Bóng Đá Mercurial", 
            "Giày bóng đá tốc độ, thiết kế khí động học", 3L, 1L, 3250000, "/images/bong-da-2.jpg");
        createProductDetails(p8);

        Product p9 = createProduct("Giày Đá Banh Predator", 
            "Giày sút bóng chuẩn xác, công nghệ Control Frame", 3L, 2L, 3150000, "/images/bong-da-3.jpg");
        createProductDetails(p9);

        // Product 10-12: Bóng Rổ
        Product p10 = createProduct("Giày Bóng Rổ Air Jordan",
            "Giày bóng rổ cổ cao, bảo vệ cổ chân tối ưu", 4L, 1L, 4590000, "/images/bong-ro-1.jpg");
        createProductDetails(p10);

        Product p11 = createProduct("Basketball Shoes Pro", 
            "Giày bóng rổ chuyên nghiệp, đế cao su chống trơn", 4L, 2L, 4150000, "/images/bong-ro-2.jpg");
        createProductDetails(p11);

        Product p12 = createProduct("Giày Bóng Rổ Harden Style", 
            "Thiết kế năng động, hỗ trợ bật nhảy", 4L, 2L, 3790000, "/images/bong-ro-3.jpg");
        createProductDetails(p12);

        // Product 13-15: Sandals & Dép
        Product p13 = createProduct("Dép Quai Ngang Thời Trang", 
            "Dép quai ngang êm ái, phù hợp mùa hè", 5L, 1L, 590000, "/images/dep-1.jpg");
        createProductDetails(p13);

        Product p14 = createProduct("Dép Adilette Classic", 
            "Dép thể thao iconic, thoáng mát", 5L, 2L, 750000, "/images/dep-2.jpg");
        createProductDetails(p14);

        Product p15 = createProduct("Sandal Outdoor Adventure", 
            "Sandal dã ngoại, đi phượt, leo núi", 5L, 9L, 1150000, "/images/sandal-2.jpg");
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
