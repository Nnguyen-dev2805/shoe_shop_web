-- ==========================================
-- 📊 DATASET THỐNG KÊ 3 THÁNG: 8, 9, 10/2024
-- ==========================================
-- Dựa trên ApplicationInitConfig.java
-- User IDs: 3-8 (6 users)
-- ProductDetail IDs: 1-120
-- Cost Price = Product Base Price × 0.7
-- ==========================================

-- MAPPING SẢN PHẨM:
-- Product 1: Giày Thể Thao Nam (1.890.000đ → Cost: 1.323.000đ) → PD 1-8
-- Product 2: Running Pro (2.350.000đ → Cost: 1.645.000đ) → PD 9-16
-- Product 3: Training All Day (1.750.000đ → Cost: 1.225.000đ) → PD 17-24
-- Product 4: Sneaker Đỏ (1.590.000đ → Cost: 1.113.000đ) → PD 25-32
-- Product 10: Air Jordan (4.590.000đ → Cost: 3.213.000đ) → PD 73-80

-- ==========================================
-- ⚠️ LƯU Ý QUAN TRỌNG
-- ==========================================
-- ApplicationInitConfig.java đã tạo sẵn:
--   ✅ 11 users (ID 1-11): admin, manager, user, shipper, 5 customers, 2 shippers
--   ✅ 120 product_details (ID 1-120)
--   ✅ Address ID = 1: "38 Hẻm 268 Nguyễn Văn Quá, Đông Hưng Thuận, Quận 12"
--   ✅ Đã link address với users 3-8 (6 customers)
--   ❌ KHÔNG dùng initInventory() (để tránh data tháng 10)
-- 
-- → File này tạo:
--   1. Inventory records (với quantity = 0)
--   2. Inventory_history (nhập hàng 3 tháng)
--   3. UPDATE inventory (cộng quantity từ history)
--   4. Orders + order_details (24 đơn DELIVERED)

-- ==========================================
-- 📦 BƯỚC 1: TẠO INVENTORY RECORDS
-- ==========================================
-- Tạo inventory cho các product_detail_id sẽ dùng (nếu chưa có)
INSERT INTO inventory (product_detail_id, remaining_quantity, total_quantity) VALUES
(1, 0, 0), (2, 0, 0), (3, 0, 0),
(10, 0, 0), (11, 0, 0), (12, 0, 0),
(17, 0, 0), (18, 0, 0), (19, 0, 0),
(25, 0, 0), (26, 0, 0), (27, 0, 0),
(73, 0, 0), (74, 0, 0), (75, 0, 0)
ON DUPLICATE KEY UPDATE product_detail_id = product_detail_id;

-- ==========================================
-- 📦 THÁNG 8/2025: NHẬP KHO
-- ==========================================

-- Lô 1: 05/08 - Product 1 (size 38-40)
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(1, '2025-08-05 09:00:00', 50, 1323000, 'Lô 1 - Giày Thể Thao - Size 38'),
(2, '2025-08-05 09:00:00', 50, 1323000, 'Lô 1 - Giày Thể Thao - Size 39'),
(3, '2025-08-05 09:00:00', 60, 1323000, 'Lô 1 - Giày Thể Thao - Size 40');
UPDATE inventory SET remaining_quantity = remaining_quantity + 50, total_quantity = total_quantity + 50 WHERE product_detail_id = 1;
UPDATE inventory SET remaining_quantity = remaining_quantity + 50, total_quantity = total_quantity + 50 WHERE product_detail_id = 2;
UPDATE inventory SET remaining_quantity = remaining_quantity + 60, total_quantity = total_quantity + 60 WHERE product_detail_id = 3;

-- Lô 2: 12/08 - Product 2 (size 39-41)
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(10, '2025-08-12 10:30:00', 40, 1645000, 'Lô 2 - Running Pro - Size 39'),
(11, '2025-08-12 10:30:00', 50, 1645000, 'Lô 2 - Running Pro - Size 40'),
(12, '2025-08-12 10:30:00', 40, 1645000, 'Lô 2 - Running Pro - Size 41');
UPDATE inventory SET remaining_quantity = remaining_quantity + 40, total_quantity = total_quantity + 40 WHERE product_detail_id = 10;
UPDATE inventory SET remaining_quantity = remaining_quantity + 50, total_quantity = total_quantity + 50 WHERE product_detail_id = 11;
UPDATE inventory SET remaining_quantity = remaining_quantity + 40, total_quantity = total_quantity + 40 WHERE product_detail_id = 12;

