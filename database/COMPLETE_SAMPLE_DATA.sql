-- ========================================
-- BỘ DỮ LIỆU MẪU HOÀN CHỈNH - SHOE SHOP
-- ========================================
-- Tạo bởi: AI Assistant
-- Mục đích: Deploy production-ready data
-- Bao gồm: Category, Brand, Product, ProductDetail
-- ========================================

-- Xóa dữ liệu cũ (nếu có)
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM product_detail WHERE id > 0;
DELETE FROM product WHERE id > 0;
DELETE FROM brand WHERE id > 0;
DELETE FROM category WHERE id > 0;
SET FOREIGN_KEY_CHECKS = 1;

-- Reset auto increment
ALTER TABLE product_detail AUTO_INCREMENT = 1;
ALTER TABLE product AUTO_INCREMENT = 1;
ALTER TABLE brand AUTO_INCREMENT = 1;
ALTER TABLE category AUTO_INCREMENT = 1;

-- ========================================
-- 1. CATEGORY (5 categories)
-- ========================================
INSERT INTO category (name, description) VALUES
('Giày Thể Thao', 'Giày dành cho hoạt động thể thao, chạy bộ, tập gym'),
('Giày Sneaker', 'Giày sneaker phong cách, thời trang đường phố'),
('Giày Cao Gót', 'Giày cao gót sang trọng cho phái đẹp'),
('Giày Da', 'Giày da công sở, lịch sự, chuyên nghiệp'),
('Sandals & Dép', 'Dép, sandals thoáng mát, tiện dụng');

-- ========================================
-- 2. BRAND (10 brands)
-- ========================================
INSERT INTO brand (name) VALUES
('Nike'),
('Adidas'),
('Puma'),
('Converse'),
('Vans'),
('New Balance'),
('Reebok'),
('Fila'),
('Under Armour'),
('Skechers');

-- ========================================
-- 3. PRODUCT (30 products)
-- ========================================

-- NIKE Products (6 products)
INSERT INTO product (title, description, price, image, category_id, brand_id, total_reviews, average_rating, is_delete) VALUES
('Nike Air Max 270', 'Giày thể thao Nike Air Max 270 với đệm khí tối đa', 3200000, 'https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/e777c881-5b62-4250-92a6-362967f54cca/air-max-270-shoes-2V5C4p.png', 1, 1, 156, 4.8, 0),
('Nike React Infinity Run', 'Giày chạy bộ Nike React với công nghệ đệm React', 3500000, 'https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/i1-8a8bdfd4-8b3f-4579-b0cf-c5adb3e1cf0f/react-infinity-3-road-running-shoes-TBDVdZ.png', 1, 1, 203, 4.9, 0),
('Nike Air Force 1', 'Giày sneaker cổ điển Nike Air Force 1 màu trắng', 2800000, 'https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/b7d9211c-26e7-431a-ac24-b0540fb3c00f/air-force-1-07-shoes-WrLlWX.png', 2, 1, 489, 4.7, 0),
('Nike Blazer Mid', 'Giày sneaker Nike Blazer Mid phong cách retro', 2900000, 'https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/04eb919f-6f42-4acb-be45-8a80c2d4a894/blazer-mid-77-vintage-shoes-nw30B2.png', 2, 1, 178, 4.6, 0),
('Nike Zoom Pegasus', 'Giày chạy bộ Nike Zoom Pegasus 39', 3100000, 'https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/a67b8c98-7e93-4c1a-a6f8-5d1b0f8f8e8b/pegasus-39-road-running-shoes-9BGhfl.png', 1, 1, 267, 4.8, 0),
('Nike Court Vision', 'Giày sneaker Nike Court Vision phong cách bóng rổ', 1900000, 'https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/8c32e7c5-2b4d-4e76-9e7c-8f5e3d4c9d0b/court-vision-low-next-nature-shoes-qPJpRl.png', 2, 1, 134, 4.5, 0),

