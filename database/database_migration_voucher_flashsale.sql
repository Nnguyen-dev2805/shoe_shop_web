-- =====================================================
-- DATABASE MIGRATION SCRIPT
-- Voucher & Flash Sale System
-- Created: 2025-10-15
-- =====================================================

-- =====================================================
-- 1. Cập nhật bảng DISCOUNT - Thêm phân loại voucher
-- =====================================================

ALTER TABLE discount 
    ADD COLUMN discount_type VARCHAR(20) NOT NULL DEFAULT 'ORDER' COMMENT 'Loại voucher: ORDER, PRODUCT, CATEGORY, SHIPPING',
    ADD COLUMN max_discount_amount DOUBLE DEFAULT NULL COMMENT 'Số tiền giảm tối đa',
    ADD COLUMN applies_to_sale_items BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Có áp dụng cho sản phẩm sale không',
    ADD COLUMN applicable_product_ids TEXT DEFAULT NULL COMMENT 'Danh sách product IDs (nếu type = PRODUCT)',
    ADD COLUMN applicable_category_ids TEXT DEFAULT NULL COMMENT 'Danh sách category IDs (nếu type = CATEGORY)';

-- Tạo index cho discount_type để query nhanh hơn
CREATE INDEX idx_discount_type ON discount(discount_type);
CREATE INDEX idx_discount_status_type ON discount(status, discount_type);

-- =====================================================
-- 2. Cập nhật bảng ORDERS - Lưu thông tin discount đã áp dụng
-- =====================================================

ALTER TABLE orders 
    ADD COLUMN discount_id BIGINT DEFAULT NULL COMMENT 'FK to discount table',
    ADD COLUMN discount_amount DOUBLE DEFAULT NULL COMMENT 'Số tiền được giảm',
    ADD COLUMN original_total_price DOUBLE DEFAULT NULL COMMENT 'Giá gốc trước khi giảm',
    ADD COLUMN flash_sale_id BIGINT DEFAULT NULL COMMENT 'FK to flash_sale table',
    ADD COLUMN discount_code VARCHAR(50) DEFAULT NULL COMMENT 'Mã voucher (nếu có)';

-- Index cho query nhanh
CREATE INDEX idx_orders_discount ON orders(discount_id);
CREATE INDEX idx_orders_flash_sale ON orders(flash_sale_id);

-- =====================================================
-- 3. Tạo bảng FLASH_SALE - Đợt giảm giá
-- =====================================================