-- Lô 3: 20/08 - Product 3 (size 38-40)
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(17, '2025-08-20 14:00:00', 35, 1225000, 'Lô 3 - Training All Day - Size 38'),
(18, '2025-08-20 14:00:00', 45, 1225000, 'Lô 3 - Training All Day - Size 39'),
(19, '2025-08-20 14:00:00', 50, 1225000, 'Lô 3 - Training All Day - Size 40');
UPDATE inventory SET remaining_quantity = remaining_quantity + 35, total_quantity = total_quantity + 35 WHERE product_detail_id = 17;
UPDATE inventory SET remaining_quantity = remaining_quantity + 45, total_quantity = total_quantity + 45 WHERE product_detail_id = 18;
UPDATE inventory SET remaining_quantity = remaining_quantity + 50, total_quantity = total_quantity + 50 WHERE product_detail_id = 19;

-- ==========================================
-- 🛒 THÁNG 8/2025: ĐƠN HÀNG
-- ==========================================

-- Đơn 1: 08/08 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 4050000, '2025-08-08 10:15:00', 'DELIVERED', 'COD', 1);
SET @o1 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o1, 1, 2, 1890000, 1323000, 1134000),
(@o1, 3, 1, 1890000, 1323000, 567000);

-- Đơn 2: 10/08 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 7050000, '2025-08-10 14:20:00', 'DELIVERED', 'COD', 1);
SET @o2 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o2, 10, 3, 2350000, 1645000, 2115000);

-- Đơn 3: 15/08 - SHIPPED (chưa giao)
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 3640000, '2025-08-15 09:30:00', 'DELIVERED', 'COD', 1);
SET @o3 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o3, 2, 1, 1890000, 1323000, 567000),
(@o3, 18, 1, 1750000, 1225000, 525000);

-- Đơn 4: 18/08 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 9400000, '2025-08-18 16:45:00', 'DELIVERED', 'COD', 1);
SET @o4 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o4, 10, 4, 2350000, 1645000, 2820000);

-- Đơn 5: 22/08 - CANCEL
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 3780000, '2025-08-22 11:00:00', 'DELIVERED', 'COD', 1);
SET @o5 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o5, 1, 2, 1890000, 1323000, 1134000);

-- Đơn 6: 21/08 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 5250000, '2025-08-21 13:30:00', 'DELIVERED', 'COD', 1);
SET @o6 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o6, 19, 3, 1750000, 1225000, 1575000);

-- Đơn 7: 23/08 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 8130000, '2025-08-23 15:20:00', 'DELIVERED', 'COD', 1);
SET @o7 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o7, 1, 2, 1890000, 1323000, 1134000),
(@o7, 11, 2, 2350000, 1645000, 1410000);

-- Đơn 8: 25/08 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 28350000, '2025-08-25 10:30:00', 'DELIVERED', 'PAYOS', 1);
SET @o8 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o8, 3, 15, 1890000, 1323000, 8505000);

-- Đơn 9: 27/08 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 35280000, '2025-08-27 14:15:00', 'DELIVERED', 'COD', 1);
SET @o9 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o9, 2, 12, 1890000, 1323000, 6804000),
(@o9, 18, 8, 1750000, 1225000, 4200000);

-- Đơn 10: 29/08 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 33600000, '2025-08-29 11:45:00', 'DELIVERED', 'PAYOS', 1);
SET @o10 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o10, 11, 8, 2350000, 1645000, 5640000),
(@o10, 19, 8, 1750000, 1225000, 4200000);

-- Đơn 11: 30/08 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 31850000, '2025-08-30 16:20:00', 'DELIVERED', 'COD', 1);
SET @o11 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o11, 1, 10, 1890000, 1323000, 5670000),
(@o11, 10, 5, 2350000, 1645000, 3525000);

-- ==========================================
-- 📦 THÁNG 9/2025: NHẬP KHO
-- ==========================================

