-- ========================================
-- 1. ROLES
-- ========================================
INSERT INTO role (id, role_name) VALUES (1, 'admin');
INSERT INTO role (id, role_name) VALUES (2, 'manager');
INSERT INTO role (id, role_name) VALUES (3, 'user');
INSERT INTO role (id, role_name) VALUES (4, 'shipper');

-- ========================================
-- 2. CATEGORIES
-- ========================================
INSERT INTO category (id, name, description) VALUES (1, 'Giày Thể Thao', 'Giày dành cho hoạt động thể thao, chạy bộ, gym, training');
INSERT INTO category (id, name, description) VALUES (2, 'Giày Sneaker', 'Giày sneaker phong cách streetwear, thời trang đường phố');
INSERT INTO category (id, name, description) VALUES (3, 'Giày Bóng Đá', 'Giày chuyên dụng cho cầu thủ bóng đá, hỗ trợ bám sân và di chuyển nhanh');
INSERT INTO category (id, name, description) VALUES (4, 'Giày Bóng Rổ', 'Giày thể thao chuyên cho bóng rổ, hỗ trợ bật nhảy và bảo vệ cổ chân');
INSERT INTO category (id, name, description) VALUES (5, 'Sandals & Dép', 'Dép, sandals thoáng mát, tiện dụng hàng ngày');

-- ========================================
-- 3. BRANDS
-- ========================================
INSERT INTO brand (id, name) VALUES (1, 'Nike');
INSERT INTO brand (id, name) VALUES (2, 'Adidas');
INSERT INTO brand (id, name) VALUES (3, 'Puma');
INSERT INTO brand (id, name) VALUES (4, 'Converse');
INSERT INTO brand (id, name) VALUES (5, 'Vans');
INSERT INTO brand (id, name) VALUES (6, 'New Balance');
INSERT INTO brand (id, name) VALUES (7, 'Reebok');
INSERT INTO brand (id, name) VALUES (8, 'Fila');
INSERT INTO brand (id, name) VALUES (9, 'Under Armour');
INSERT INTO brand (id, name) VALUES (10, 'Skechers');

-- ========================================
-- 5. PRODUCTS (Sample Data)
-- ========================================

