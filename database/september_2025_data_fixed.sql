-- ==========================================
-- 📊 DỮ LIỆU THỐNG KÊ THÁNG 9/2025 (FIXED VERSION)
-- ==========================================
-- User IDs: 3-18 (16 customers)
-- Product IDs: 1-20
-- ProductDetail IDs: 1-160
-- Cost Price = Product Base Price × 0.7
-- ==========================================

SET foreign_key_checks = 0;

-- ==========================================
-- 📦 TẠO INVENTORY CHO 20 SẢN PHẨM
-- ==========================================
INSERT INTO inventory (product_detail_id, remaining_quantity, total_quantity) 
SELECT pd.id, 0, 0 FROM product_detail pd WHERE pd.id BETWEEN 1 AND 160
ON DUPLICATE KEY UPDATE product_detail_id = product_detail_id;

-- ==========================================
-- 📦 NHẬP KHO THÁNG 9/2025
-- ==========================================

-- Lô 1: 01/09 - Products 1-5
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(1,'2025-09-01 08:00:00',80,1323000,'Sep-Lô1-P1-S38'),
(2,'2025-09-01 08:00:00',90,1323000,'Sep-Lô1-P1-S39'),
(3,'2025-09-01 08:00:00',100,1323000,'Sep-Lô1-P1-S40'),
(4,'2025-09-01 08:00:00',90,1373000,'Sep-Lô1-P1-S41'),
(5,'2025-09-01 08:00:00',80,1373000,'Sep-Lô1-P1-S42');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(9,'2025-09-01 08:30:00',70,1645000,'Sep-Lô1-P2-S39'),
(10,'2025-09-01 08:30:00',80,1645000,'Sep-Lô1-P2-S40'),
(11,'2025-09-01 08:30:00',85,1695000,'Sep-Lô1-P2-S41'),
(12,'2025-09-01 08:30:00',75,1695000,'Sep-Lô1-P2-S42');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(17,'2025-09-01 09:00:00',90,1225000,'Sep-Lô1-P3-S38'),
(18,'2025-09-01 09:00:00',100,1225000,'Sep-Lô1-P3-S39'),
(19,'2025-09-01 09:00:00',95,1225000,'Sep-Lô1-P3-S40'),
(20,'2025-09-01 09:00:00',85,1275000,'Sep-Lô1-P3-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(25,'2025-09-01 09:30:00',85,1113000,'Sep-Lô1-P4-S38'),
(26,'2025-09-01 09:30:00',95,1113000,'Sep-Lô1-P4-S39'),
(27,'2025-09-01 09:30:00',100,1113000,'Sep-Lô1-P4-S40'),
(28,'2025-09-01 09:30:00',90,1163000,'Sep-Lô1-P4-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(33,'2025-09-01 10:00:00',75,1015000,'Sep-Lô1-P5-S38'),
(34,'2025-09-01 10:00:00',80,1015000,'Sep-Lô1-P5-S39'),
(35,'2025-09-01 10:00:00',85,1015000,'Sep-Lô1-P5-S40'),
(36,'2025-09-01 10:00:00',75,1065000,'Sep-Lô1-P5-S41');

-- Lô 2: 05/09 - Products 6-10
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(41,'2025-09-05 08:30:00',70,1183000,'Sep-Lô2-P6-S38'),
(42,'2025-09-05 08:30:00',75,1183000,'Sep-Lô2-P6-S39'),
(43,'2025-09-05 08:30:00',80,1183000,'Sep-Lô2-P6-S40'),
(44,'2025-09-05 08:30:00',70,1233000,'Sep-Lô2-P6-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(49,'2025-09-05 09:00:00',60,2023000,'Sep-Lô2-P7-S38'),
(50,'2025-09-05 09:00:00',65,2023000,'Sep-Lô2-P7-S39'),
(51,'2025-09-05 09:00:00',70,2023000,'Sep-Lô2-P7-S40'),
(52,'2025-09-05 09:00:00',65,2073000,'Sep-Lô2-P7-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(57,'2025-09-05 09:30:00',50,2275000,'Sep-Lô2-P8-S38'),
(58,'2025-09-05 09:30:00',55,2275000,'Sep-Lô2-P8-S39'),
(59,'2025-09-05 09:30:00',60,2275000,'Sep-Lô2-P8-S40'),
(60,'2025-09-05 09:30:00',50,2325000,'Sep-Lô2-P8-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(65,'2025-09-05 10:00:00',55,2205000,'Sep-Lô2-P9-S38'),
(66,'2025-09-05 10:00:00',60,2205000,'Sep-Lô2-P9-S39'),
(67,'2025-09-05 10:00:00',65,2205000,'Sep-Lô2-P9-S40'),
(68,'2025-09-05 10:00:00',55,2255000,'Sep-Lô2-P9-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(73,'2025-09-05 10:30:00',40,3213000,'Sep-Lô2-P10-S38'),
(74,'2025-09-05 10:30:00',45,3213000,'Sep-Lô2-P10-S39'),
(75,'2025-09-05 10:30:00',50,3213000,'Sep-Lô2-P10-S40'),
(76,'2025-09-05 10:30:00',45,3263000,'Sep-Lô2-P10-S41');

-- Lô 3: 10/09 - Products 11-15
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(81,'2025-09-10 08:00:00',45,2905000,'Sep-Lô3-P11-S38'),
(82,'2025-09-10 08:00:00',50,2905000,'Sep-Lô3-P11-S39'),
(83,'2025-09-10 08:00:00',55,2905000,'Sep-Lô3-P11-S40'),
(84,'2025-09-10 08:00:00',50,2955000,'Sep-Lô3-P11-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(89,'2025-09-10 08:30:00',50,2653000,'Sep-Lô3-P12-S38'),
(90,'2025-09-10 08:30:00',55,2653000,'Sep-Lô3-P12-S39'),
(91,'2025-09-10 08:30:00',60,2653000,'Sep-Lô3-P12-S40'),
(92,'2025-09-10 08:30:00',50,2703000,'Sep-Lô3-P12-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(97,'2025-09-10 09:00:00',100,413000,'Sep-Lô3-P13-S38'),
(98,'2025-09-10 09:00:00',110,413000,'Sep-Lô3-P13-S39'),
(99,'2025-09-10 09:00:00',120,413000,'Sep-Lô3-P13-S40'),
(100,'2025-09-10 09:00:00',100,413000,'Sep-Lô3-P13-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(105,'2025-09-10 09:30:00',95,525000,'Sep-Lô3-P14-S38'),
(106,'2025-09-10 09:30:00',100,525000,'Sep-Lô3-P14-S39'),
(107,'2025-09-10 09:30:00',110,525000,'Sep-Lô3-P14-S40'),
(108,'2025-09-10 09:30:00',95,525000,'Sep-Lô3-P14-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(113,'2025-09-10 10:00:00',80,805000,'Sep-Lô3-P15-S38'),
(114,'2025-09-10 10:00:00',85,805000,'Sep-Lô3-P15-S39'),
(115,'2025-09-10 10:00:00',90,805000,'Sep-Lô3-P15-S40'),
(116,'2025-09-10 10:00:00',80,855000,'Sep-Lô3-P15-S41');

-- Lô 4: 15/09 - Products 16-20
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(121,'2025-09-15 08:00:00',65,623000,'Sep-Lô4-P16-S38'),
(122,'2025-09-15 08:00:00',70,623000,'Sep-Lô4-P16-S39'),
(123,'2025-09-15 08:00:00',75,623000,'Sep-Lô4-P16-S40'),
(124,'2025-09-15 08:00:00',65,623000,'Sep-Lô4-P16-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(129,'2025-09-15 08:30:00',55,903000,'Sep-Lô4-P17-S38'),
(130,'2025-09-15 08:30:00',60,903000,'Sep-Lô4-P17-S39'),
(131,'2025-09-15 08:30:00',65,903000,'Sep-Lô4-P17-S40'),
(132,'2025-09-15 08:30:00',55,903000,'Sep-Lô4-P17-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(137,'2025-09-15 09:00:00',60,665000,'Sep-Lô4-P18-S38'),
(138,'2025-09-15 09:00:00',65,665000,'Sep-Lô4-P18-S39'),
(139,'2025-09-15 09:00:00',70,665000,'Sep-Lô4-P18-S40'),
(140,'2025-09-15 09:00:00',60,665000,'Sep-Lô4-P18-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(145,'2025-09-15 09:30:00',65,805000,'Sep-Lô4-P19-S38'),
(146,'2025-09-15 09:30:00',70,805000,'Sep-Lô4-P19-S39'),
(147,'2025-09-15 09:30:00',75,805000,'Sep-Lô4-P19-S40'),
(148,'2025-09-15 09:30:00',65,855000,'Sep-Lô4-P19-S41');

INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(153,'2025-09-15 10:00:00',70,693000,'Sep-Lô4-P20-S38'),
(154,'2025-09-15 10:00:00',75,693000,'Sep-Lô4-P20-S39'),
(155,'2025-09-15 10:00:00',80,693000,'Sep-Lô4-P20-S40'),
(156,'2025-09-15 10:00:00',70,693000,'Sep-Lô4-P20-S41');

-- Lô 5: 20/09 - Bổ sung sản phẩm bán chạy
INSERT INTO inventory_history (product_detail_id, import_date, quantity, cost_price, note) VALUES
(1,'2025-09-20 09:00:00',50,1323000,'Sep-Lô5-P1-S38'),
(2,'2025-09-20 09:00:00',60,1323000,'Sep-Lô5-P1-S39'),
(3,'2025-09-20 09:00:00',65,1323000,'Sep-Lô5-P1-S40'),
(10,'2025-09-20 09:30:00',45,1645000,'Sep-Lô5-P2-S40'),
(11,'2025-09-20 09:30:00',50,1695000,'Sep-Lô5-P2-S41'),
(26,'2025-09-20 10:00:00',55,1113000,'Sep-Lô5-P4-S39'),
(27,'2025-09-20 10:00:00',60,1113000,'Sep-Lô5-P4-S40'),
(74,'2025-09-20 10:30:00',30,3213000,'Sep-Lô5-P10-S39'),
(75,'2025-09-20 10:30:00',35,3213000,'Sep-Lô5-P10-S40');

-- Update inventory từ inventory_history
UPDATE inventory i 
JOIN (
    SELECT product_detail_id, SUM(quantity) as total 
    FROM inventory_history 
    WHERE import_date LIKE '2025-09%' 
    GROUP BY product_detail_id
) h ON i.product_detail_id = h.product_detail_id 
SET i.remaining_quantity = i.remaining_quantity + h.total, 
    i.total_quantity = i.total_quantity + h.total;

-- ==========================================
-- 🛒 ĐƠN HÀNG THÁNG 9/2025 (AUTO-GENERATED)
-- 120 đơn rải đều 30 ngày
-- ==========================================

-- Đơn 1: 01/09 - User 15
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(15, 13770000, '2025-09-01 17:32:00', 'DELIVERED', 'PAYOS', 1);
SET @o1 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o1, 73, 3, 4590000, 3213000, 4131000);

-- Đơn 2: 01/09 - User 7
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 7250000, '2025-09-01 20:18:00', 'DELIVERED', 'PAYOS', 1);
SET @o2 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o2, 109, 2, 750000, 525000, 450000),
(@o2, 114, 5, 1150000, 805000, 1725000);

-- Đơn 3: 02/09 - User 7
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 2580000, '2025-09-02 20:46:00', 'DELIVERED', 'COD', 1);
SET @o3 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o3, 130, 2, 1290000, 903000, 774000);

-- Đơn 4: 02/09 - User 6
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 15200000, '2025-09-02 11:15:00', 'DELIVERED', 'PAYOS', 1);
SET @o4 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o4, 21, 5, 1750000, 1225000, 2625000),
(@o4, 129, 5, 1290000, 903000, 1935000);

-- Đơn 5: 02/09 - User 9
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(9, 7580000, '2025-09-02 14:01:00', 'DELIVERED', 'COD', 1);
SET @o5 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o5, 90, 2, 3790000, 2653000, 2274000);

-- Đơn 6: 02/09 - User 9
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(9, 21450000, '2025-09-02 09:09:00', 'DELIVERED', 'COD', 1);
SET @o6 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o6, 18, 4, 1750000, 1225000, 2100000),
(@o6, 51, 5, 2890000, 2023000, 4335000);

-- Đơn 7: 03/09 - User 12
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(12, 17040000, '2025-09-03 18:40:00', 'DELIVERED', 'COD', 1);
SET @o7 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o7, 108, 2, 750000, 525000, 450000),
(@o7, 74, 3, 4590000, 3213000, 4131000),
(@o7, 101, 3, 590000, 413000, 531000);

-- Đơn 8: 03/09 - User 17
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(17, 24110000, '2025-09-03 16:19:00', 'DELIVERED', 'COD', 1);
SET @o8 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o8, 75, 4, 4590000, 3213000, 5508000),
(@o8, 147, 5, 1150000, 805000, 1725000);

-- Đơn 9: 03/09 - User 6
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 2970000, '2025-09-03 17:23:00', 'DELIVERED', 'PAYOS', 1);
SET @o9 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o9, 156, 3, 990000, 693000, 891000);

