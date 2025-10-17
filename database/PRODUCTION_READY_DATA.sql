-- ========================================
-- BỘ DỮ LIỆU PRODUCTION-READY - SHOE SHOP
-- ========================================
-- Dựa trên Entity: Category, Brand, Product, ProductDetail
-- Images: Placeholder từ placehold.co (có thể thay bằng ảnh thật)
-- Data: 30 products với đầy đủ sizes
-- ========================================

-- Xóa dữ liệu cũ (cẩn thận!)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE product_detail;
TRUNCATE TABLE product;
TRUNCATE TABLE brand;
TRUNCATE TABLE category;
SET FOREIGN_KEY_CHECKS = 1;

-- ========================================
-- 1. CATEGORIES (5 loại giày phổ biến)
-- ========================================
INSERT INTO category (id, name, description) VALUES
(1, 'Giày Thể Thao', 'Giày dành cho hoạt động thể thao, chạy bộ, gym, training'),
(2, 'Giày Sneaker', 'Giày sneaker phong cách streetwear, thời trang đường phố'),
(3, 'Giày Da', 'Giày da công sở, lịch sự, chuyên nghiệp cho nam'),
(4, 'Giày Cao Gót', 'Giày cao gót sang trọng, thanh lịch cho nữ'),
(5, 'Sandals & Dép', 'Dép, sandals thoáng mát, tiện dụng hàng ngày');

-- ========================================
-- 2. BRANDS (10 thương hiệu nổi tiếng)
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
-- 3. PRODUCTS (30 sản phẩm thật)
-- ========================================

-- ===== NIKE (6 products) =====
INSERT INTO product (id, title, description, price, image, category_id, brand_id, total_reviews, average_rating, voucher, is_delete) VALUES
(1, 'Nike Air Max 270', 'Giày thể thao Nike Air Max 270 với đệm khí tối đa cho sự thoải mái', 3200000, 'https://placehold.co/600x400/FF6B6B/FFF?text=Nike+Air+Max+270', 1, 1, 156, 4.8, NULL, 0),
(2, 'Nike React Infinity Run', 'Giày chạy bộ Nike React với công nghệ đệm React foam', 3500000, 'https://placehold.co/600x400/4ECDC4/FFF?text=Nike+React', 1, 1, 203, 4.9, NULL, 0),
(3, 'Nike Air Force 1', 'Giày sneaker cổ điển Nike Air Force 1 màu trắng iconic', 2800000, 'https://placehold.co/600x400/FFE66D/000?text=Nike+AF1', 2, 1, 489, 4.7, NULL, 0),
(4, 'Nike Blazer Mid 77', 'Giày sneaker Nike Blazer Mid phong cách retro vintage', 2900000, 'https://placehold.co/600x400/A8E6CF/000?text=Nike+Blazer', 2, 1, 178, 4.6, NULL, 0),
(5, 'Nike Zoom Pegasus 39', 'Giày chạy bộ Nike Zoom Pegasus cho runner chuyên nghiệp', 3100000, 'https://placehold.co/600x400/FF6B9D/FFF?text=Nike+Pegasus', 1, 1, 267, 4.8, NULL, 0),
(6, 'Nike Court Vision Low', 'Giày sneaker Nike Court Vision phong cách basketball', 1900000, 'https://placehold.co/600x400/C7CEEA/000?text=Nike+Court', 2, 1, 134, 4.5, NULL, 0),