CREATE TABLE flash_sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT 'Tên flash sale',
    description TEXT COMMENT 'Mô tả flash sale',
    start_time DATETIME NOT NULL COMMENT 'Thời gian bắt đầu',
    end_time DATETIME NOT NULL COMMENT 'Thời gian kết thúc',
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' COMMENT 'Trạng thái: SCHEDULED, ACTIVE, ENDED, CANCELLED',
    total_items INT NOT NULL DEFAULT 0 COMMENT 'Tổng số sản phẩm',
    total_sold INT NOT NULL DEFAULT 0 COMMENT 'Tổng số đã bán',
    banner_image VARCHAR(500) COMMENT 'Đường dẫn banner image',
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT COMMENT 'Admin tạo',
    is_delete BOOLEAN NOT NULL DEFAULT FALSE,
    
    INDEX idx_flash_sale_status (status),
    INDEX idx_flash_sale_time (start_time, end_time),
    INDEX idx_flash_sale_active (status, start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Bảng lưu thông tin flash sale';

-- =====================================================
-- 4. Tạo bảng FLASH_SALE_ITEM - Sản phẩm trong flash sale
-- =====================================================

CREATE TABLE flash_sale_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flash_sale_id BIGINT NOT NULL COMMENT 'FK to flash_sale',
    product_detail_id BIGINT NOT NULL COMMENT 'FK to product_detail',
    original_price DOUBLE NOT NULL COMMENT 'Giá gốc',
    flash_sale_price DOUBLE NOT NULL COMMENT 'Giá flash sale',
    discount_percent DOUBLE COMMENT '% giảm giá',
    stock INT NOT NULL DEFAULT 0 COMMENT 'Số lượng dành cho flash sale',
    sold INT NOT NULL DEFAULT 0 COMMENT 'Đã bán',
    limit_per_user INT COMMENT 'Giới hạn mỗi user',
    position INT DEFAULT 0 COMMENT 'Vị trí hiển thị',
    
    FOREIGN KEY (flash_sale_id) REFERENCES flash_sale(id) ON DELETE CASCADE,
    FOREIGN KEY (product_detail_id) REFERENCES product_detail(id) ON DELETE CASCADE,
    
    INDEX idx_flash_sale_item_flash_sale (flash_sale_id),
    INDEX idx_flash_sale_item_product (product_detail_id),
    INDEX idx_flash_sale_item_stock (stock, sold)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Bảng lưu sản phẩm trong flash sale';

-- =====================================================
-- 5. Thêm Foreign Keys cho orders
-- =====================================================

ALTER TABLE orders 
    ADD CONSTRAINT fk_orders_discount 
    FOREIGN KEY (discount_id) REFERENCES discount(id) ON DELETE SET NULL;

ALTER TABLE orders 
    ADD CONSTRAINT fk_orders_flash_sale 
    FOREIGN KEY (flash_sale_id) REFERENCES flash_sale(id) ON DELETE SET NULL;

-- =====================================================
-- 6. Cập nhật bảng DISCOUNT_USED - Thêm thông tin
-- =====================================================

ALTER TABLE discount_used 
    ADD COLUMN discount_type VARCHAR(20) COMMENT 'Loại voucher đã dùng',
    ADD COLUMN original_order_value DOUBLE COMMENT 'Giá trị đơn gốc';

CREATE INDEX idx_discount_used_type ON discount_used(discount_type);

-- =====================================================
-- 7. Insert dữ liệu mẫu (Optional - for testing)
-- =====================================================

-- Voucher giảm giá đơn hàng
INSERT INTO discount (name, quantity, discount_percent, status, min_order_value, start_date, end_date, discount_type, max_discount_amount, created_date)
VALUES 
    ('GIẢM 20% ĐƠN HÀNG', 100, 0.20, 'ACTIVE', 500000, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'ORDER', 100000, CURDATE()),
    ('FREESHIP 30K', 200, 0, 'ACTIVE', 0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'SHIPPING', 30000, CURDATE()),
    ('GIẢM 15% GIÀY THỂ THAO', 50, 0.15, 'ACTIVE', 200000, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'CATEGORY', 50000, CURDATE());

-- Flash sale mẫu
INSERT INTO flash_sale (name, description, start_time, end_time, status, created_date)
VALUES 
    ('Flash Sale 12h Trưa', 'Giảm giá cực sốc trong 2 giờ', 
     CONCAT(CURDATE(), ' 12:00:00'), 
     CONCAT(CURDATE(), ' 14:00:00'), 
     'SCHEDULED', 
     NOW()),
    ('Flash Sale 20h Tối', 'Flash sale buổi tối giảm đến 50%',
     CONCAT(CURDATE(), ' 20:00:00'),
     CONCAT(CURDATE(), ' 22:00:00'),
     'SCHEDULED',
     NOW());

-- =====================================================
-- 8. Query mẫu để test
-- =====================================================

/*
-- Lấy tất cả voucher đang active
SELECT * FROM discount 
WHERE status = 'ACTIVE' 
  AND is_delete = FALSE 
  AND CURDATE() BETWEEN start_date AND end_date;

-- Lấy voucher theo loại
SELECT * FROM discount 
WHERE discount_type = 'ORDER' 
  AND status = 'ACTIVE';

-- Lấy flash sale đang active với số sản phẩm
SELECT fs.*, COUNT(fsi.id) as total_products
FROM flash_sale fs
LEFT JOIN flash_sale_item fsi ON fs.id = fsi.flash_sale_id
WHERE fs.status = 'ACTIVE'
  AND NOW() BETWEEN fs.start_time AND fs.end_time
  AND fs.is_delete = FALSE
GROUP BY fs.id;

-- Lấy sản phẩm flash sale còn hàng
SELECT fsi.*, pd.*, p.*
FROM flash_sale_item fsi
JOIN product_detail pd ON fsi.product_detail_id = pd.id
JOIN product p ON pd.product_id = p.id
WHERE fsi.flash_sale_id = ?
  AND fsi.stock > fsi.sold;

-- Thống kê discount usage
SELECT 
    d.name,
    d.discount_type,
    d.quantity as total_available,
    COUNT(du.id) as times_used,
    SUM(du.discount_amount) as total_savings
FROM discount d
LEFT JOIN discount_used du ON d.id = du.discount_id AND du.is_active = TRUE
WHERE d.is_delete = FALSE
GROUP BY d.id;
*/

-- =====================================================
-- 9. Rollback script (nếu cần revert)
-- =====================================================

/*
-- Chạy script này nếu muốn revert lại:

DROP TABLE IF EXISTS flash_sale_item;
DROP TABLE IF EXISTS flash_sale;

ALTER TABLE orders 
    DROP FOREIGN KEY IF EXISTS fk_orders_discount,
    DROP FOREIGN KEY IF EXISTS fk_orders_flash_sale,
    DROP INDEX IF EXISTS idx_orders_discount,
    DROP INDEX IF EXISTS idx_orders_flash_sale,
    DROP COLUMN IF EXISTS discount_id,
    DROP COLUMN IF EXISTS discount_amount,
    DROP COLUMN IF EXISTS original_total_price,
    DROP COLUMN IF EXISTS flash_sale_id,
    DROP COLUMN IF EXISTS discount_code;

ALTER TABLE discount 
    DROP INDEX IF EXISTS idx_discount_type,
    DROP INDEX IF EXISTS idx_discount_status_type,
    DROP COLUMN IF EXISTS discount_type,
    DROP COLUMN IF EXISTS max_discount_amount,
    DROP COLUMN IF EXISTS applies_to_sale_items,
    DROP COLUMN IF EXISTS applicable_product_ids,
    DROP COLUMN IF EXISTS applicable_category_ids;

ALTER TABLE discount_used
    DROP INDEX IF EXISTS idx_discount_used_type,
    DROP COLUMN IF EXISTS discount_type,
    DROP COLUMN IF EXISTS original_order_value;
*/

-- =====================================================
-- 10. HOÀN TẤT!
-- =====================================================

-- Verify schema
SELECT 'Migration completed successfully!' as Status;

-- Check tables exist
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME IN ('flash_sale', 'flash_sale_item');

-- Check new columns in discount
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'discount'
  AND COLUMN_NAME IN ('discount_type', 'max_discount_amount', 'applicable_product_ids');

-- Check new columns in orders
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'orders'
  AND COLUMN_NAME IN ('discount_id', 'discount_amount', 'flash_sale_id');