-- Đơn 10: 03/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 27090000, '2025-09-03 12:44:00', 'DELIVERED', 'COD', 1);
SET @o10 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o10, 53, 1, 2890000, 2023000, 867000),
(@o10, 66, 5, 3150000, 2205000, 4725000),
(@o10, 42, 5, 1690000, 1183000, 2535000);

-- Đơn 11: 03/09 - User 6
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 18200000, '2025-09-03 09:51:00', 'DELIVERED', 'PAYOS', 1);
SET @o11 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o11, 147, 5, 1150000, 805000, 1725000),
(@o11, 85, 3, 4150000, 2905000, 3735000);

-- Đơn 12: 03/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 5750000, '2025-09-03 08:46:00', 'DELIVERED', 'COD', 1);
SET @o12 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o12, 116, 5, 1150000, 805000, 1725000);

-- Đơn 13: 03/09 - User 12
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(12, 4170000, '2025-09-03 10:38:00', 'DELIVERED', 'COD', 1);
SET @o13 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o13, 25, 2, 1590000, 1113000, 954000),
(@o13, 157, 1, 990000, 693000, 297000);

-- Đơn 14: 04/09 - User 14
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(14, 19250000, '2025-09-04 15:42:00', 'DELIVERED', 'PAYOS', 1);
SET @o14 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o14, 73, 4, 4590000, 3213000, 5508000),
(@o14, 123, 1, 890000, 623000, 267000);