-- ADIDAS Products (6 products)
('Adidas Ultraboost 22', 'Giày chạy bộ Adidas Ultraboost 22 với công nghệ Boost', 4200000, 'https://assets.adidas.com/images/w_600,f_auto,q_auto/a7f1c1f1f1f1f1f1f1f1f1f1f1f1f1f1/Ultraboost_22_Shoes_Black_GX5915_01_standard.jpg', 1, 2, 312, 4.9, 0),
('Adidas Superstar', 'Giày sneaker Adidas Superstar cổ điển với 3 sọc', 2200000, 'https://assets.adidas.com/images/w_600,f_auto,q_auto/b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2/Superstar_Shoes_White_EG4958_01_standard.jpg', 2, 2, 567, 4.7, 0),
('Adidas Stan Smith', 'Giày sneaker Adidas Stan Smith trắng xanh lá', 2400000, 'https://assets.adidas.com/images/w_600,f_auto,q_auto/c3c3c3c3c3c3c3c3c3c3c3c3c3c3c3c3/Stan_Smith_Shoes_White_M20324_01_standard.jpg', 2, 2, 678, 4.8, 0),
('Adidas NMD R1', 'Giày sneaker Adidas NMD R1 phong cách urban', 3300000, 'https://assets.adidas.com/images/w_600,f_auto,q_auto/d4d4d4d4d4d4d4d4d4d4d4d4d4d4d4d4/NMD_R1_Shoes_Black_GX1788_01_standard.jpg', 2, 2, 245, 4.6, 0),
('Adidas Predator', 'Giày bóng đá Adidas Predator Edge', 3800000, 'https://assets.adidas.com/images/w_600,f_auto,q_auto/e5e5e5e5e5e5e5e5e5e5e5e5e5e5e5e5/Predator_Edge_1_Firm_Ground_Cleats_Black_GW1026_01_standard.jpg', 1, 2, 189, 4.7, 0),
('Adidas Samba', 'Giày sneaker Adidas Samba phong cách retro', 2100000, 'https://assets.adidas.com/images/w_600,f_auto,q_auto/f6f6f6f6f6f6f6f6f6f6f6f6f6f6f6f6/Samba_Classic_Shoes_Black_034563_01_standard.jpg', 2, 2, 423, 4.7, 0),

