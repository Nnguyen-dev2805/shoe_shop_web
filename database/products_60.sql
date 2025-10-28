-- 60 PRODUCTS INSERT SCRIPT
-- Run after roles, categories, brands are created

-- Giày Thể Thao (6 products)
INSERT INTO product (id, title, description, category_id, brand_id, price, image, average_stars, total_reviewers) VALUES
(1, 'Nike Air Max 270', 'Giày thể thao với đệm khí Air Max', 1, 1, 2890000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-1_ofpkf9.jpg', 0, 0),
(2, 'Adidas Running Pro', 'Giày chạy đệm Boost êm ái', 1, 2, 2350000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-2_wnj5lk.jpg', 0, 0),
(3, 'Puma Training All Day', 'Giày tập đa năng bám sân tốt', 1, 3, 1750000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893901/the-thao-3_rcptay.jpg', 0, 0),
(4, 'Nike Revolution 6', 'Giày gym êm nhẹ', 1, 1, 1650000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-1_ofpkf9.jpg', 0, 0),
(5, 'Adidas Cloudfoam Pure', 'Giày thể thao nữ năng động', 1, 2, 1950000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-2_wnj5lk.jpg', 0, 0),
(6, 'Under Armour Charged', 'Giày training bền bỉ', 1, 9, 2150000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893901/the-thao-3_rcptay.jpg', 0, 0),

-- Sneaker (8 products)  
(7, 'Nike Air Force 1', 'Sneaker trắng classic', 2, 1, 2590000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sneaker-1_ibssp8.jpg', 0, 0),
(8, 'Adidas Superstar', 'Sneaker 3 sọc đen', 2, 2, 2450000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),
(9, 'Converse Chuck Taylor', 'Sneaker canvas cổ cao', 2, 4, 1290000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/sneaker-3_pwjfu2.jpg', 0, 0),
(10, 'Vans Old Skool', 'Sneaker skateboard', 2, 5, 1790000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sneaker-1_ibssp8.jpg', 0, 0),
(11, 'Puma Suede Classic', 'Sneaker da lộn retro', 2, 3, 1990000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),
(12, 'Fila Disruptor II', 'Sneaker chunky đế dày', 2, 8, 1850000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/sneaker-3_pwjfu2.jpg', 0, 0),
(13, 'New Balance 574', 'Sneaker retro thoải mái', 2, 6, 2190000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sneaker-1_ibssp8.jpg', 0, 0),
(14, 'Reebok Club C 85', 'Sneaker tennis minimal', 2, 7, 1950000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),

-- Bóng Đá (6)
(15, 'Nike Mercurial Vapor', 'Giày bóng đá tốc độ', 3, 1, 3250000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/bong-da-1_jhx4pr.jpg', 0, 0),
(16, 'Adidas Predator Edge', 'Giày sút chuẩn xác', 3, 2, 3150000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/bong-da-2_edzip1.jpg', 0, 0),
(17, 'Puma Ultra TF', 'Giày sân cỏ nhân tạo', 3, 3, 2890000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/bong-da-3_dw2ngl.jpg', 0, 0),
(18, 'Nike Phantom GT', 'Giày bám bóng tốt', 3, 1, 3450000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/bong-da-1_jhx4pr.jpg', 0, 0),
(19, 'Adidas Copa Sense', 'Giày da mềm tự nhiên', 3, 2, 2750000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/bong-da-2_edzip1.jpg', 0, 0),
(20, 'Puma Future Z', 'Giày xử lý bóng sáng tạo', 3, 3, 2950000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/bong-da-3_dw2ngl.jpg', 0, 0),

-- Bóng Rổ (6)
(21, 'Nike Air Jordan 1', 'Giày bóng rổ iconic', 4, 1, 4590000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893899/bong-ro-1_tpxjas.jpg', 0, 0),
(22, 'Adidas Dame 8', 'Giày bóng rổ pro', 4, 2, 4150000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/bong-ro-2_bpgxk1.jpg', 0, 0),
(23, 'Nike LeBron 20', 'Hỗ trợ bật nhảy', 4, 1, 5290000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/bong-ro-3_wnzw5d.jpg', 0, 0),
(24, 'Under Armour Curry 9', 'Giày guards linh hoạt', 4, 9, 4750000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893899/bong-ro-1_tpxjas.jpg', 0, 0),
(25, 'Puma Clyde All-Pro', 'Retro-modern đa năng', 4, 3, 3890000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/bong-ro-2_bpgxk1.jpg', 0, 0),
(26, 'Adidas Harden Vol. 6', 'Thiết kế độc đáo', 4, 2, 4250000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/bong-ro-3_wnzw5d.jpg', 0, 0),

-- Sandals & Dép (6)
(27, 'Nike Benassi Slide', 'Dép quai ngang êm', 5, 1, 590000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/dep-1_irwrep.jpg', 0, 0),
(28, 'Adidas Adilette', 'Dép thể thao classic', 5, 2, 750000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893895/dep-2_na32oy.jpg', 0, 0),
(29, 'Puma Leadcat Slide', 'Dép tối giản êm chân', 5, 3, 650000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/dep-1_irwrep.jpg', 0, 0),
(30, 'New Balance Sport Slide', 'Dép thể thao thoải mái', 5, 6, 690000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893895/dep-2_na32oy.jpg', 0, 0),
(31, 'Skechers Arch Fit', 'Sandal outdoor', 5, 10, 1150000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sandal-2_qztleo.jpg', 0, 0),
(32, 'Vans Slide-On', 'Dép lê thoải mái', 5, 5, 550000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893894/dep-1_irwrep.jpg', 0, 0),

-- Giày Chạy Bộ (8)
(33, 'Nike ZoomX Vaporfly', 'Marathon elite', 6, 1, 5290000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-1_ofpkf9.jpg', 0, 0),
(34, 'Adidas Ultraboost 22', 'Đệm Boost tối ưu', 6, 2, 4850000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-2_wnj5lk.jpg', 0, 0),
(35, 'New Balance 1080', 'Chạy đường dài', 6, 6, 3950000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893901/the-thao-3_rcptay.jpg', 0, 0),
(36, 'Nike Pegasus 39', 'Versatile mọi cự ly', 6, 1, 3290000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-1_ofpkf9.jpg', 0, 0),
(37, 'Adidas Supernova+', 'Cho người mới', 6, 2, 2850000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-2_wnj5lk.jpg', 0, 0),
(38, 'Puma Velocity Nitro', 'Phản hồi năng lượng', 6, 3, 3150000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893901/the-thao-3_rcptay.jpg', 0, 0),
(39, 'Skechers GoRun Max', 'Đệm tối đa', 6, 10, 2950000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-1_ofpkf9.jpg', 0, 0),
(40, 'Reebok Floatride', 'Tiết kiệm năng lượng', 6, 7, 2750000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-2_wnj5lk.jpg', 0, 0),

-- Giày Đi Bộ (6)
(41, 'Skechers Go Walk 6', 'Siêu nhẹ cả ngày', 7, 10, 1650000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),
(42, 'Nike React Infinity', 'Phòng chống chấn thương', 7, 1, 3490000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-1_ofpkf9.jpg', 0, 0),
(43, 'New Balance 880', 'Ổn định đi làm', 7, 6, 2590000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),
(44, 'Adidas Cloudfoam Adv', 'Casual dễ phối', 7, 2, 1850000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-2_wnj5lk.jpg', 0, 0),
(45, 'Puma Softride Rift', 'Đế mềm êm chân', 7, 3, 1750000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),
(46, 'Reebok Walk Ultra', 'Hỗ trợ vòm chân', 7, 7, 1950000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/the-thao-2_wnj5lk.jpg', 0, 0),

-- Giày Cao Gót (4)
(47, 'Cao Gót Mũi Nhọn 7cm', 'Thanh lịch công sở', 8, 1, 1290000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sneaker-1_ibssp8.jpg', 0, 0),
(48, 'Cao Gót Quai Mảnh 9cm', 'Sexy dự tiệc', 8, 2, 1590000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sneaker-1_ibssp8.jpg', 0, 0),
(49, 'Cao Gót Đế Vuông 5cm', 'Êm chân dễ đi', 8, 3, 1150000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sneaker-1_ibssp8.jpg', 0, 0),
(50, 'Cao Gót Platform 10cm', 'Đế platform cá tính', 8, 4, 1750000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893896/sneaker-1_ibssp8.jpg', 0, 0),

-- Giày Lười (5)
(51, 'Nike Court Legacy Slip', 'Lười thể thao tiện lợi', 9, 1, 1450000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),
(52, 'Vans Slip-On Classic', 'Canvas dễ phối', 9, 5, 1290000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),
(53, 'Skechers Slip-Ins', 'Hands-Free dễ mang', 9, 10, 1650000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),
(54, 'Adidas Adilette Comfort', 'Slide hàng ngày', 9, 2, 990000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893895/dep-2_na32oy.jpg', 0, 0),
(55, 'Converse One Star Slip', 'Retro vintage', 9, 4, 1350000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893900/sneaker-2_uoid45.jpg', 0, 0),

-- Giày Boots (5)
(56, 'Nike Manoa Leather', 'Boots da outdoor', 10, 1, 3250000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/sneaker-3_pwjfu2.jpg', 0, 0),
(57, 'Vans SK8-Hi MTE', 'Boots chống nước', 10, 5, 2590000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/sneaker-3_pwjfu2.jpg', 0, 0),
(58, 'Puma Desierto V2', 'Boots lính năng động', 10, 3, 2290000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/sneaker-3_pwjfu2.jpg', 0, 0),
(59, 'Converse Lugged', 'Đế răng cưa cá tính', 10, 4, 1990000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/sneaker-3_pwjfu2.jpg', 0, 0),
(60, 'Fila Shearling Boot', 'Chunky ấm mùa đông', 10, 8, 2750000, 'https://res.cloudinary.com/dpsj19dsn/image/upload/v1760893898/sneaker-3_pwjfu2.jpg', 0, 0);

-- Product Details for all 60 products (8 sizes each)
INSERT INTO product_detail (product_id, size, priceadd)
SELECT p.id, s.size,
  CASE WHEN s.size <= 40 THEN 0
       WHEN s.size <= 42 THEN 50000
       WHEN s.size <= 44 THEN 100000
       ELSE 150000 END
FROM product p
CROSS JOIN (SELECT 38 size UNION SELECT 39 UNION SELECT 40 UNION SELECT 41 
           UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45) s
WHERE p.id BETWEEN 1 AND 60;
