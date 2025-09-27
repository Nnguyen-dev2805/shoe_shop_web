-- ========================================
-- SHOE SHOP COMPLETE DATABASE SETUP
-- Chạy file này 1 lần để tạo toàn bộ database
-- ========================================

-- Tạo database
CREATE DATABASE IF NOT EXISTS shoe_shop_basic;
USE shoe_shop_basic;

-- ========================================
-- 1. TẠO CÁC BẢNG CHÍNH
-- ========================================

-- Bảng roles
CREATE TABLE IF NOT EXISTS roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(255),
    role_id INT NOT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng category
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng brand
CREATE TABLE IF NOT EXISTS brand (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng product
CREATE TABLE IF NOT EXISTS product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    brand_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    image VARCHAR(255),
    description TEXT,
    price DECIMAL(18,2) NOT NULL,
    voucher DECIMAL(18,2) DEFAULT 0,
    is_delete BOOLEAN DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (brand_id) REFERENCES brand(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng product_detail
CREATE TABLE IF NOT EXISTS product_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    size VARCHAR(20) NOT NULL,
    price_add DECIMAL(18,2) DEFAULT 0,
    quantity INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng cart
CREATE TABLE IF NOT EXISTS cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_price DECIMAL(18,2) DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng cart_detail
CREATE TABLE IF NOT EXISTS cart_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    productdetail_id INT NOT NULL UNIQUE,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES cart(id),
    FOREIGN KEY (productdetail_id) REFERENCES product_detail(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng address
CREATE TABLE IF NOT EXISTS address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    address_line VARCHAR(255) NOT NULL,
    district VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(50) DEFAULT 'VietNam'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng user_address
CREATE TABLE IF NOT EXISTS user_address (
    user_id INT NOT NULL,
    address_id INT NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id, address_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES address(id)
);

-- Bảng orders
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    delivery_address_id INT NOT NULL,
    total_price DECIMAL(18,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    pay_option VARCHAR(50) NOT NULL DEFAULT 'tiền mặt',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (delivery_address_id) REFERENCES address(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng order_detail
CREATE TABLE IF NOT EXISTS order_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    productdetail_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 1),
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (productdetail_id) REFERENCES product_detail(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng shipment
CREATE TABLE IF NOT EXISTS shipment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    shipper_id INT NOT NULL,
    shipment_address_id INT NULL,
    status VARCHAR(50) DEFAULT 'shipping',
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (shipper_id) REFERENCES users(id),
    FOREIGN KEY (shipment_address_id) REFERENCES address(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- 2. TẠO BẢNG DISCOUNT (QUAN TRỌNG!)
-- ========================================

-- Bảng discount
CREATE TABLE IF NOT EXISTS discount (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 1000,
    discount_percent DECIMAL(5,4) NOT NULL CHECK (discount_percent >= 0 AND discount_percent <= 1),
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'COMING', 'EXPIRED')),
    min_order_value DECIMAL(18,2),
    created_date DATE NOT NULL DEFAULT (CURDATE()),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_delete BOOLEAN NOT NULL DEFAULT FALSE,
    created_by INT,
    updated_by INT,
    updated_date DATE,
    CHECK (end_date > start_date),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng discount_usage (track ai đã dùng discount nào)
CREATE TABLE IF NOT EXISTS discount_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    discount_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    order_id INT
    used_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (discount_id) REFERENCES discount(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng rating
CREATE TABLE IF NOT EXISTS rating (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    star INT CHECK (star >= 1 AND star <= 5),
    description TEXT,
    image VARCHAR(255),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- 3. THÊM DỮ LIỆU MẪU
-- ========================================

-- Dữ liệu roles
INSERT INTO roles (role_name) VALUES ('admin'), ('manager'), ('shipper'), ('user');

-- Dữ liệu users
INSERT INTO users (email, password, full_name, address, phone, role_id)
VALUES 
    ('tnhatnguyen.dev2805@gmail.com', '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm', 'Nhất Nguyên', 'HCM City', '0123456789', 1),
    ('admin@shoeshop.com', '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm', 'Admin User', 'Hanoi', '0987654321', 1),
    ('manager@shoeshop.com', '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm', 'Manager User', 'Da Nang', '0123456788', 2),
    ('shipper@shoeshop.com', '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm', 'Shipper User', 'Can Tho', '0123456787', 3),
    ('user@shoeshop.com', '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm', 'Customer User', 'Hue', '0123456786', 4);

-- Dữ liệu category
INSERT INTO category (name, description) VALUES 
    ('Giày thể thao', 'Các loại giày thể thao cho nam và nữ'),
    ('Giày da', 'Giày da cao cấp cho công sở'),
    ('Giày boot', 'Giày boot thời trang'),
    ('Sandal', 'Sandal mùa hè'),
    ('Giày lười', 'Giày lười thoải mái');

-- Dữ liệu brand
INSERT INTO brand (name) VALUES 
    ('Nike'), ('Adidas'), ('Puma'), ('Converse'), ('Vans'),
    ('Bitis'), ('Ananas'), ('Vascara'), ('Camper'), ('Geox');

-- Dữ liệu product
INSERT INTO product (category_id, brand_id, title, image, description, price, voucher) VALUES 
    (1, 1, 'Nike Air Max 270', 'nike-air-max-270.jpg', 'Giày thể thao Nike Air Max 270', 2500000, 0),
    (1, 2, 'Adidas Ultraboost 22', 'adidas-ultraboost-22.jpg', 'Giày chạy bộ Adidas Ultraboost 22', 3200000, 0),
    (2, 6, 'Bitis Hunter Da', 'bitis-hunter-da.jpg', 'Giày da Bitis Hunter', 1800000, 0),
    (3, 4, 'Converse Chuck Taylor', 'converse-chuck.jpg', 'Giày Converse Chuck Taylor All Star', 1200000, 0),
    (4, 5, 'Vans Classic Slip-On', 'vans-slip-on.jpg', 'Giày Vans Classic Slip-On', 1500000, 0);

-- Dữ liệu product_detail
INSERT INTO product_detail (product_id, size, price_add, quantity) VALUES 
    (1, '39', 0, 50), (1, '40', 0, 30), (1, '41', 0, 25), (1, '42', 0, 20),
    (2, '39', 0, 40), (2, '40', 0, 35), (2, '41', 0, 30), (2, '42', 0, 25),
    (3, '39', 0, 20), (3, '40', 0, 25), (3, '41', 0, 30), (3, '42', 0, 15),
    (4, '39', 0, 60), (4, '40', 0, 50), (4, '41', 0, 45), (4, '42', 0, 40),
    (5, '39', 0, 35), (5, '40', 0, 40), (5, '41', 0, 35), (5, '42', 0, 30);

-- ========================================
-- 4. DỮ LIỆU MẪU CHO DISCOUNT (QUAN TRỌNG!)
-- ========================================

-- Xóa dữ liệu cũ nếu có
DELETE FROM discount;

-- Thêm dữ liệu mẫu cho discount
INSERT INTO discount (name, quantity, discount_percent, status, min_order_value, start_date, end_date, created_by) VALUES 
-- Discount đang active
('Summer Sale 2024', 500, 0.20, 'ACTIVE', 500000, '2024-01-20', '2025-02-20', 1),
('New Year Special', 300, 0.15, 'ACTIVE', 300000, '2024-01-01', '2025-12-31', 1),
('Student Discount', 2000, 0.10, 'ACTIVE', 200000, '2024-01-01', '2025-12-31', 1),
('Weekend Flash Sale', 150, 0.25, 'ACTIVE', 1000000, '2024-01-01', '2025-12-31', 1),

-- Discount sắp bắt đầu
('Spring Collection 2025', 800, 0.25, 'COMING', 600000, '2025-03-01', '2025-03-31', 1),
('Black Friday 2025', 1000, 0.50, 'COMING', 200000, '2025-11-29', '2025-12-02', 1),
('Flash Sale Weekend', 200, 0.30, 'COMING', 100000, '2025-02-15', '2025-02-17', 1),
('Valentine Special', 400, 0.18, 'COMING', 800000, '2025-02-14', '2025-02-16', 1),

-- Discount inactive
('Winter Sale 2023', 200, 0.18, 'INACTIVE', 400000, '2024-12-01', '2024-12-31', 1),
('Back to School', 600, 0.12, 'INACTIVE', 250000, '2024-08-15', '2024-09-15', 1),
('Mid Autumn Festival', 350, 0.15, 'INACTIVE', 350000, '2024-09-15', '2024-09-17', 1),

-- Discount đã hết hạn
('Flash Sale Old', 100, 0.30, 'EXPIRED', 1000000, '2024-01-08', '2024-01-10', 1),
('Christmas Special 2023', 400, 0.22, 'EXPIRED', 350000, '2024-12-20', '2024-12-25', 1),
('New Year 2024', 300, 0.20, 'EXPIRED', 500000, '2024-01-01', '2024-01-03', 1);

-- ========================================
-- 5. KIỂM TRA DỮ LIỆU
-- ========================================

-- Hiển thị thông tin tổng quan
SELECT '=== TỔNG QUAN DATABASE ===' as info;

SELECT 
    'Users' as table_name, 
    COUNT(*) as total_records 
FROM users
UNION ALL
SELECT 
    'Categories' as table_name, 
    COUNT(*) as total_records 
FROM category
UNION ALL
SELECT 
    'Brands' as table_name, 
    COUNT(*) as total_records 
FROM brand
UNION ALL
SELECT 
    'Products' as table_name, 
    COUNT(*) as total_records 
FROM product
UNION ALL
SELECT 
    'Discounts' as table_name, 
    COUNT(*) as total_records 
FROM discount;

-- Hiển thị chi tiết discount
SELECT '=== CHI TIẾT DISCOUNT ===' as info;

SELECT 
    id,
    name,
    quantity,
    CONCAT(ROUND(discount_percent * 100, 1), '%') as discount_percent_display,
    status,
    CASE 
        WHEN min_order_value > 0 THEN CONCAT(FORMAT(min_order_value, 0), ' VNĐ')
        ELSE 'Không giới hạn'
    END as min_order_value_display,
    DATE_FORMAT(created_date, '%d/%m/%Y') as created_date,
    DATE_FORMAT(start_date, '%d/%m/%Y') as start_date,
    DATE_FORMAT(end_date, '%d/%m/%Y') as end_date,
    CASE WHEN is_delete = 0 THEN 'Active' ELSE 'Deleted' END as delete_status
FROM discount 
ORDER BY created_date DESC, status;

-- Đếm số discount theo status
SELECT '=== THỐNG KÊ DISCOUNT THEO STATUS ===' as info;

SELECT 
    status,
    COUNT(*) as count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM discount), 1), '%') as percentage
FROM discount 
WHERE is_delete = false
GROUP BY status
ORDER BY count DESC;

-- Hiển thị discount có thể sử dụng
SELECT '=== DISCOUNT CÓ THỂ SỬ DỤNG ===' as info;

SELECT 
    id,
    name,
    CONCAT(ROUND(discount_percent * 100, 1), '%') as discount_percent,
    quantity,
    CASE 
        WHEN min_order_value > 0 THEN CONCAT(FORMAT(min_order_value, 0), ' VNĐ')
        ELSE 'Không giới hạn'
    END as min_order_value
FROM discount 
WHERE status = 'ACTIVE' 
    AND is_delete = false 
    AND quantity > 0 
    AND CURDATE() BETWEEN start_date AND end_date
ORDER BY discount_percent DESC;

SELECT '=== HOÀN THÀNH SETUP DATABASE ===' as info;