-- Giày Thể Thao (Category 1)
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (1, 'Giày Thể Thao Nam Năng Động', 'Giày thể thao thiết kế hiện đại, phù hợp cho chạy bộ và tập gym', 1, 1, 1890000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (2, 'Giày Thể Thao Running Pro', 'Giày chạy bộ chuyên nghiệp, đệm khí êm ái', 1, 2, 2350000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (3, 'Giày Training All Day', 'Giày tập luyện đa năng, bám sân tốt', 1, 3, 1750000);

-- Giày Sneaker (Category 2)
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (4, 'Sneaker Street Style Đỏ', 'Sneaker phong cách đường phố, màu đỏ nổi bật', 2, 4, 1590000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (5, 'Sneaker Low-Top Trắng', 'Sneaker trắng basic, dễ phối đồ', 2, 5, 1450000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (6, 'Sneaker High-Top Canvas', 'Sneaker cổ cao vải canvas, style vintage', 2, 4, 1690000);

-- Giày Bóng Đá (Category 3)
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (7, 'Giày Bóng Đá Sân Cỏ TF', 'Giày bóng đá sân cỏ nhân tạo, đế TF bám sân cực tốt', 3, 1, 2890000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (8, 'Giày Bóng Đá Mercurial', 'Giày bóng đá tốc độ, thiết kế khí động học', 3, 1, 3250000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (9, 'Giày Đá Banh Predator', 'Giày sút bóng chuẩn xác, công nghệ Control Frame', 3, 2, 3150000);

-- Giày Bóng Rổ (Category 4)
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (10, 'Giày Bóng Rổ Air Jordan Style', 'Giày bóng rổ cổ cao, bảo vệ cổ chân tối ưu', 4, 1, 4590000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (11, 'Basketball Shoes Pro', 'Giày bóng rổ chuyên nghiệp, đế cao su chống trơn', 4, 2, 4150000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (12, 'Giày Bóng Rổ Harden Style', 'Thiết kế năng động, hỗ trợ bật nhảy', 4, 2, 3790000);

-- Sandals & Dép (Category 5)
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (13, 'Dép Quai Ngang Thời Trang', 'Dép quai ngang êm ái, phù hợp mùa hè', 5, 1, 590000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (14, 'Dép Adilette Classic', 'Dép thể thao iconic, thoáng mát', 5, 2, 750000);
INSERT INTO product (id, title, description, category_id, brand_id, price) VALUES (15, 'Sandal Outdoor Adventure', 'Sandal dã ngoại, đi phượt, leo núi', 5, 9, 1150000);

-- ========================================
-- 6. PRODUCT DETAILS (Size & Images)
-- ========================================

-- Product 1: Giày Thể Thao Nam (the-thao-1.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (1, 1, 38, 0, '/images/the-thao-1.jpg'),
                                                                            (2, 1, 39, 0, '/images/the-thao-1.jpg'),
                                                                            (3, 1, 40, 0, '/images/the-thao-1.jpg'),
                                                                            (4, 1, 41, 50000, '/images/the-thao-1.jpg'),
                                                                            (5, 1, 42, 50000, '/images/the-thao-1.jpg'),
                                                                            (6, 1, 43, 100000, '/images/the-thao-1.jpg'),
                                                                            (7, 1, 44, 100000, '/images/the-thao-1.jpg'),
                                                                            (8, 1, 45, 150000, '/images/the-thao-1.jpg');

-- Product 2: Running Pro (the-thao-2.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (9, 2, 38, 0, '/images/the-thao-2.jpg'),
                                                                            (10, 2, 39, 0, '/images/the-thao-2.jpg'),
                                                                            (11, 2, 40, 0, '/images/the-thao-2.jpg'),
                                                                            (12, 2, 41, 50000, '/images/the-thao-2.jpg'),
                                                                            (13, 2, 42, 50000, '/images/the-thao-2.jpg'),
                                                                            (14, 2, 43, 100000, '/images/the-thao-2.jpg'),
                                                                            (15, 2, 44, 100000, '/images/the-thao-2.jpg'),
                                                                            (16, 2, 45, 150000, '/images/the-thao-2.jpg');

-- Product 3: Training All Day (the-thao-3.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (17, 3, 38, 0, '/images/the-thao-3.jpg'),
                                                                            (18, 3, 39, 0, '/images/the-thao-3.jpg'),
                                                                            (19, 3, 40, 0, '/images/the-thao-3.jpg'),
                                                                            (20, 3, 41, 50000, '/images/the-thao-3.jpg'),
                                                                            (21, 3, 42, 50000, '/images/the-thao-3.jpg'),
                                                                            (22, 3, 43, 100000, '/images/the-thao-3.jpg'),
                                                                            (23, 3, 44, 100000, '/images/the-thao-3.jpg'),
                                                                            (24, 3, 45, 150000, '/images/the-thao-3.jpg');

-- Product 4: Sneaker Đỏ (sneaker-1.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (25, 4, 38, 0, '/images/sneaker-1.jpg'),
                                                                            (26, 4, 39, 0, '/images/sneaker-1.jpg'),
                                                                            (27, 4, 40, 0, '/images/sneaker-1.jpg'),
                                                                            (28, 4, 41, 50000, '/images/sneaker-1.jpg'),
                                                                            (29, 4, 42, 50000, '/images/sneaker-1.jpg'),
                                                                            (30, 4, 43, 100000, '/images/sneaker-1.jpg'),
                                                                            (31, 4, 44, 100000, '/images/sneaker-1.jpg'),
                                                                            (32, 4, 45, 150000, '/images/sneaker-1.jpg');

-- Product 5: Sneaker Trắng (sneaker-2.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (33, 5, 38, 0, '/images/sneaker-2.jpg'),
                                                                            (34, 5, 39, 0, '/images/sneaker-2.jpg'),
                                                                            (35, 5, 40, 0, '/images/sneaker-2.jpg'),
                                                                            (36, 5, 41, 50000, '/images/sneaker-2.jpg'),
                                                                            (37, 5, 42, 50000, '/images/sneaker-2.jpg'),
                                                                            (38, 5, 43, 100000, '/images/sneaker-2.jpg'),
                                                                            (39, 5, 44, 100000, '/images/sneaker-2.jpg'),
                                                                            (40, 5, 45, 150000, '/images/sneaker-2.jpg');

-- Product 6: High-Top Canvas (sneaker-3.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (41, 6, 38, 0, '/images/sneaker-3.jpg'),
                                                                            (42, 6, 39, 0, '/images/sneaker-3.jpg'),
                                                                            (43, 6, 40, 0, '/images/sneaker-3.jpg'),
                                                                            (44, 6, 41, 50000, '/images/sneaker-3.jpg'),
                                                                            (45, 6, 42, 50000, '/images/sneaker-3.jpg'),
                                                                            (46, 6, 43, 100000, '/images/sneaker-3.jpg'),
                                                                            (47, 6, 44, 100000, '/images/sneaker-3.jpg'),
                                                                            (48, 6, 45, 150000, '/images/sneaker-3.jpg');

-- Product 7: Bóng Đá TF (bong-da-1.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (49, 7, 38, 0, '/images/bong-da-1.jpg'),
                                                                            (50, 7, 39, 0, '/images/bong-da-1.jpg'),
                                                                            (51, 7, 40, 0, '/images/bong-da-1.jpg'),
                                                                            (52, 7, 41, 50000, '/images/bong-da-1.jpg'),
                                                                            (53, 7, 42, 50000, '/images/bong-da-1.jpg'),
                                                                            (54, 7, 43, 100000, '/images/bong-da-1.jpg'),
                                                                            (55, 7, 44, 100000, '/images/bong-da-1.jpg'),
                                                                            (56, 7, 45, 150000, '/images/bong-da-1.jpg');

-- Product 8: Mercurial (bong-da-2.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (57, 8, 38, 0, '/images/bong-da-2.jpg'),
                                                                            (58, 8, 39, 0, '/images/bong-da-2.jpg'),
                                                                            (59, 8, 40, 0, '/images/bong-da-2.jpg'),
                                                                            (60, 8, 41, 50000, '/images/bong-da-2.jpg'),
                                                                            (61, 8, 42, 50000, '/images/bong-da-2.jpg'),
                                                                            (62, 8, 43, 100000, '/images/bong-da-2.jpg'),
                                                                            (63, 8, 44, 100000, '/images/bong-da-2.jpg'),
                                                                            (64, 8, 45, 150000, '/images/bong-da-2.jpg');

-- Product 9: Predator (bong-da-3.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (65, 9, 38, 0, '/images/bong-da-3.jpg'),
                                                                            (66, 9, 39, 0, '/images/bong-da-3.jpg'),
                                                                            (67, 9, 40, 0, '/images/bong-da-3.jpg'),
                                                                            (68, 9, 41, 50000, '/images/bong-da-3.jpg'),
                                                                            (69, 9, 42, 50000, '/images/bong-da-3.jpg'),
                                                                            (70, 9, 43, 100000, '/images/bong-da-3.jpg'),
                                                                            (71, 9, 44, 100000, '/images/bong-da-3.jpg'),
                                                                            (72, 9, 45, 150000, '/images/bong-da-3.jpg');

-- Product 10: Air Jordan Style (bong-ro-1.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (73, 10, 38, 0, '/images/bong-ro-1.jpg'),
                                                                            (74, 10, 39, 0, '/images/bong-ro-1.jpg'),
                                                                            (75, 10, 40, 0, '/images/bong-ro-1.jpg'),
                                                                            (76, 10, 41, 50000, '/images/bong-ro-1.jpg'),
                                                                            (77, 10, 42, 50000, '/images/bong-ro-1.jpg'),
                                                                            (78, 10, 43, 100000, '/images/bong-ro-1.jpg'),
                                                                            (79, 10, 44, 100000, '/images/bong-ro-1.jpg'),
                                                                            (80, 10, 45, 150000, '/images/bong-ro-1.jpg');

-- Product 11: Basketball Pro (bong-ro-2.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (81, 11, 38, 0, '/images/bong-ro-2.jpg'),
                                                                            (82, 11, 39, 0, '/images/bong-ro-2.jpg'),
                                                                            (83, 11, 40, 0, '/images/bong-ro-2.jpg'),
                                                                            (84, 11, 41, 50000, '/images/bong-ro-2.jpg'),
                                                                            (85, 11, 42, 50000, '/images/bong-ro-2.jpg'),
                                                                            (86, 11, 43, 100000, '/images/bong-ro-2.jpg'),
                                                                            (87, 11, 44, 100000, '/images/bong-ro-2.jpg'),
                                                                            (88, 11, 45, 150000, '/images/bong-ro-2.jpg');

-- Product 12: Harden Style (bong-ro-3.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (89, 12, 38, 0, '/images/bong-ro-3.jpg'),
                                                                            (90, 12, 39, 0, '/images/bong-ro-3.jpg'),
                                                                            (91, 12, 40, 0, '/images/bong-ro-3.jpg'),
                                                                            (92, 12, 41, 50000, '/images/bong-ro-3.jpg'),
                                                                            (93, 12, 42, 50000, '/images/bong-ro-3.jpg'),
                                                                            (94, 12, 43, 100000, '/images/bong-ro-3.jpg'),
                                                                            (95, 12, 44, 100000, '/images/bong-ro-3.jpg'),
                                                                            (96, 12, 45, 150000, '/images/bong-ro-3.jpg');

-- Product 13: Dép Quai Ngang (dep-1.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (97, 13, 38, 0, '/images/dep-1.jpg'),
                                                                            (98, 13, 39, 0, '/images/dep-1.jpg'),
                                                                            (99, 13, 40, 0, '/images/dep-1.jpg'),
                                                                            (100, 13, 41, 30000, '/images/dep-1.jpg'),
                                                                            (101, 13, 42, 30000, '/images/dep-1.jpg'),
                                                                            (102, 13, 43, 50000, '/images/dep-1.jpg'),
                                                                            (103, 13, 44, 50000, '/images/dep-1.jpg'),
                                                                            (104, 13, 45, 70000, '/images/dep-1.jpg');

-- Product 14: Adilette (dep-2.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (105, 14, 38, 0, '/images/dep-2.jpg'),
                                                                            (106, 14, 39, 0, '/images/dep-2.jpg'),
                                                                            (107, 14, 40, 0, '/images/dep-2.jpg'),
                                                                            (108, 14, 41, 30000, '/images/dep-2.jpg'),
                                                                            (109, 14, 42, 30000, '/images/dep-2.jpg'),
                                                                            (110, 14, 43, 50000, '/images/dep-2.jpg'),
                                                                            (111, 14, 44, 50000, '/images/dep-2.jpg'),
                                                                            (112, 14, 45, 70000, '/images/dep-2.jpg');

-- Product 15: Outdoor Adventure (sandal-2.jpg)
INSERT INTO product_detail (id, product_id, size, price_add, image_url) VALUES
                                                                            (113, 15, 38, 0, '/images/sandal-2.jpg'),
                                                                            (114, 15, 39, 0, '/images/sandal-2.jpg'),
                                                                            (115, 15, 40, 0, '/images/sandal-2.jpg'),
                                                                            (116, 15, 41, 30000, '/images/sandal-2.jpg'),
                                                                            (117, 15, 42, 30000, '/images/sandal-2.jpg'),
                                                                            (118, 15, 43, 50000, '/images/sandal-2.jpg'),
                                                                            (119, 15, 44, 50000, '/images/sandal-2.jpg'),
                                                                            (120, 15, 45, 70000, '/images/sandal-2.jpg');

-- ========================================
-- 4. USERS (4 accounts for 4 roles)
-- ========================================

-- Admin account
INSERT INTO users (id, email, password, full_name, phone, role_id, is_active, provider) VALUES
    (1, 'admin@admin', '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm', 'Admin', '0901234567', 1, true, 'LOCAL');

-- Manager account
INSERT INTO users (id, email, password, full_name, phone, role_id, is_active, provider) VALUES
    (2, 'manager@manager', '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm', 'Manager', '0902345678', 2, true, 'LOCAL');

-- Regular user account
INSERT INTO users (id, email, password, full_name, phone, role_id, is_active, provider) VALUES
    (3, 'user@user', '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm', 'User', '0903456789', 3, true, 'LOCAL');

-- Shipper account
INSERT INTO users (id, email, password, full_name, phone, role_id, is_active, provider) VALUES
    (4, 'shipper@shipper', '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm', 'Shipper', '0904567890', 4, true, 'LOCAL');