-- ===== ADIDAS (6 products) =====
(7, 'Adidas Ultraboost 22', 'Giày chạy bộ Adidas Ultraboost 22 với công nghệ Boost foam', 4200000, 'https://placehold.co/600x400/000000/FFF?text=Adidas+Ultraboost', 1, 2, 312, 4.9, NULL, 0),
(8, 'Adidas Superstar', 'Giày sneaker Adidas Superstar cổ điển với 3 sọc và mũi sò', 2200000, 'https://placehold.co/600x400/FFFFFF/000?text=Adidas+Superstar', 2, 2, 567, 4.7, NULL, 0),
(9, 'Adidas Stan Smith', 'Giày sneaker Adidas Stan Smith trắng xanh lá huyền thoại', 2400000, 'https://placehold.co/600x400/B4F8C8/000?text=Stan+Smith', 2, 2, 678, 4.8, NULL, 0),
(10, 'Adidas NMD R1', 'Giày sneaker Adidas NMD R1 phong cách urban streetwear', 3300000, 'https://placehold.co/600x400/FBE7C6/000?text=Adidas+NMD', 2, 2, 245, 4.6, NULL, 0),
(11, 'Adidas Predator Edge', 'Giày bóng đá Adidas Predator Edge với đế AG chuyên nghiệp', 3800000, 'https://placehold.co/600x400/FF4747/FFF?text=Predator', 1, 2, 189, 4.7, NULL, 0),
(12, 'Adidas Samba Classic', 'Giày sneaker Adidas Samba phong cách retro soccer', 2100000, 'https://placehold.co/600x400/333333/FFF?text=Adidas+Samba', 2, 2, 423, 4.7, NULL, 0),

-- ===== PUMA (4 products) =====
(13, 'Puma RS-X', 'Giày sneaker Puma RS-X phong cách chunky dad shoes', 2600000, 'https://placehold.co/600x400/F4A261/000?text=Puma+RSX', 2, 3, 167, 4.5, NULL, 0),
(14, 'Puma Suede Classic', 'Giày sneaker Puma Suede Classic da lộn cao cấp', 1800000, 'https://placehold.co/600x400/2A9D8F/FFF?text=Puma+Suede', 2, 3, 289, 4.6, NULL, 0),
(15, 'Puma Velocity Nitro 2', 'Giày chạy bộ Puma Velocity Nitro công nghệ Nitro foam', 2900000, 'https://placehold.co/600x400/E76F51/FFF?text=Puma+Velocity', 1, 3, 134, 4.7, NULL, 0),
(16, 'Puma Cali Sport', 'Giày sneaker Puma Cali Sport dành cho nữ phong cách LA', 2300000, 'https://placehold.co/600x400/FFB4B4/000?text=Puma+Cali', 2, 3, 198, 4.5, NULL, 0),

-- ===== CONVERSE (3 products) =====
(17, 'Converse Chuck Taylor All Star', 'Giày sneaker Converse Chuck Taylor Classic đen huyền thoại', 1200000, 'https://placehold.co/600x400/000000/FFF?text=Converse+Chuck', 2, 4, 892, 4.8, NULL, 0),
(18, 'Converse Chuck 70 High Top', 'Giày sneaker Converse Chuck 70 cao cổ phiên bản premium', 1800000, 'https://placehold.co/600x400/FFEAA7/000?text=Chuck+70', 2, 4, 456, 4.7, NULL, 0),
(19, 'Converse Run Star Hike', 'Giày sneaker Converse Run Star Hike đế cao độc đáo', 2200000, 'https://placehold.co/600x400/FFFFFF/000?text=Run+Star+Hike', 2, 4, 234, 4.6, NULL, 0),

-- ===== VANS (3 products) =====
(20, 'Vans Old Skool', 'Giày sneaker Vans Old Skool với sọc trắng iconic', 1500000, 'https://placehold.co/600x400/000000/FFF?text=Vans+Old+Skool', 2, 5, 623, 4.7, NULL, 0),
(21, 'Vans Authentic', 'Giày sneaker Vans Authentic cổ điển skate shoes', 1200000, 'https://placehold.co/600x400/EE5A6F/FFF?text=Vans+Authentic', 2, 5, 478, 4.6, NULL, 0),
(22, 'Vans Sk8-Hi', 'Giày sneaker Vans Sk8-Hi cao cổ phong cách skater', 1700000, 'https://placehold.co/600x400/FAD02C/000?text=Vans+Sk8Hi', 2, 5, 345, 4.7, NULL, 0),