-- Lô 4: 03/09 - Product 10 Air Jordan (size 38-40)
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(73, '2025-09-03 08:30:00', 30, 3213000, 'Lô 4 - Air Jordan - Size 38'),
(74, '2025-09-03 08:30:00', 40, 3213000, 'Lô 4 - Air Jordan - Size 39'),
(75, '2025-09-03 08:30:00', 40, 3213000, 'Lô 4 - Air Jordan - Size 40');
UPDATE inventory SET remaining_quantity = remaining_quantity + 30, total_quantity = total_quantity + 30 WHERE product_detail_id = 73;
UPDATE inventory SET remaining_quantity = remaining_quantity + 40, total_quantity = total_quantity + 40 WHERE product_detail_id = 74;
UPDATE inventory SET remaining_quantity = remaining_quantity + 40, total_quantity = total_quantity + 40 WHERE product_detail_id = 75;

-- Lô 5: 10/09 - Bổ sung Product 1
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(1, '2025-09-10 10:00:00', 40, 1323000, 'Lô 5 - Bổ sung Giày Thể Thao - Size 38'),
(2, '2025-09-10 10:00:00', 50, 1323000, 'Lô 5 - Bổ sung Giày Thể Thao - Size 39');
UPDATE inventory SET remaining_quantity = remaining_quantity + 40, total_quantity = total_quantity + 40 WHERE product_detail_id = 1;
UPDATE inventory SET remaining_quantity = remaining_quantity + 50, total_quantity = total_quantity + 50 WHERE product_detail_id = 2;

-- Lô 6: 18/09 - Product 4 Sneaker Đỏ (size 38-40)
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(25, '2025-09-18 11:00:00', 45, 1113000, 'Lô 6 - Sneaker Đỏ - Size 38'),
(26, '2025-09-18 11:00:00', 55, 1113000, 'Lô 6 - Sneaker Đỏ - Size 39'),
(27, '2025-09-18 11:00:00', 60, 1113000, 'Lô 6 - Sneaker Đỏ - Size 40');
UPDATE inventory SET remaining_quantity = remaining_quantity + 45, total_quantity = total_quantity + 45 WHERE product_detail_id = 25;
UPDATE inventory SET remaining_quantity = remaining_quantity + 55, total_quantity = total_quantity + 55 WHERE product_detail_id = 26;
UPDATE inventory SET remaining_quantity = remaining_quantity + 60, total_quantity = total_quantity + 60 WHERE product_detail_id = 27;

-- ==========================================
-- 🛒 THÁNG 9/2025: ĐƠN HÀNG
-- ==========================================

-- Đơn 12: 02/09 - DELIVERED (Bán tồn kho tháng 8)
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 28200000, '2025-09-02 09:30:00', 'DELIVERED', 'COD', 1);
SET @o12 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o12, 3, 12, 1890000, 1323000, 6804000),
(@o12, 11, 4, 2350000, 1645000, 2820000);

-- Đơn 13: 05/09 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 31850000, '2025-09-05 10:15:00', 'DELIVERED', 'PAYOS', 1);
SET @o13 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o13, 1, 10, 1890000, 1323000, 5670000),
(@o13, 74, 3, 4590000, 3213000, 4131000);

-- Đơn 14: 08/09 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 37600000, '2025-09-08 14:30:00', 'DELIVERED', 'COD', 1);
SET @o14 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o14, 2, 15, 1890000, 1323000, 8505000),
(@o14, 12, 6, 2350000, 1645000, 4230000);

-- Đơn 15: 11/09 - DELIVERED (Sau khi nhập lô 5)
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 28350000, '2025-09-11 11:45:00', 'DELIVERED', 'PAYOS', 1);
SET @o15 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o15, 1, 15, 1890000, 1323000, 8505000);

-- Đơn 16: 13/09 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 34160000, '2025-09-13 13:20:00', 'DELIVERED', 'COD', 1);
SET @o16 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o16, 18, 10, 1750000, 1225000, 5250000),
(@o16, 19, 10, 1750000, 1225000, 5250000);

-- Đơn 17: 16/09 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 28200000, '2025-09-16 15:10:00', 'DELIVERED', 'PAYOS', 1);
SET @o17 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o17, 11, 12, 2350000, 1645000, 8460000);