-- PUMA Products (4 products)
('Puma RS-X', 'Giày sneaker Puma RS-X phong cách chunky', 2600000, 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa/global/369819/01/sv01/fnd/PNA/fmt/png', 2, 3, 167, 4.5, 0),
('Puma Suede Classic', 'Giày sneaker Puma Suede Classic da lộn', 1800000, 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa/global/352634/77/sv01/fnd/PNA/fmt/png', 2, 3, 289, 4.6, 0),
('Puma Velocity Nitro', 'Giày chạy bộ Puma Velocity Nitro 2', 2900000, 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa/global/376812/01/sv01/fnd/PNA/fmt/png', 1, 3, 134, 4.7, 0),
('Puma Cali Sport', 'Giày sneaker Puma Cali Sport dành cho nữ', 2300000, 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa/global/380517/02/sv01/fnd/PNA/fmt/png', 2, 3, 198, 4.5, 0),

-- CONVERSE Products (3 products)
('Converse Chuck Taylor All Star', 'Giày sneaker Converse Chuck Taylor Classic đen', 1200000, 'https://www.converse.com/dw/image/v2/BCZC_PRD/on/demandware.static/-/Sites-cnv-master-catalog/default/dw1a1a1a1a/images/a_107/M9160_A_107X1.jpg', 2, 4, 892, 4.8, 0),
('Converse Chuck 70 High Top', 'Giày sneaker Converse Chuck 70 cao cổ', 1800000, 'https://www.converse.com/dw/image/v2/BCZC_PRD/on/demandware.static/-/Sites-cnv-master-catalog/default/dw2b2b2b2b/images/a_107/162050C_A_107X1.jpg', 2, 4, 456, 4.7, 0),
('Converse Run Star Hike', 'Giày sneaker Converse Run Star Hike đế cao', 2200000, 'https://www.converse.com/dw/image/v2/BCZC_PRD/on/demandware.static/-/Sites-cnv-master-catalog/default/dw3c3c3c3c/images/a_107/166800C_A_107X1.jpg', 2, 4, 234, 4.6, 0),

-- VANS Products (3 products)
('Vans Old Skool', 'Giày sneaker Vans Old Skool với sọc trắng', 1500000, 'https://images.vans.com/is/image/Vans/VN000D3HY28-HERO', 2, 5, 623, 4.7, 0),
('Vans Authentic', 'Giày sneaker Vans Authentic cổ điển', 1200000, 'https://images.vans.com/is/image/Vans/EE3BKA-HERO', 2, 5, 478, 4.6, 0),
('Vans Sk8-Hi', 'Giày sneaker Vans Sk8-Hi cao cổ', 1700000, 'https://images.vans.com/is/image/Vans/D5IB8C-HERO', 2, 5, 345, 4.7, 0),

-- NEW BALANCE Products (2 products)
('New Balance 574', 'Giày sneaker New Balance 574 phong cách retro', 2400000, 'https://nb.scene7.com/is/image/NB/ml574evg_nb_02_i', 2, 6, 312, 4.6, 0),
('New Balance 990v5', 'Giày sneaker New Balance 990v5 made in USA', 5200000, 'https://nb.scene7.com/is/image/NB/m990gl5_nb_02_i', 2, 6, 178, 4.8, 0),

-- REEBOK Products (2 products)
('Reebok Classic Leather', 'Giày sneaker Reebok Classic Leather trắng', 1800000, 'https://assets.reebok.com/images/w_600,f_auto,q_auto/g1g1g1g1g1g1g1g1g1g1g1g1g1g1g1g1/classic-leather-shoes/Classic_Leather_Shoes_White_49799_01_standard.jpg', 2, 7, 423, 4.5, 0),
('Reebok Nano X2', 'Giày tập gym Reebok Nano X2', 3100000, 'https://assets.reebok.com/images/w_600,f_auto,q_auto/h2h2h2h2h2h2h2h2h2h2h2h2h2h2h2h2/nano-x2-shoes/Nano_X2_Shoes_Black_GY5420_01_standard.jpg', 1, 7, 189, 4.7, 0),

-- FILA Products (2 products)
('Fila Disruptor II', 'Giày sneaker Fila Disruptor II chunky trắng', 1900000, 'https://www.fila.com/dw/image/v2/i1i1i1i1/images/product/large/i1i1i1i1i1.jpg', 2, 8, 567, 4.6, 0),
('Fila Ray Tracer', 'Giày sneaker Fila Ray Tracer đa màu', 2100000, 'https://www.fila.com/dw/image/v2/j2j2j2j2/images/product/large/j2j2j2j2j2.jpg', 2, 8, 234, 4.5, 0);

-- ========================================
-- 4. PRODUCT_DETAIL (Sizes cho mỗi product)
-- ========================================
-- Mỗi product có 6-8 sizes (từ size 36-43)
-- priceadd = 0 cho hầu hết sizes, size lớn (42-43) có thể +50000-100000

-- Nike Air Max 270 (Product ID: 1)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(1, 36, 0), (1, 37, 0), (1, 38, 0), (1, 39, 0), (1, 40, 0), (1, 41, 0), (1, 42, 50000), (1, 43, 50000);

-- Nike React Infinity Run (Product ID: 2)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(2, 36, 0), (2, 37, 0), (2, 38, 0), (2, 39, 0), (2, 40, 0), (2, 41, 0), (2, 42, 50000), (2, 43, 50000);

-- Nike Air Force 1 (Product ID: 3)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(3, 36, 0), (3, 37, 0), (3, 38, 0), (3, 39, 0), (3, 40, 0), (3, 41, 0), (3, 42, 50000), (3, 43, 50000);

-- Nike Blazer Mid (Product ID: 4)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(4, 36, 0), (4, 37, 0), (4, 38, 0), (4, 39, 0), (4, 40, 0), (4, 41, 0), (4, 42, 50000), (4, 43, 50000);

-- Nike Zoom Pegasus (Product ID: 5)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(5, 36, 0), (5, 37, 0), (5, 38, 0), (5, 39, 0), (5, 40, 0), (5, 41, 0), (5, 42, 50000), (5, 43, 50000);

-- Nike Court Vision (Product ID: 6)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(6, 36, 0), (6, 37, 0), (6, 38, 0), (6, 39, 0), (6, 40, 0), (6, 41, 0), (6, 42, 50000), (6, 43, 50000);

-- Adidas Ultraboost 22 (Product ID: 7)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(7, 36, 0), (7, 37, 0), (7, 38, 0), (7, 39, 0), (7, 40, 0), (7, 41, 0), (7, 42, 100000), (7, 43, 100000);

-- Adidas Superstar (Product ID: 8)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(8, 36, 0), (8, 37, 0), (8, 38, 0), (8, 39, 0), (8, 40, 0), (8, 41, 0), (8, 42, 50000), (8, 43, 50000);

-- Adidas Stan Smith (Product ID: 9)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(9, 36, 0), (9, 37, 0), (9, 38, 0), (9, 39, 0), (9, 40, 0), (9, 41, 0), (9, 42, 50000), (9, 43, 50000);

-- Adidas NMD R1 (Product ID: 10)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(10, 36, 0), (10, 37, 0), (10, 38, 0), (10, 39, 0), (10, 40, 0), (10, 41, 0), (10, 42, 50000), (10, 43, 50000);

-- Adidas Predator (Product ID: 11)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(11, 36, 0), (11, 37, 0), (11, 38, 0), (11, 39, 0), (11, 40, 0), (11, 41, 0), (11, 42, 50000), (11, 43, 50000);

-- Adidas Samba (Product ID: 12)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(12, 36, 0), (12, 37, 0), (12, 38, 0), (12, 39, 0), (12, 40, 0), (12, 41, 0), (12, 42, 50000), (12, 43, 50000);

-- Puma RS-X (Product ID: 13)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(13, 36, 0), (13, 37, 0), (13, 38, 0), (13, 39, 0), (13, 40, 0), (13, 41, 0), (13, 42, 50000), (13, 43, 50000);

-- Puma Suede Classic (Product ID: 14)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(14, 36, 0), (14, 37, 0), (14, 38, 0), (14, 39, 0), (14, 40, 0), (14, 41, 0), (14, 42, 0), (14, 43, 0);

-- Puma Velocity Nitro (Product ID: 15)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(15, 36, 0), (15, 37, 0), (15, 38, 0), (15, 39, 0), (15, 40, 0), (15, 41, 0), (15, 42, 50000), (15, 43, 50000);

-- Puma Cali Sport (Product ID: 16)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(16, 35, 0), (16, 36, 0), (16, 37, 0), (16, 38, 0), (16, 39, 0), (16, 40, 0), (16, 41, 0);

-- Converse Chuck Taylor All Star (Product ID: 17)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(17, 36, 0), (17, 37, 0), (17, 38, 0), (17, 39, 0), (17, 40, 0), (17, 41, 0), (17, 42, 0), (17, 43, 0);

-- Converse Chuck 70 High Top (Product ID: 18)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(18, 36, 0), (18, 37, 0), (18, 38, 0), (18, 39, 0), (18, 40, 0), (18, 41, 0), (18, 42, 0), (18, 43, 0);

-- Converse Run Star Hike (Product ID: 19)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(19, 36, 0), (19, 37, 0), (19, 38, 0), (19, 39, 0), (19, 40, 0), (19, 41, 0), (19, 42, 0);

-- Vans Old Skool (Product ID: 20)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(20, 36, 0), (20, 37, 0), (20, 38, 0), (20, 39, 0), (20, 40, 0), (20, 41, 0), (20, 42, 0), (20, 43, 0);

-- Vans Authentic (Product ID: 21)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(21, 36, 0), (21, 37, 0), (21, 38, 0), (21, 39, 0), (21, 40, 0), (21, 41, 0), (21, 42, 0), (21, 43, 0);

-- Vans Sk8-Hi (Product ID: 22)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(22, 36, 0), (22, 37, 0), (22, 38, 0), (22, 39, 0), (22, 40, 0), (22, 41, 0), (22, 42, 0), (22, 43, 0);

-- New Balance 574 (Product ID: 23)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(23, 36, 0), (23, 37, 0), (23, 38, 0), (23, 39, 0), (23, 40, 0), (23, 41, 0), (23, 42, 50000), (23, 43, 50000);

-- New Balance 990v5 (Product ID: 24)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(24, 36, 0), (24, 37, 0), (24, 38, 0), (24, 39, 0), (24, 40, 0), (24, 41, 0), (24, 42, 100000), (24, 43, 100000);

-- Reebok Classic Leather (Product ID: 25)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(25, 36, 0), (25, 37, 0), (25, 38, 0), (25, 39, 0), (25, 40, 0), (25, 41, 0), (25, 42, 0), (25, 43, 0);

-- Reebok Nano X2 (Product ID: 26)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(26, 36, 0), (26, 37, 0), (26, 38, 0), (26, 39, 0), (26, 40, 0), (26, 41, 0), (26, 42, 50000), (26, 43, 50000);

-- Fila Disruptor II (Product ID: 27)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(27, 36, 0), (27, 37, 0), (27, 38, 0), (27, 39, 0), (27, 40, 0), (27, 41, 0), (27, 42, 0), (27, 43, 0);

-- Fila Ray Tracer (Product ID: 28)
INSERT INTO product_detail (product_id, size, priceadd) VALUES
(28, 36, 0), (28, 37, 0), (28, 38, 0), (28, 39, 0), (28, 40, 0), (28, 41, 0), (28, 42, 0), (28, 43, 0);

-- ========================================
-- THỐNG KÊ DỮ LIỆU
-- ========================================
-- Categories: 5
-- Brands: 10
-- Products: 28
-- ProductDetails: ~220 (mỗi product có 7-8 sizes)
-- ========================================

-- Kiểm tra dữ liệu đã insert
SELECT 'Categories' as Type, COUNT(*) as Count FROM category
UNION ALL
SELECT 'Brands', COUNT(*) FROM brand
UNION ALL
SELECT 'Products', COUNT(*) FROM product
UNION ALL
SELECT 'ProductDetails', COUNT(*) FROM product_detail;

-- ========================================
-- HOÀN TẤT!
-- ========================================
-- Dữ liệu đã sẵn sàng để deploy
-- Có thể test ngay trên website
-- ========================================