-- ===== NEW BALANCE (2 products) =====
(23, 'New Balance 574', 'Giày sneaker New Balance 574 phong cách retro runner', 2400000, 'https://placehold.co/600x400/95B8D1/000?text=NB+574', 2, 6, 312, 4.6, NULL, 0),
(24, 'New Balance 990v5', 'Giày sneaker New Balance 990v5 Made in USA cao cấp', 5200000, 'https://placehold.co/600x400/D4A5A5/FFF?text=NB+990v5', 2, 6, 178, 4.8, NULL, 0),

-- ===== REEBOK (2 products) =====
(25, 'Reebok Classic Leather', 'Giày sneaker Reebok Classic Leather trắng cổ điển', 1800000, 'https://placehold.co/600x400/FFFFFF/000?text=Reebok+Classic', 2, 7, 423, 4.5, NULL, 0),
(26, 'Reebok Nano X2', 'Giày tập gym Reebok Nano X2 cho CrossFit training', 3100000, 'https://placehold.co/600x400/FF6B6B/FFF?text=Nano+X2', 1, 7, 189, 4.7, NULL, 0),

-- ===== FILA (2 products) =====
(27, 'Fila Disruptor II', 'Giày sneaker Fila Disruptor II chunky trắng trendy', 1900000, 'https://placehold.co/600x400/FFFFFF/000?text=Fila+Disruptor', 2, 8, 567, 4.6, NULL, 0),
(28, 'Fila Ray Tracer', 'Giày sneaker Fila Ray Tracer đa màu phong cách 90s', 2100000, 'https://placehold.co/600x400/C9ADA7/000?text=Fila+Ray', 2, 8, 234, 4.5, NULL, 0),

-- ===== UNDER ARMOUR (1 product) =====
(29, 'Under Armour HOVR Phantom 2', 'Giày chạy bộ UA HOVR Phantom 2 với công nghệ HOVR', 3400000, 'https://placehold.co/600x400/000000/FFF?text=UA+HOVR', 1, 9, 145, 4.7, NULL, 0),

-- ===== SKECHERS (1 product) =====
(30, 'Skechers Go Walk 5', 'Giày đi bộ Skechers Go Walk 5 êm ái thoải mái', 1600000, 'https://placehold.co/600x400/00A896/FFF?text=Skechers+Walk', 1, 10, 234, 4.6, NULL, 0);

-- ========================================
-- 4. PRODUCT_DETAILS (Sizes)
-- ========================================
-- Mỗi product có 7-8 sizes (từ 36-43)
-- priceadd: 0 cho sizes nhỏ, +50k-100k cho sizes lớn (42-43)

-- Helper function to generate sizes
-- Nike products (1-6): sizes 36-43
INSERT INTO product_detail (product_id, size, priceadd) VALUES
-- Product 1: Nike Air Max 270
(1, 36, 0), (1, 37, 0), (1, 38, 0), (1, 39, 0), (1, 40, 0), (1, 41, 0), (1, 42, 50000), (1, 43, 50000),
-- Product 2: Nike React
(2, 36, 0), (2, 37, 0), (2, 38, 0), (2, 39, 0), (2, 40, 0), (2, 41, 0), (2, 42, 50000), (2, 43, 50000),
-- Product 3: Nike AF1
(3, 36, 0), (3, 37, 0), (3, 38, 0), (3, 39, 0), (3, 40, 0), (3, 41, 0), (3, 42, 50000), (3, 43, 50000),
-- Product 4: Nike Blazer
(4, 36, 0), (4, 37, 0), (4, 38, 0), (4, 39, 0), (4, 40, 0), (4, 41, 0), (4, 42, 50000), (4, 43, 50000),
-- Product 5: Nike Pegasus
(5, 36, 0), (5, 37, 0), (5, 38, 0), (5, 39, 0), (5, 40, 0), (5, 41, 0), (5, 42, 50000), (5, 43, 50000),
-- Product 6: Nike Court
(6, 36, 0), (6, 37, 0), (6, 38, 0), (6, 39, 0), (6, 40, 0), (6, 41, 0), (6, 42, 50000), (6, 43, 50000),

