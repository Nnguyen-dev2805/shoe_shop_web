-- =============================================
-- Migration: Cleanup unused fields from Discount & DiscountUsed entities
-- Date: 2025-10-18
-- Reason: Remove redundant and unused fields based on code analysis
-- =============================================

USE shoe_shop_basic;

-- =============================================
-- STEP 1: Clean up DISCOUNT table
-- =============================================

-- Drop redundant discount_scope (duplicate with 'type' field)
ALTER TABLE discount DROP COLUMN IF EXISTS discount_scope;

-- Drop unused fields for product/category-specific discounts
ALTER TABLE discount DROP COLUMN IF EXISTS applies_to_sale_items;
ALTER TABLE discount DROP COLUMN IF EXISTS applicable_product_ids;
ALTER TABLE discount DROP COLUMN IF EXISTS applicable_category_ids;

-- Drop audit fields (not used in code)
ALTER TABLE discount DROP COLUMN IF EXISTS updated_by;
ALTER TABLE discount DROP COLUMN IF EXISTS updated_date;

-- Note: Keep created_by (commented in code but may be useful for future)

-- =============================================
-- STEP 2: Clean up DISCOUNT_USED table
-- =============================================

-- Drop unused tracking fields
ALTER TABLE discount_used DROP COLUMN IF EXISTS discount_type;
ALTER TABLE discount_used DROP COLUMN IF EXISTS original_order_value;

-- =============================================
-- VERIFICATION
-- =============================================

SELECT 'Discount table cleanup completed!' AS Status;
DESCRIBE discount;

SELECT 'DiscountUsed table cleanup completed!' AS Status;
DESCRIBE discount_used;

-- =============================================
-- SUMMARY OF REMOVED COLUMNS
-- =============================================
-- From DISCOUNT:
--   ❌ discount_scope (redundant with 'type')
--   ❌ applies_to_sale_items (not used)
--   ❌ applicable_product_ids (not used)
--   ❌ applicable_category_ids (not used)
--   ❌ updated_by (not used)
--   ❌ updated_date (not used)
--
-- From DISCOUNT_USED:
--   ❌ discount_type (not used - can get from discount.type)
--   ❌ original_order_value (not used - can get from order.totalPrice)
-- =============================================