-- Đơn 15: 04/09 - User 8
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 17050000, '2025-09-04 08:08:00', 'DELIVERED', 'COD', 1);
SET @o15 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o15, 81, 3, 4150000, 2905000, 3735000),
(@o15, 114, 4, 1150000, 805000, 1380000);

-- Đơn 16: 04/09 - User 8
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 22190000, '2025-09-04 11:15:00', 'DELIVERED', 'COD', 1);
SET @o16 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o16, 74, 3, 4590000, 3213000, 4131000),
(@o16, 113, 5, 1150000, 805000, 1725000),
(@o16, 125, 3, 890000, 623000, 801000);

-- Đơn 17: 05/09 - User 6
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 1150000, '2025-09-05 16:45:00', 'DELIVERED', 'COD', 1);
SET @o17 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o17, 114, 1, 1150000, 805000, 345000);

-- Đơn 18: 05/09 - User 18
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(18, 11640000, '2025-09-05 13:58:00', 'DELIVERED', 'PAYOS', 1);
SET @o18 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o18, 50, 1, 2890000, 2023000, 867000),
(@o18, 21, 5, 1750000, 1225000, 2625000);

-- Đơn 19: 05/09 - User 15
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(15, 10260000, '2025-09-05 18:46:00', 'DELIVERED', 'COD', 1);
SET @o19 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o19, 156, 4, 990000, 693000, 1188000),
(@o19, 69, 2, 3150000, 2205000, 1890000);

-- Đơn 20: 06/09 - User 7
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 23620000, '2025-09-06 12:02:00', 'DELIVERED', 'COD', 1);
SET @o20 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o20, 4, 3, 1890000, 1323000, 1701000),
(@o20, 19, 2, 1750000, 1225000, 1050000),
(@o20, 52, 5, 2890000, 2023000, 4335000);

-- Đơn 21: 06/09 - User 9
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(9, 5800000, '2025-09-06 08:19:00', 'DELIVERED', 'COD', 1);
SET @o21 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o21, 37, 4, 1450000, 1015000, 1740000);

-- Đơn 22: 07/09 - User 3
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 26210000, '2025-09-07 18:31:00', 'DELIVERED', 'COD', 1);
SET @o22 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o22, 75, 4, 4590000, 3213000, 5508000),
(@o22, 9, 2, 2350000, 1645000, 1410000),
(@o22, 65, 1, 3150000, 2205000, 945000);

