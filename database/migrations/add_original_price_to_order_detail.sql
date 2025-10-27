-- ========================================
-- Migration: Add original_price to order_detail
-- Date: 2025-10-27
-- Purpose: Lưu giá gốc để hiển thị giá gạch bỏ trong order history
-- ========================================

-- 1. Add column original_price
ALTER TABLE order_detail 
ADD COLUMN original_price DOUBLE NULL 
COMMENT 'Giá gốc trước khi áp dụng flash sale/discount (để hiển thị gạch bỏ)';

-- 2. Update existing data: Set original_price = price (giả định price cũ là giá gốc)
UPDATE order_detail 
SET original_price = price 
WHERE original_price IS NULL;

-- 3. Optional: Add index if querying by discount percentage
-- CREATE INDEX idx_order_detail_price_discount ON order_detail(original_price, price);

-- ✅ DONE
-- Từ giờ khi tạo order mới:
-- - price = Giá đã mua (có flash sale/discount)
-- - original_price = Giá gốc (để hiển thị gạch bỏ)