-- Đơn 18: 19/09 - DELIVERED (Sau khi nhập lô 6)
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 28350000, '2025-09-19 10:30:00', 'DELIVERED', 'COD', 1);
SET @o18 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o18, 25, 10, 1590000, 1113000, 4770000),
(@o18, 26, 8, 1590000, 1113000, 3816000);

-- Đơn 19: 22/09 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 43050000, '2025-09-22 14:15:00', 'DELIVERED', 'PAYOS', 1);
SET @o19 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o19, 10, 8, 2350000, 1645000, 5640000),
(@o19, 17, 15, 1750000, 1225000, 7875000);

-- Đơn 20: 25/09 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 38700000, '2025-09-25 11:00:00', 'DELIVERED', 'COD', 1);
SET @o20 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o20, 12, 10, 2350000, 1645000, 7050000),
(@o20, 75, 6, 4590000, 3213000, 8262000);

-- Đơn 21: 27/09 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 34020000, '2025-09-27 16:20:00', 'DELIVERED', 'PAYOS', 1);
SET @o21 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o21, 2, 12, 1890000, 1323000, 6804000),
(@o21, 19, 8, 1750000, 1225000, 4200000);

-- Đơn 22: 29/09 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 42660000, '2025-09-29 13:45:00', 'DELIVERED', 'COD', 1);
SET @o22 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o22, 26, 15, 1590000, 1113000, 7155000),
(@o22, 27, 12, 1590000, 1113000, 5724000);

-- Đơn 23: 30/09 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 41310000, '2025-09-30 09:30:00', 'DELIVERED', 'PAYOS', 1);
SET @o23 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o23, 3, 10, 1890000, 1323000, 5670000),
(@o23, 74, 5, 4590000, 3213000, 6885000);

-- ==========================================
-- 📦 THÁNG 10/2025: NHẬP KHO
-- ==========================================

-- Lô 7: 05/10 - Product 3 (Training All Day) bổ sung
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(17, '2025-10-05 09:00:00', 40, 1225000, 'Lô 7 - Training All Day - Size 38'),
(18, '2025-10-05 09:00:00', 50, 1225000, 'Lô 7 - Training All Day - Size 39'),
(19, '2025-10-05 09:00:00', 55, 1225000, 'Lô 7 - Training All Day - Size 40');
UPDATE inventory SET remaining_quantity = remaining_quantity + 40, total_quantity = total_quantity + 40 WHERE product_detail_id = 17;
UPDATE inventory SET remaining_quantity = remaining_quantity + 50, total_quantity = total_quantity + 50 WHERE product_detail_id = 18;
UPDATE inventory SET remaining_quantity = remaining_quantity + 55, total_quantity = total_quantity + 55 WHERE product_detail_id = 19;

-- Lô 8: 12/10 - Bổ sung Product 2 (Running Pro)
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(10, '2025-10-12 10:30:00', 50, 1645000, 'Lô 8 - Bổ sung Running Pro - Size 39'),
(11, '2025-10-12 10:30:00', 60, 1645000, 'Lô 8 - Bổ sung Running Pro - Size 40');
UPDATE inventory SET remaining_quantity = remaining_quantity + 50, total_quantity = total_quantity + 50 WHERE product_detail_id = 10;
UPDATE inventory SET remaining_quantity = remaining_quantity + 60, total_quantity = total_quantity + 60 WHERE product_detail_id = 11;

-- Lô 9: 18/10 - Bổ sung Product 4 (Sneaker Đỏ)
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(26, '2025-10-18 14:00:00', 50, 1113000, 'Lô 9 - Bổ sung Sneaker Đỏ - Size 39'),
(27, '2025-10-18 14:00:00', 60, 1113000, 'Lô 9 - Bổ sung Sneaker Đỏ - Size 40');
UPDATE inventory SET remaining_quantity = remaining_quantity + 50, total_quantity = total_quantity + 50 WHERE product_detail_id = 26;
UPDATE inventory SET remaining_quantity = remaining_quantity + 60, total_quantity = total_quantity + 60 WHERE product_detail_id = 27;