-- Đơn 23: 07/09 - User 18
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(18, 8220000, '2025-09-07 11:06:00', 'DELIVERED', 'COD', 1);
SET @o23 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o23, 27, 3, 1590000, 1113000, 1431000),
(@o23, 147, 3, 1150000, 805000, 1035000);

-- Đơn 24: 07/09 - User 8
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 8300000, '2025-09-07 08:28:00', 'DELIVERED', 'COD', 1);
SET @o24 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o24, 130, 3, 1290000, 903000, 1161000),
(@o24, 60, 1, 3250000, 2275000, 975000),
(@o24, 101, 2, 590000, 413000, 354000);

-- Đơn 25: 07/09 - User 10
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(10, 890000, '2025-09-07 15:46:00', 'DELIVERED', 'PAYOS', 1);
SET @o25 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o25, 123, 1, 890000, 623000, 267000);

-- Đơn 26: 07/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 19520000, '2025-09-07 17:23:00', 'DELIVERED', 'PAYOS', 1);
SET @o26 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o26, 74, 3, 4590000, 3213000, 4131000),
(@o26, 115, 5, 1150000, 805000, 1725000);

-- Đơn 27: 08/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 11560000, '2025-09-08 18:03:00', 'DELIVERED', 'PAYOS', 1);
SET @o27 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o27, 52, 4, 2890000, 2023000, 3468000);

-- Đơn 28: 08/09 - User 17
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(17, 18860000, '2025-09-08 09:52:00', 'DELIVERED', 'PAYOS', 1);
SET @o28 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o28, 125, 4, 890000, 623000, 1068000),
(@o28, 57, 4, 3250000, 2275000, 3900000),
(@o28, 113, 2, 1150000, 805000, 690000);

-- Đơn 29: 08/09 - User 7
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 4080000, '2025-09-08 12:46:00', 'DELIVERED', 'COD', 1);
SET @o29 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o29, 132, 2, 1290000, 903000, 774000),
(@o29, 109, 2, 750000, 525000, 450000);

-- Đơn 30: 09/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 8450000, '2025-09-09 16:28:00', 'DELIVERED', 'PAYOS', 1);
SET @o30 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o30, 45, 5, 1690000, 1183000, 2535000);

-- Đơn 31: 09/09 - User 12
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(12, 6400000, '2025-09-09 10:55:00', 'DELIVERED', 'PAYOS', 1);
SET @o31 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o31, 68, 1, 3150000, 2205000, 945000),
(@o31, 57, 1, 3250000, 2275000, 975000);

-- Đơn 32: 10/09 - User 6
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 13120000, '2025-09-10 18:31:00', 'DELIVERED', 'COD', 1);
SET @o32 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o32, 121, 5, 890000, 623000, 1335000),
(@o32, 50, 3, 2890000, 2023000, 2601000);

-- Đơn 33: 10/09 - User 15
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(15, 17820000, '2025-09-10 17:27:00', 'DELIVERED', 'PAYOS', 1);
SET @o33 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o33, 74, 2, 4590000, 3213000, 2754000),
(@o33, 12, 3, 2350000, 1645000, 2115000),
(@o33, 26, 1, 1590000, 1113000, 477000);

-- Đơn 34: 11/09 - User 7
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 5750000, '2025-09-11 08:13:00', 'DELIVERED', 'PAYOS', 1);
SET @o34 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o34, 117, 5, 1150000, 805000, 1725000);

-- Đơn 35: 11/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 8160000, '2025-09-11 10:31:00', 'DELIVERED', 'COD', 1);
SET @o35 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o35, 100, 4, 590000, 413000, 708000),
(@o35, 37, 4, 1450000, 1015000, 1740000);

-- Đơn 36: 11/09 - User 8
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 2580000, '2025-09-11 12:30:00', 'DELIVERED', 'COD', 1);
SET @o36 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o36, 132, 2, 1290000, 903000, 774000);

-- Đơn 37: 11/09 - User 14
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(14, 7950000, '2025-09-11 10:38:00', 'DELIVERED', 'COD', 1);
SET @o37 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o37, 25, 5, 1590000, 1113000, 2385000);

-- Đơn 38: 12/09 - User 15
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(15, 10950000, '2025-09-12 08:41:00', 'DELIVERED', 'PAYOS', 1);
SET @o38 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o38, 34, 4, 1450000, 1015000, 1740000),
(@o38, 116, 2, 1150000, 805000, 690000),
(@o38, 137, 3, 950000, 665000, 855000);

-- Đơn 39: 12/09 - User 5
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 950000, '2025-09-12 13:42:00', 'DELIVERED', 'COD', 1);
SET @o39 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o39, 139, 1, 950000, 665000, 285000);

-- Đơn 40: 12/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 18250000, '2025-09-12 15:25:00', 'DELIVERED', 'COD', 1);
SET @o40 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o40, 51, 5, 2890000, 2023000, 4335000),
(@o40, 138, 4, 950000, 665000, 1140000);

-- Đơn 41: 12/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 890000, '2025-09-12 09:45:00', 'DELIVERED', 'PAYOS', 1);
SET @o41 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o41, 125, 1, 890000, 623000, 267000);

-- Đơn 42: 12/09 - User 18
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(18, 33600000, '2025-09-12 19:37:00', 'DELIVERED', 'PAYOS', 1);
SET @o42 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o42, 83, 1, 4150000, 2905000, 1245000),
(@o42, 73, 5, 4590000, 3213000, 6885000),
(@o42, 57, 2, 3250000, 2275000, 1950000);

-- Đơn 43: 13/09 - User 17
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(17, 11320000, '2025-09-13 15:24:00', 'DELIVERED', 'COD', 1);
SET @o43 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o43, 113, 2, 1150000, 805000, 690000),
(@o43, 34, 5, 1450000, 1015000, 2175000),
(@o43, 98, 3, 590000, 413000, 531000);

