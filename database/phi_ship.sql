-- ============================================
-- DỮ LIỆU PHÍ SHIP CHUẨN (7 MỨC)
-- ============================================
-- Dựa theo Entity: ShippingRate.java
-- Columns: min_distance_km, max_distance_km, fee, description, is_active
-- created_at, updated_at sẽ tự động set bởi @PrePersist
-- ============================================

-- Xóa dữ liệu cũ (nếu có)
DELETE FROM shipping_rate;

-- Reset AUTO_INCREMENT về 1
ALTER TABLE shipping_rate AUTO_INCREMENT = 1;

-- Insert 7 mức phí ship theo chuẩn thị trường
INSERT INTO shipping_rate (min_distance_km, max_distance_km, fee, description, is_active, created_at, updated_at) VALUES
(0.0, 3.0, 15000, 'Phí ship chuẩn (0-3km)', 1, NOW(), NOW()),
(3.0, 7.0, 22000, 'Phí ship nội thành (3-7km)', 1, NOW(), NOW()),
(7.0, 15.0, 35000, 'Phí ship ngoại thành (7-15km)', 1, NOW(), NOW()),
(15.0, 25.0, 55000, 'Phí ship vùng xa (15-25km)', 1, NOW(), NOW()),
(25.0, 40.0, 80000, 'Phí ship liên tỉnh gần (25-40km)', 1, NOW(), NOW()),
(40.0, 80.0, 120000, 'Phí ship liên tỉnh (40-80km)', 1, NOW(), NOW()),
(80.0, 999.0, 180000, 'Phí ship liên tỉnh xa (>80km)', 1, NOW(), NOW());

-- Verify inserted data
SELECT 
    id,
    CONCAT(min_distance_km, '-', max_distance_km, ' km') as khoang_cach,
    CONCAT(FORMAT(fee, 0), 'đ') as phi_ship,
    description as mo_ta,
    CASE WHEN is_active = 1 THEN 'Đang hoạt động' ELSE 'Tạm ngưng' END as trang_thai,
    DATE_FORMAT(created_at, '%d/%m/%Y %H:%i') as ngay_tao
FROM shipping_rate
ORDER BY min_distance_km;

-- Expected result:
-- +----+---------------+-----------+----------------------------------+------------------+------------------+
-- | id | khoang_cach   | phi_ship  | mo_ta                            | trang_thai       | ngay_tao         |
-- +----+---------------+-----------+----------------------------------+------------------+------------------+
-- |  1 | 0.0-3.0 km    | 15,000đ   | Phí ship chuẩn (0-3km)           | Đang hoạt động   | 17/10/2025 20:50 |
-- |  2 | 3.0-7.0 km    | 22,000đ   | Phí ship nội thành (3-7km)       | Đang hoạt động   | 17/10/2025 20:50 |
-- |  3 | 7.0-15.0 km   | 35,000đ   | Phí ship ngoại thành (7-15km)    | Đang hoạt động   | 17/10/2025 20:50 |
-- |  4 | 15.0-25.0 km  | 55,000đ   | Phí ship vùng xa (15-25km)       | Đang hoạt động   | 17/10/2025 20:50 |
-- |  5 | 25.0-40.0 km  | 80,000đ   | Phí ship liên tỉnh gần (25-40km) | Đang hoạt động   | 17/10/2025 20:50 |
-- |  6 | 40.0-80.0 km  | 120,000đ  | Phí ship liên tỉnh (40-80km)     | Đang hoạt động   | 17/10/2025 20:50 |
-- |  7 | 80.0-999.0 km | 180,000đ  | Phí ship liên tỉnh xa (>80km)    | Đang hoạt động   | 17/10/2025 20:50 |
-- +----+---------------+-----------+----------------------------------+------------------+------------------+