-- Adidas products (7-12): sizes 36-43
-- Product 7: Adidas Ultraboost
(7, 36, 0), (7, 37, 0), (7, 38, 0), (7, 39, 0), (7, 40, 0), (7, 41, 0), (7, 42, 100000), (7, 43, 100000),
-- Product 8: Adidas Superstar
(8, 36, 0), (8, 37, 0), (8, 38, 0), (8, 39, 0), (8, 40, 0), (8, 41, 0), (8, 42, 50000), (8, 43, 50000),
-- Product 9: Stan Smith
(9, 36, 0), (9, 37, 0), (9, 38, 0), (9, 39, 0), (9, 40, 0), (9, 41, 0), (9, 42, 50000), (9, 43, 50000),
-- Product 10: Adidas NMD
(10, 36, 0), (10, 37, 0), (10, 38, 0), (10, 39, 0), (10, 40, 0), (10, 41, 0), (10, 42, 50000), (10, 43, 50000),
-- Product 11: Predator
(11, 36, 0), (11, 37, 0), (11, 38, 0), (11, 39, 0), (11, 40, 0), (11, 41, 0), (11, 42, 50000), (11, 43, 50000),
-- Product 12: Samba
(12, 36, 0), (12, 37, 0), (12, 38, 0), (12, 39, 0), (12, 40, 0), (12, 41, 0), (12, 42, 50000), (12, 43, 50000),

-- Puma products (13-16): sizes 36-43
-- Product 13: RS-X
(13, 36, 0), (13, 37, 0), (13, 38, 0), (13, 39, 0), (13, 40, 0), (13, 41, 0), (13, 42, 50000), (13, 43, 50000),
-- Product 14: Suede
(14, 36, 0), (14, 37, 0), (14, 38, 0), (14, 39, 0), (14, 40, 0), (14, 41, 0), (14, 42, 0), (14, 43, 0),
-- Product 15: Velocity
(15, 36, 0), (15, 37, 0), (15, 38, 0), (15, 39, 0), (15, 40, 0), (15, 41, 0), (15, 42, 50000), (15, 43, 50000),
-- Product 16: Cali (nữ, sizes nhỏ hơn)
(16, 35, 0), (16, 36, 0), (16, 37, 0), (16, 38, 0), (16, 39, 0), (16, 40, 0), (16, 41, 0),

-- Converse products (17-19): sizes 36-43
-- Product 17: Chuck Taylor
(17, 36, 0), (17, 37, 0), (17, 38, 0), (17, 39, 0), (17, 40, 0), (17, 41, 0), (17, 42, 0), (17, 43, 0),
-- Product 18: Chuck 70
(18, 36, 0), (18, 37, 0), (18, 38, 0), (18, 39, 0), (18, 40, 0), (18, 41, 0), (18, 42, 0), (18, 43, 0),
-- Product 19: Run Star Hike
(19, 36, 0), (19, 37, 0), (19, 38, 0), (19, 39, 0), (19, 40, 0), (19, 41, 0), (19, 42, 0),

-- Vans products (20-22): sizes 36-43
-- Product 20: Old Skool
(20, 36, 0), (20, 37, 0), (20, 38, 0), (20, 39, 0), (20, 40, 0), (20, 41, 0), (20, 42, 0), (20, 43, 0),
-- Product 21: Authentic
(21, 36, 0), (21, 37, 0), (21, 38, 0), (21, 39, 0), (21, 40, 0), (21, 41, 0), (21, 42, 0), (21, 43, 0),
-- Product 22: Sk8-Hi
(22, 36, 0), (22, 37, 0), (22, 38, 0), (22, 39, 0), (22, 40, 0), (22, 41, 0), (22, 42, 0), (22, 43, 0),