-- Đơn 44: 13/09 - User 18
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(18, 16040000, '2025-09-13 16:18:00', 'DELIVERED', 'PAYOS', 1);
SET @o44 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o44, 28, 1, 1590000, 1113000, 477000),
(@o44, 53, 5, 2890000, 2023000, 4335000);

-- Đơn 45: 14/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 16350000, '2025-09-14 11:52:00', 'DELIVERED', 'COD', 1);
SET @o45 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o45, 147, 2, 1150000, 805000, 690000),
(@o45, 85, 2, 4150000, 2905000, 2490000),
(@o45, 117, 5, 1150000, 805000, 1725000);

-- Đơn 46: 14/09 - User 15
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(15, 7050000, '2025-09-14 10:53:00', 'DELIVERED', 'PAYOS', 1);
SET @o46 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o46, 11, 3, 2350000, 1645000, 2115000);

-- Đơn 47: 14/09 - User 9
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(9, 1690000, '2025-09-14 08:03:00', 'DELIVERED', 'PAYOS', 1);
SET @o47 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o47, 45, 1, 1690000, 1183000, 507000);

-- Đơn 48: 14/09 - User 13
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(13, 12730000, '2025-09-14 20:13:00', 'DELIVERED', 'COD', 1);
SET @o48 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o48, 45, 1, 1690000, 1183000, 507000),
(@o48, 34, 5, 1450000, 1015000, 2175000),
(@o48, 91, 1, 3790000, 2653000, 1137000);

-- Đơn 49: 14/09 - User 5
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 7580000, '2025-09-14 13:11:00', 'DELIVERED', 'COD', 1);
SET @o49 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o49, 91, 2, 3790000, 2653000, 2274000);

-- Đơn 50: 14/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 590000, '2025-09-14 08:58:00', 'DELIVERED', 'COD', 1);
SET @o50 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o50, 97, 1, 590000, 413000, 177000);

-- Đơn 51: 14/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 1770000, '2025-09-14 10:03:00', 'DELIVERED', 'PAYOS', 1);
SET @o51 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o51, 99, 3, 590000, 413000, 531000);

-- Đơn 52: 15/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 7770000, '2025-09-15 17:19:00', 'DELIVERED', 'PAYOS', 1);
SET @o52 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o52, 77, 1, 4590000, 3213000, 1377000),
(@o52, 28, 2, 1590000, 1113000, 954000);

-- Đơn 53: 15/09 - User 6
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 24050000, '2025-09-15 14:24:00', 'DELIVERED', 'COD', 1);
SET @o53 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o53, 19, 4, 1750000, 1225000, 2100000),
(@o53, 145, 4, 1150000, 805000, 1380000),
(@o53, 85, 3, 4150000, 2905000, 3735000);

-- Đơn 54: 15/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 7560000, '2025-09-15 15:50:00', 'DELIVERED', 'COD', 1);
SET @o54 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o54, 1, 4, 1890000, 1323000, 2268000);

-- Đơn 55: 16/09 - User 3
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 9580000, '2025-09-16 16:36:00', 'DELIVERED', 'PAYOS', 1);
SET @o55 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o55, 52, 2, 2890000, 2023000, 1734000),
(@o55, 139, 4, 950000, 665000, 1140000);

-- Đơn 56: 16/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 25270000, '2025-09-16 15:23:00', 'DELIVERED', 'PAYOS', 1);
SET @o56 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o56, 69, 5, 3150000, 2205000, 4725000),
(@o56, 44, 3, 1690000, 1183000, 1521000),
(@o56, 122, 5, 890000, 623000, 1335000);

-- Đơn 57: 17/09 - User 13
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(13, 8730000, '2025-09-17 09:17:00', 'DELIVERED', 'COD', 1);
SET @o57 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o57, 101, 5, 590000, 413000, 885000),
(@o57, 51, 2, 2890000, 2023000, 1734000);

-- Đơn 58: 17/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 3040000, '2025-09-17 13:36:00', 'DELIVERED', 'COD', 1);
SET @o58 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o58, 21, 1, 1750000, 1225000, 525000),
(@o58, 132, 1, 1290000, 903000, 387000);

-- Đơn 59: 17/09 - User 3
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 10230000, '2025-09-17 19:32:00', 'DELIVERED', 'PAYOS', 1);
SET @o59 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o59, 125, 3, 890000, 623000, 801000),
(@o59, 4, 4, 1890000, 1323000, 2268000);

-- Đơn 60: 17/09 - User 17
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(17, 4350000, '2025-09-17 14:03:00', 'DELIVERED', 'PAYOS', 1);
SET @o60 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o60, 34, 3, 1450000, 1015000, 1305000);

-- Đơn 61: 18/09 - User 13
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(13, 9760000, '2025-09-18 09:54:00', 'DELIVERED', 'COD', 1);
SET @o61 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o61, 130, 4, 1290000, 903000, 1548000),
(@o61, 116, 4, 1150000, 805000, 1380000);

-- Đơn 62: 18/09 - User 9
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(9, 8400000, '2025-09-18 19:51:00', 'DELIVERED', 'COD', 1);
SET @o62 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o62, 43, 2, 1690000, 1183000, 1014000),
(@o62, 116, 1, 1150000, 805000, 345000),
(@o62, 133, 3, 1290000, 903000, 1161000);

-- Đơn 63: 18/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 8540000, '2025-09-18 16:55:00', 'DELIVERED', 'COD', 1);
SET @o63 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o63, 43, 4, 1690000, 1183000, 2028000),
(@o63, 123, 2, 890000, 623000, 534000);

-- Đơn 64: 18/09 - User 10
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(10, 19400000, '2025-09-18 11:59:00', 'DELIVERED', 'COD', 1);
SET @o64 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o64, 154, 5, 990000, 693000, 1485000),
(@o64, 53, 5, 2890000, 2023000, 4335000);

