-- ========================================
-- 1. ROLES
-- ========================================
INSERT INTO role (id, role_name) VALUES
(1, 'admin'),
(2, 'manager'),
(3, 'user'),
(4, 'shipper');


-- ========================================
-- 2. CATEGORIES
-- ========================================
INSERT IGNORE INTO category (id, name, description) VALUES
(1, 'Giày Thể Thao', 'Giày dành cho hoạt động thể thao, chạy bộ, gym, training'),
(2, 'Giày Sneaker', 'Giày sneaker phong cách streetwear, thời trang đường phố'),
(3, 'Giày Da', 'Giày da công sở, lịch sự, chuyên nghiệp cho nam'),
(4, 'Giày Cao Gót', 'Giày cao gót sang trọng, thanh lịch cho nữ'),
(5, 'Sandals & Dép', 'Dép, sandals thoáng mát, tiện dụng hàng ngày');

-- ========================================
-- 3. BRANDS
-- ========================================
INSERT INTO brand (id, name) VALUES
(1, 'Nike'),
(2, 'Adidas'),
(3, 'Puma'),
(4, 'Converse'),
(5, 'Vans'),
(6, 'New Balance'),
(7, 'Reebok'),
(8, 'Fila'),
(9, 'Under Armour'),
(10, 'Skechers');

-- ========================================
-- 4. ADMIN USER
-- ========================================
-- Email: admin@shoeshop.com
-- Password: admin123
-- (BCrypt hash của password 'admin123')
INSERT IGNORE INTO users (id, email, password, full_name, phone, role_id, is_active, provider) VALUES
(1, 'admin@shoeshop.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Administrator', '0123456789', 1, true, 'LOCAL');

-- ========================================
-- DONE! 
-- ========================================
-- Sau khi chạy file này, bạn sẽ có:
-- - 3 roles (admin, user, manager)
-- - 5 categories
-- - 10 brands
-- - 1 admin user (login: admin@shoeshop.com / admin123)