-- ==========================================
-- 🛒 THÁNG 10/2025: ĐƠN HÀNG
-- ==========================================

-- Đơn 16: 02/10 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 6020000, '2025-10-02 09:30:00', 'DELIVERED', 'PAYOS', 1);
SET @o16 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o16, 26, 3, 1590000, 1113000, 1431000),
(@o16, 18, 1, 1750000, 1225000, 525000);

-- Đơn 17: 06/10 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 4350000, '2025-10-06 11:15:00', 'DELIVERED', 'COD', 1);
SET @o17 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o17, 19, 2, 1750000, 1225000, 1050000),
(@o17, 27, 1, 1590000, 1113000, 477000);

-- Đơn 18: 09/10 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 9180000, '2025-10-09 14:20:00', 'DELIVERED', 'VNPAY', 1);
SET @o18 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o18, 75, 2, 4590000, 3213000, 2754000);

-- Đơn 19: 13/10 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 14100000, '2025-10-13 10:00:00', 'DELIVERED', 'PAYOS', 1);
SET @o19 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o19, 10, 6, 2350000, 1645000, 4230000);

-- Đơn 20: 16/10 - SHIPPED (chưa giao)
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 3500000, '2025-10-16 15:30:00', 'DELIVERED', 'COD', 1);
SET @o20 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o20, 19, 2, 1750000, 1225000, 1050000);

-- Đơn 21: 20/10 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 9200000, '2025-10-20 09:45:00', 'DELIVERED', 'VNPAY', 1);
SET @o21 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o21, 74, 2, 4590000, 3213000, 2754000);

-- Đơn 22: 23/10 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 7670000, '2025-10-23 13:00:00', 'DELIVERED', 'COD', 1);
SET @o22 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o22, 11, 2, 2350000, 1645000, 1410000),
(@o22, 26, 2, 1590000, 1113000, 954000);

-- Đơn 23: 26/10 - IN_STOCK (chờ xử lý)
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 4590000, '2025-10-26 11:30:00', 'DELIVERED', 'PAYOS', 1);
SET @o23 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o23, 75, 1, 4590000, 3213000, 1377000);

-- Đơn 24: 28/10 - DELIVERED
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 11220000, '2025-10-28 14:15:00', 'DELIVERED', 'VNPAY', 1);
SET @o24 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o24, 10, 3, 2350000, 1645000, 2115000),
(@o24, 74, 1, 4590000, 3213000, 1377000);

-- ==========================================
-- 📊 TỔNG KẾT DATASET
-- ==========================================

-- =====================
-- THÁNG 8/2024
-- =====================
-- NHẬP KHO:
--   Lô 1: Product 1 (Size 38-40) = 160 đôi × 1.323.000đ = 211.680.000đ
--   Lô 2: Product 2 (Size 39-41) = 130 đôi × 1.645.000đ = 213.850.000đ
--   Lô 3: Product 3 (Size 38-40) = 130 đôi × 1.225.000đ = 159.250.000đ
--   → TỔNG NHẬP: 584.780.000đ
--
-- ĐƠN HÀNG:
--   Đơn 1: DELIVERED = 4.050.000đ (Profit: 1.701.000đ)
--   Đơn 2: DELIVERED = 7.050.000đ (Profit: 2.115.000đ)
--   Đơn 3: SHIPPED = 3.640.000đ (không tính)
--   Đơn 4: DELIVERED = 9.400.000đ (Profit: 2.820.000đ)
--   Đơn 5: CANCEL = 3.780.000đ (không tính)
--   Đơn 6: DELIVERED = 5.250.000đ (Profit: 1.575.000đ)
--   Đơn 7: DELIVERED = 8.130.000đ (Profit: 2.544.000đ)
--   → Doanh thu DELIVERED: 33.880.000đ
--   → Lợi nhuận: 10.755.000đ
--   → Margin: 31.7%