-- Đơn 65: 18/09 - User 14
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(14, 7800000, '2025-09-18 14:28:00', 'DELIVERED', 'COD', 1);
SET @o65 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o65, 109, 2, 750000, 525000, 450000),
(@o65, 66, 2, 3150000, 2205000, 1890000);

-- Đơn 66: 18/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 24850000, '2025-09-18 17:28:00', 'DELIVERED', 'PAYOS', 1);
SET @o66 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o66, 92, 5, 3790000, 2653000, 5685000),
(@o66, 36, 2, 1450000, 1015000, 870000),
(@o66, 108, 4, 750000, 525000, 900000);

-- Đơn 67: 19/09 - User 12
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(12, 6750000, '2025-09-19 16:34:00', 'DELIVERED', 'COD', 1);
SET @o67 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o67, 138, 2, 950000, 665000, 570000),
(@o67, 133, 1, 1290000, 903000, 387000),
(@o67, 125, 4, 890000, 623000, 1068000);

-- Đơn 68: 19/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 13040000, '2025-09-19 13:24:00', 'DELIVERED', 'COD', 1);
SET @o68 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o68, 1, 2, 1890000, 1323000, 1134000),
(@o68, 53, 3, 2890000, 2023000, 2601000),
(@o68, 101, 1, 590000, 413000, 177000);

-- Đơn 69: 19/09 - User 17
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(17, 17520000, '2025-09-19 19:20:00', 'DELIVERED', 'COD', 1);
SET @o69 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o69, 20, 4, 1750000, 1225000, 2100000),
(@o69, 148, 5, 1150000, 805000, 1725000),
(@o69, 27, 3, 1590000, 1113000, 1431000);

-- Đơn 70: 19/09 - User 18
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(18, 15260000, '2025-09-19 15:36:00', 'DELIVERED', 'PAYOS', 1);
SET @o70 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o70, 100, 4, 590000, 413000, 708000),
(@o70, 21, 2, 1750000, 1225000, 1050000),
(@o70, 12, 4, 2350000, 1645000, 2820000);

-- Đơn 71: 19/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 29150000, '2025-09-19 18:02:00', 'DELIVERED', 'COD', 1);
SET @o71 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o71, 34, 1, 1450000, 1015000, 435000),
(@o71, 20, 5, 1750000, 1225000, 2625000),
(@o71, 93, 5, 3790000, 2653000, 5685000);

-- Đơn 72: 20/09 - User 8
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 13990000, '2025-09-20 18:31:00', 'DELIVERED', 'PAYOS', 1);
SET @o72 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o72, 61, 4, 3250000, 2275000, 3900000),
(@o72, 157, 1, 990000, 693000, 297000);

-- Đơn 73: 20/09 - User 18
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(18, 6940000, '2025-09-20 14:13:00', 'DELIVERED', 'COD', 1);
SET @o73 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o73, 18, 3, 1750000, 1225000, 1575000),
(@o73, 41, 1, 1690000, 1183000, 507000);

-- Đơn 74: 20/09 - User 7
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 11770000, '2025-09-20 08:04:00', 'DELIVERED', 'PAYOS', 1);
SET @o74 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o74, 5, 3, 1890000, 1323000, 1701000),
(@o74, 105, 2, 750000, 525000, 450000),
(@o74, 145, 4, 1150000, 805000, 1380000);

-- Đơn 75: 20/09 - User 3
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 18110000, '2025-09-20 15:13:00', 'DELIVERED', 'COD', 1);
SET @o75 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o75, 10, 5, 2350000, 1645000, 3525000),
(@o75, 27, 4, 1590000, 1113000, 1908000);

-- Đơn 76: 20/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 26600000, '2025-09-20 11:46:00', 'DELIVERED', 'COD', 1);
SET @o76 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o76, 28, 5, 1590000, 1113000, 2385000),
(@o76, 68, 5, 3150000, 2205000, 4725000),
(@o76, 34, 2, 1450000, 1015000, 870000);

-- Đơn 77: 20/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 15550000, '2025-09-20 10:44:00', 'DELIVERED', 'COD', 1);
SET @o77 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o77, 82, 2, 4150000, 2905000, 2490000),
(@o77, 35, 5, 1450000, 1015000, 2175000);

-- Đơn 78: 21/09 - User 3
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 9820000, '2025-09-21 18:15:00', 'DELIVERED', 'COD', 1);
SET @o78 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o78, 147, 1, 1150000, 805000, 345000),
(@o78, 49, 3, 2890000, 2023000, 2601000);

-- Đơn 79: 21/09 - User 5
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 1180000, '2025-09-21 09:50:00', 'DELIVERED', 'PAYOS', 1);
SET @o79 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o79, 100, 2, 590000, 413000, 354000);

-- Đơn 80: 21/09 - User 8
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 1780000, '2025-09-21 18:01:00', 'DELIVERED', 'COD', 1);
SET @o80 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o80, 123, 2, 890000, 623000, 534000);

-- Đơn 81: 21/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 18060000, '2025-09-21 18:14:00', 'DELIVERED', 'COD', 1);
SET @o81 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o81, 29, 5, 1590000, 1113000, 2385000),
(@o81, 130, 4, 1290000, 903000, 1548000),
(@o81, 157, 5, 990000, 693000, 1485000);

-- Đơn 82: 22/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 8170000, '2025-09-22 13:31:00', 'DELIVERED', 'COD', 1);
SET @o82 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o82, 99, 3, 590000, 413000, 531000),
(@o82, 21, 3, 1750000, 1225000, 1575000),
(@o82, 113, 1, 1150000, 805000, 345000);