-- New Balance products (23-24): sizes 36-43
-- Product 23: 574
(23, 36, 0), (23, 37, 0), (23, 38, 0), (23, 39, 0), (23, 40, 0), (23, 41, 0), (23, 42, 50000), (23, 43, 50000),
-- Product 24: 990v5 (premium, priceadd cao hơn)
(24, 36, 0), (24, 37, 0), (24, 38, 0), (24, 39, 0), (24, 40, 0), (24, 41, 0), (24, 42, 100000), (24, 43, 100000),

-- Reebok products (25-26): sizes 36-43
-- Product 25: Classic Leather
(25, 36, 0), (25, 37, 0), (25, 38, 0), (25, 39, 0), (25, 40, 0), (25, 41, 0), (25, 42, 0), (25, 43, 0),
-- Product 26: Nano X2
(26, 36, 0), (26, 37, 0), (26, 38, 0), (26, 39, 0), (26, 40, 0), (26, 41, 0), (26, 42, 50000), (26, 43, 50000),

-- Fila products (27-28): sizes 36-43
-- Product 27: Disruptor II
(27, 36, 0), (27, 37, 0), (27, 38, 0), (27, 39, 0), (27, 40, 0), (27, 41, 0), (27, 42, 0), (27, 43, 0),
-- Product 28: Ray Tracer
(28, 36, 0), (28, 37, 0), (28, 38, 0), (28, 39, 0), (28, 40, 0), (28, 41, 0), (28, 42, 0), (28, 43, 0),

-- Under Armour product (29): sizes 36-43
-- Product 29: HOVR
(29, 36, 0), (29, 37, 0), (29, 38, 0), (29, 39, 0), (29, 40, 0), (29, 41, 0), (29, 42, 50000), (29, 43, 50000),

-- Skechers product (30): sizes 36-43
-- Product 30: Go Walk
(30, 36, 0), (30, 37, 0), (30, 38, 0), (30, 39, 0), (30, 40, 0), (30, 41, 0), (30, 42, 0), (30, 43, 0);

-- ========================================
-- 5. KIỂM TRA KẾT QUẢ
-- ========================================
SELECT 
    'Summary' as Info,
    (SELECT COUNT(*) FROM category) as Categories,
    (SELECT COUNT(*) FROM brand) as Brands,
    (SELECT COUNT(*) FROM product) as Products,
    (SELECT COUNT(*) FROM product_detail) as ProductDetails;

-- Chi tiết theo brand
SELECT 
    b.name as Brand,
    COUNT(p.id) as ProductCount,
    COUNT(pd.id) as TotalSizes
FROM brand b
LEFT JOIN product p ON p.brand_id = b.id
LEFT JOIN product_detail pd ON pd.product_id = p.id
GROUP BY b.id, b.name
ORDER BY ProductCount DESC;

-- Chi tiết theo category
SELECT 
    c.name as Category,
    COUNT(p.id) as ProductCount
FROM category c
LEFT JOIN product p ON p.category_id = c.id
GROUP BY c.id, c.name
ORDER BY ProductCount DESC;

-- ========================================
-- HOÀN TẤT! 🎉
-- ========================================
-- ✅ 5 Categories
-- ✅ 10 Brands (Nike, Adidas, Puma, v.v.)
-- ✅ 30 Products (đa dạng giày thể thao, sneaker)
-- ✅ ~230 ProductDetails (mỗi product có 7-8 sizes)
-- 
-- 📝 LƯU Ý:
-- - Images đang dùng placeholder từ placehold.co
-- - Có thể thay bằng URL ảnh thật sau
-- - Hoặc upload ảnh vào /uploads và update image path
-- 
-- 🚀 READY TO DEPLOY!
-- ========================================
