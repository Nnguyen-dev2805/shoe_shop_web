-- ============================================
-- Migration: Add PAYOS to pay_option ENUM
-- Description: Thêm giá trị 'PAYOS' vào column pay_option trong bảng orders
--              để hỗ trợ thanh toán qua PayOS payment gateway
-- Date: 2025-10-20
-- Author: System Migration
-- ============================================

USE shoe_shop;

-- Bước 1: Kiểm tra cấu trúc hiện tại
-- SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_NAME = 'orders' AND COLUMN_NAME = 'pay_option';

-- Bước 2: ALTER TABLE để thêm 'PAYOS' vào ENUM
ALTER TABLE orders 
MODIFY COLUMN pay_option ENUM('COD', 'VNPAY', 'PAYOS') NOT NULL;

-- Bước 3: Verify thay đổi
SELECT COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'shoe_shop' 
  AND TABLE_NAME = 'orders' 
  AND COLUMN_NAME = 'pay_option';

-- Expected result: enum('COD','VNPAY','PAYOS')

-- ============================================
-- ROLLBACK (nếu cần quay lại)
-- ============================================
-- ALTER TABLE orders 
-- MODIFY COLUMN pay_option ENUM('COD', 'VNPAY') NOT NULL;
-- ============================================

SELECT '✅ Migration completed: PAYOS added to pay_option enum' AS status;