-- Đơn 83: 22/09 - User 12
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(12, 5190000, '2025-09-22 11:29:00', 'DELIVERED', 'PAYOS', 1);
SET @o83 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o83, 99, 1, 590000, 413000, 177000),
(@o83, 117, 4, 1150000, 805000, 1380000);

-- Đơn 84: 22/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 21720000, '2025-09-22 19:33:00', 'DELIVERED', 'COD', 1);
SET @o84 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o84, 75, 3, 4590000, 3213000, 4131000),
(@o84, 28, 5, 1590000, 1113000, 2385000);

-- Đơn 85: 22/09 - User 8
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 13770000, '2025-09-22 10:20:00', 'DELIVERED', 'PAYOS', 1);
SET @o85 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o85, 73, 3, 4590000, 3213000, 4131000);

-- Đơn 86: 22/09 - User 10
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(10, 1150000, '2025-09-22 13:15:00', 'DELIVERED', 'PAYOS', 1);
SET @o86 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o86, 145, 1, 1150000, 805000, 345000);

-- Đơn 87: 22/09 - User 14
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(14, 13000000, '2025-09-22 12:41:00', 'DELIVERED', 'COD', 1);
SET @o87 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o87, 149, 5, 1150000, 805000, 1725000),
(@o87, 36, 5, 1450000, 1015000, 2175000);

-- Đơn 88: 22/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 25450000, '2025-09-22 14:03:00', 'DELIVERED', 'COD', 1);
SET @o88 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o88, 9, 2, 2350000, 1645000, 1410000),
(@o88, 81, 5, 4150000, 2905000, 6225000);

-- Đơn 89: 23/09 - User 12
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(12, 20120000, '2025-09-23 11:05:00', 'DELIVERED', 'PAYOS', 1);
SET @o89 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o89, 89, 3, 3790000, 2653000, 3411000),
(@o89, 17, 5, 1750000, 1225000, 2625000);

-- Đơn 90: 23/09 - User 15
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(15, 2360000, '2025-09-23 10:07:00', 'DELIVERED', 'PAYOS', 1);
SET @o90 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o90, 98, 4, 590000, 413000, 708000);

-- Đơn 91: 23/09 - User 13
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(13, 30150000, '2025-09-23 20:56:00', 'DELIVERED', 'PAYOS', 1);
SET @o91 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o91, 33, 3, 1450000, 1015000, 1305000),
(@o91, 76, 5, 4590000, 3213000, 6885000),
(@o91, 141, 3, 950000, 665000, 855000);

-- Đơn 92: 24/09 - User 8
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(8, 19400000, '2025-09-24 17:18:00', 'DELIVERED', 'COD', 1);
SET @o92 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o92, 156, 5, 990000, 693000, 1485000),
(@o92, 51, 5, 2890000, 2023000, 4335000);

-- Đơn 93: 24/09 - User 5
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 950000, '2025-09-24 11:30:00', 'DELIVERED', 'PAYOS', 1);
SET @o93 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o93, 138, 1, 950000, 665000, 285000);

-- Đơn 94: 25/09 - User 9
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(9, 10620000, '2025-09-25 09:11:00', 'DELIVERED', 'COD', 1);
SET @o94 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o94, 115, 5, 1150000, 805000, 1725000),
(@o94, 44, 1, 1690000, 1183000, 507000),
(@o94, 29, 2, 1590000, 1113000, 954000);

-- Đơn 95: 25/09 - User 7
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(7, 13000000, '2025-09-25 11:00:00', 'DELIVERED', 'PAYOS', 1);
SET @o95 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o95, 59, 4, 3250000, 2275000, 3900000);

-- Đơn 96: 25/09 - User 10
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(10, 4590000, '2025-09-25 17:22:00', 'DELIVERED', 'PAYOS', 1);
SET @o96 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o96, 77, 1, 4590000, 3213000, 1377000);

-- Đơn 97: 25/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 21450000, '2025-09-25 11:22:00', 'DELIVERED', 'PAYOS', 1);
SET @o97 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o97, 61, 1, 3250000, 2275000, 975000),
(@o97, 106, 5, 750000, 525000, 1125000),
(@o97, 52, 5, 2890000, 2023000, 4335000);

-- Đơn 98: 26/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 22610000, '2025-09-26 13:54:00', 'DELIVERED', 'PAYOS', 1);
SET @o98 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o98, 60, 5, 3250000, 2275000, 4875000),
(@o98, 26, 4, 1590000, 1113000, 1908000);

-- Đơn 99: 26/09 - User 5
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 3250000, '2025-09-26 13:47:00', 'DELIVERED', 'PAYOS', 1);
SET @o99 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o99, 57, 1, 3250000, 2275000, 975000);

-- Đơn 100: 26/09 - User 5
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 15380000, '2025-09-26 12:45:00', 'DELIVERED', 'COD', 1);
SET @o100 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o100, 122, 1, 890000, 623000, 267000),
(@o100, 4, 1, 1890000, 1323000, 567000),
(@o100, 65, 4, 3150000, 2205000, 3780000);

-- Đơn 101: 26/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 18120000, '2025-09-26 09:48:00', 'DELIVERED', 'COD', 1);
SET @o101 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o101, 5, 3, 1890000, 1323000, 1701000),
(@o101, 84, 3, 4150000, 2905000, 3735000);

-- Đơn 102: 26/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 18050000, '2025-09-26 19:43:00', 'DELIVERED', 'PAYOS', 1);
SET @o102 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o102, 141, 3, 950000, 665000, 855000),
(@o102, 115, 5, 1150000, 805000, 1725000),
(@o102, 65, 3, 3150000, 2205000, 2835000);

-- Đơn 103: 27/09 - User 13
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(13, 1900000, '2025-09-27 17:21:00', 'DELIVERED', 'PAYOS', 1);
SET @o103 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o103, 137, 2, 950000, 665000, 570000);

