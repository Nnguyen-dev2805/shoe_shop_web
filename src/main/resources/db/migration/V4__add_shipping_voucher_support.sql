-- ============================================
-- V4: Add Shipping Voucher Support
-- ============================================
-- Add columns to support both ORDER and SHIPPING vouchers

-- Add voucher type column (ORDER_DISCOUNT or SHIPPING_DISCOUNT)
ALTER TABLE discount 
ADD COLUMN type VARCHAR(50) DEFAULT 'ORDER_DISCOUNT' AFTER status;

-- Add discount type column (PERCENTAGE or FIXED_AMOUNT)
ALTER TABLE discount 
ADD COLUMN discount_type VARCHAR(50) DEFAULT 'PERCENTAGE' AFTER type;

-- Add max discount amount (for percentage shipping vouchers)
ALTER TABLE discount 
ADD COLUMN max_discount_amount DOUBLE DEFAULT NULL AFTER percent;

-- Update existing records to ORDER_DISCOUNT type
UPDATE discount 
SET type = 'ORDER_DISCOUNT', 
    discount_type = 'PERCENTAGE'
WHERE type IS NULL;

-- Add indexes for better query performance
CREATE INDEX idx_discount_type ON discount(type);
CREATE INDEX idx_discount_type_status ON discount(type, status);

-- Add comments for clarity
ALTER TABLE discount 
MODIFY COLUMN type VARCHAR(50) NOT NULL 
COMMENT 'Loại voucher: ORDER_DISCOUNT (giảm đơn hàng) hoặc SHIPPING_DISCOUNT (giảm phí ship)';

ALTER TABLE discount 
MODIFY COLUMN discount_type VARCHAR(50) NOT NULL 
COMMENT 'Kiểu giảm: PERCENTAGE (%) hoặc FIXED_AMOUNT (VNĐ)';

ALTER TABLE discount 
MODIFY COLUMN max_discount_amount DOUBLE DEFAULT NULL 
COMMENT 'Giảm tối đa (chỉ dùng cho shipping voucher %)';

-- Insert sample shipping vouchers for testing
INSERT INTO discount (name, quantity, percent, status, type, discount_type, max_discount_amount, min_order_value, start_date, end_date, created_date, updated_date, created_by)
VALUES 
-- Fixed amount shipping voucher
('Miễn phí ship 30K', NULL, 30000, 'ACTIVE', 'SHIPPING_DISCOUNT', 'FIXED_AMOUNT', NULL, 200000, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), CURDATE(), CURDATE(), 1),

-- Percentage shipping voucher with max discount
('Giảm 50% phí ship', NULL, 0.5, 'ACTIVE', 'SHIPPING_DISCOUNT', 'PERCENTAGE', 20000, 100000, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), CURDATE(), CURDATE(), 1),

-- Another fixed amount shipping voucher
('Free ship toàn quốc', NULL, 50000, 'ACTIVE', 'SHIPPING_DISCOUNT', 'FIXED_AMOUNT', NULL, 500000, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), CURDATE(), CURDATE(), 1);

-- Verify structure
SELECT 
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'discount'
AND TABLE_SCHEMA = DATABASE()
ORDER BY ORDINAL_POSITION;
