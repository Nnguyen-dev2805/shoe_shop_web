-- =============================================
-- Migration: Remove voucher column from product table
-- Date: 2025-10-18
-- Reason: Field not used, voucher logic moved to Discount system
-- =============================================

USE shoe_shop_basic;

-- Drop voucher column from product table
ALTER TABLE product DROP COLUMN IF EXISTS voucher;

-- Verification
SELECT 'voucher column removed from product table successfully!' AS Status;
DESCRIBE product;

-- =============================================
-- SUMMARY
-- =============================================
-- Removed: product.voucher (Long)
-- Reason: Voucher/Discount system now uses separate Discount entity
--         Product không cần lưu voucher_id nữa
-- =============================================