-- Đơn 104: 27/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 12450000, '2025-09-27 13:11:00', 'DELIVERED', 'PAYOS', 1);
SET @o104 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o104, 85, 3, 4150000, 2905000, 3735000);

-- Đơn 105: 28/09 - User 5
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 9340000, '2025-09-28 18:23:00', 'DELIVERED', 'PAYOS', 1);
SET @o105 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o105, 17, 5, 1750000, 1225000, 2625000),
(@o105, 98, 1, 590000, 413000, 177000);

-- Đơn 106: 28/09 - User 12
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(12, 5160000, '2025-09-28 20:58:00', 'DELIVERED', 'PAYOS', 1);
SET @o106 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o106, 129, 4, 1290000, 903000, 1548000);

-- Đơn 107: 28/09 - User 9
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(9, 10270000, '2025-09-28 17:32:00', 'DELIVERED', 'PAYOS', 1);
SET @o107 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o107, 97, 3, 590000, 413000, 531000),
(@o107, 108, 2, 750000, 525000, 450000),
(@o107, 19, 4, 1750000, 1225000, 2100000);

-- Đơn 108: 29/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 18950000, '2025-09-29 08:41:00', 'DELIVERED', 'COD', 1);
SET @o108 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o108, 91, 5, 3790000, 2653000, 5685000);

-- Đơn 109: 29/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 4600000, '2025-09-29 19:08:00', 'DELIVERED', 'PAYOS', 1);
SET @o109 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o109, 148, 1, 1150000, 805000, 345000),
(@o109, 115, 3, 1150000, 805000, 1035000);

-- Đơn 110: 29/09 - User 10
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(10, 20700000, '2025-09-29 12:39:00', 'DELIVERED', 'COD', 1);
SET @o110 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o110, 21, 1, 1750000, 1225000, 525000),
(@o110, 91, 5, 3790000, 2653000, 5685000);

-- Đơn 111: 29/09 - User 18
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(18, 2300000, '2025-09-29 09:38:00', 'DELIVERED', 'PAYOS', 1);
SET @o111 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o111, 146, 2, 1150000, 805000, 690000);

-- Đơn 112: 29/09 - User 6
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 30020000, '2025-09-29 14:12:00', 'DELIVERED', 'PAYOS', 1);
SET @o112 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o112, 76, 3, 4590000, 3213000, 4131000),
(@o112, 61, 5, 3250000, 2275000, 4875000);

-- Đơn 113: 29/09 - User 16
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(16, 7800000, '2025-09-29 20:38:00', 'DELIVERED', 'COD', 1);
SET @o113 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o113, 36, 3, 1450000, 1015000, 1305000),
(@o113, 117, 3, 1150000, 805000, 1035000);

-- Đơn 114: 29/09 - User 10
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(10, 2950000, '2025-09-29 18:20:00', 'DELIVERED', 'PAYOS', 1);
SET @o114 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o114, 101, 5, 590000, 413000, 885000);

-- Đơn 115: 30/09 - User 3
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(3, 30000000, '2025-09-30 15:15:00', 'DELIVERED', 'COD', 1);
SET @o115 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o115, 10, 3, 2350000, 1645000, 2115000),
(@o115, 75, 5, 4590000, 3213000, 6885000);

-- Đơn 116: 30/09 - User 6
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(6, 4770000, '2025-09-30 13:29:00', 'DELIVERED', 'PAYOS', 1);
SET @o116 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o116, 28, 3, 1590000, 1113000, 1431000);

-- Đơn 117: 30/09 - User 5
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(5, 9300000, '2025-09-30 16:45:00', 'DELIVERED', 'PAYOS', 1);
SET @o117 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o117, 36, 3, 1450000, 1015000, 1305000),
(@o117, 154, 5, 990000, 693000, 1485000);

-- Đơn 118: 30/09 - User 4
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(4, 20770000, '2025-09-30 16:12:00', 'DELIVERED', 'COD', 1);
SET @o118 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o118, 18, 4, 1750000, 1225000, 2100000),
(@o118, 75, 3, 4590000, 3213000, 4131000);

-- Đơn 119: 30/09 - User 11
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(11, 17350000, '2025-09-30 08:13:00', 'DELIVERED', 'COD', 1);
SET @o119 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o119, 114, 5, 1150000, 805000, 1725000),
(@o119, 138, 3, 950000, 665000, 855000),
(@o119, 21, 5, 1750000, 1225000, 2625000);

-- Đơn 120: 30/09 - User 18
INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES
(18, 15380000, '2025-09-30 10:49:00', 'DELIVERED', 'COD', 1);
SET @o120 = LAST_INSERT_ID();
INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES
(@o120, 33, 4, 1450000, 1015000, 1740000),
(@o120, 129, 2, 1290000, 903000, 774000),
(@o120, 18, 4, 1750000, 1225000, 2100000);

-- ==========================================
-- ✅ TỔNG KẾT
-- Đã tạo 120 đơn hàng cho tháng 9/2025
-- ==========================================

-- ==========================================
--  T?NG K?T D? LI?U TH�NG 9/2025
-- ==========================================
--  Nh?p kho: 5 l� h�ng (01, 05, 10, 15, 20/09)
--  �on h�ng: 120 don DELIVERED
--  Users: 16 customers (ID 3-18)
--  Products: 20 s?n ph?m d?u (ID 1-20)
--  Cost Price: 70% gi� b�n
-- ==========================================

SET foreign_key_checks = 1;

--  C�CH S? D?NG:
-- 1. �?m b?o d� ch?y ApplicationInitConfig.java (t?o users, products)
-- 2. Import file n�y: mysql -u root -p shoe_shop_test < september_2025_data_fixed.sql
-- 3. Ki?m tra: SELECT COUNT(*) FROM orders WHERE created_date LIKE '2025-09%';
-- ==========================================