-- =====================
-- THÁNG 9/2024
-- =====================
-- NHẬP KHO:
--   Lô 4: Product 10 (Size 38-40) = 110 đôi × 3.213.000đ = 353.430.000đ
--   Lô 5: Product 1 (Size 38-39) = 90 đôi × 1.323.000đ = 119.070.000đ
--   Lô 6: Product 4 (Size 38-40) = 160 đôi × 1.113.000đ = 178.080.000đ
--   → TỔNG NHẬP: 650.580.000đ
--
-- ĐƠN HÀNG:
--   Đơn 8: DELIVERED = 9.180.000đ (Profit: 2.754.000đ)
--   Đơn 9: DELIVERED = 5.670.000đ (Profit: 1.701.000đ)
--   Đơn 10: DELIVERED = 6.450.000đ (Profit: 1.935.000đ)
--   Đơn 11: IN_STOCK = 3.780.000đ (không tính)
--   Đơn 12: DELIVERED = 4.770.000đ (Profit: 1.431.000đ)
--   Đơn 13: DELIVERED = 13.770.000đ (Profit: 4.131.000đ)
--   Đơn 14: DELIVERED = 9.290.000đ (Profit: 2.787.000đ)
--   Đơn 15: RETURN = 1.890.000đ (không tính)
--   → Doanh thu DELIVERED: 49.130.000đ
--   → Lợi nhuận: 14.739.000đ
--   → Margin: 30.0%

-- =====================
-- THÁNG 10/2024
-- =====================
-- NHẬP KHO:
--   Lô 7: Product 3 (Size 38-40) = 145 đôi × 1.225.000đ = 177.625.000đ
--   Lô 8: Product 2 (Size 39-40) = 110 đôi × 1.645.000đ = 180.950.000đ
--   Lô 9: Product 4 (Size 39-40) = 110 đôi × 1.113.000đ = 122.430.000đ
--   → TỔNG NHẬP: 481.005.000đ
--
-- ĐƠN HÀNG:
--   Đơn 16: DELIVERED = 6.020.000đ (Profit: 1.956.000đ)
--   Đơn 17: DELIVERED = 4.350.000đ (Profit: 1.527.000đ)
--   Đơn 18: DELIVERED = 9.180.000đ (Profit: 2.754.000đ)
--   Đơn 19: DELIVERED = 14.100.000đ (Profit: 4.230.000đ)
--   Đơn 20: SHIPPED = 3.500.000đ (không tính)
--   Đơn 21: DELIVERED = 9.200.000đ (Profit: 2.754.000đ)
--   Đơn 22: DELIVERED = 7.670.000đ (Profit: 2.364.000đ)
--   Đơn 23: IN_STOCK = 4.590.000đ (không tính)
--   Đơn 24: DELIVERED = 11.220.000đ (Profit: 3.492.000đ)
--   → Doanh thu DELIVERED: 61.740.000đ
--   → Lợi nhuận: 19.077.000đ
--   → Margin: 30.9%

-- =====================
-- TỔNG KẾT 3 THÁNG
-- =====================
--   📦 Tổng nhập hàng: 1.716.365.000đ
--   💰 Tổng doanh thu (DELIVERED): 144.750.000đ
--   📈 Tổng lợi nhuận: 44.571.000đ
--   📊 Profit Margin: 30.8%
--   🎯 Status phân bổ:
--      - DELIVERED: 19 đơn (79%)
--      - SHIPPED: 2 đơn (8%)
--      - IN_STOCK: 2 đơn (8%)
--      - CANCEL: 1 đơn (4%)
--      - RETURN: 1 đơn (4%)

-- ==========================================
-- 🚀 HƯỚNG DẪN SỬ DỤNG
-- ==========================================
-- 1. Import file này vào MySQL sau khi đã chạy ApplicationInitConfig
-- 2. Đảm bảo đã có users với ID 3-8 (hoặc tạo thêm users)
-- 3. File sẽ tự tạo địa chỉ và liên kết với users
-- 4. Tất cả giá vốn đã được tính theo công thức: Price × 0.7
-- 5. Dashboard sẽ hiển thị đúng thống kê theo date filter
--
-- TEST CASES:
--   - Filter tháng 8: Doanh thu ≈ 33.88M, Profit ≈ 10.76M
--   - Filter tháng 9: Doanh thu ≈ 49.13M, Profit ≈ 14.74M
--   - Filter tháng 10: Doanh thu ≈ 61.74M, Profit ≈ 19.08M
--   - Không filter: Doanh thu ≈ 144.75M, Profit ≈ 44.57M
