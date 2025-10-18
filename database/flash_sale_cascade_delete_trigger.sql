-- =============================================
-- Flash Sale CASCADE DELETE Trigger
-- Date: 2025-10-18
-- Purpose: Auto cleanup flash_sale_item when deleting flash_sale
-- =============================================

USE shoe_shop_basic;

-- =============================================
-- TRIGGER: Cascade delete flash_sale_item when deleting flash_sale
-- 
-- KHI XÓA FLASH SALE → Xóa TẤT CẢ flash_sale_item của flash sale đó
-- =============================================

DROP TRIGGER IF EXISTS before_delete_flash_sale_cascade;

DELIMITER $$

CREATE TRIGGER before_delete_flash_sale_cascade
BEFORE DELETE ON flash_sale
FOR EACH ROW
BEGIN
    -- Xóa tất cả flash_sale_item liên quan đến flash_sale này
    DELETE FROM flash_sale_item 
    WHERE flash_sale_id = OLD.id;
    
    -- Log (optional - có thể bỏ comment nếu muốn log)
    -- INSERT INTO audit_log (action, table_name, record_id, created_at)
    -- VALUES (CONCAT('Deleted flash_sale_items for flash_sale_id: ', OLD.id), 'flash_sale', OLD.id, NOW());
END$$

DELIMITER ;


-- =============================================
-- TRIGGER: Auto update total_items when inserting flash_sale_item
-- =============================================

DROP TRIGGER IF EXISTS after_insert_flash_sale_item_update_total;

DELIMITER $$

CREATE TRIGGER after_insert_flash_sale_item_update_total
AFTER INSERT ON flash_sale_item
FOR EACH ROW
BEGIN
    -- Tự động tăng total_items trong flash_sale
    UPDATE flash_sale 
    SET total_items = total_items + 1
    WHERE id = NEW.flash_sale_id;
END$$

DELIMITER ;


-- =============================================
-- TRIGGER: Auto update total_items when deleting flash_sale_item
-- =============================================

DROP TRIGGER IF EXISTS after_delete_flash_sale_item_update_total;

DELIMITER $$

CREATE TRIGGER after_delete_flash_sale_item_update_total
AFTER DELETE ON flash_sale_item
FOR EACH ROW
BEGIN
    -- Tự động giảm total_items trong flash_sale
    UPDATE flash_sale 
    SET total_items = GREATEST(0, total_items - 1)
    WHERE id = OLD.flash_sale_id;
END$$

DELIMITER ;


-- =============================================
-- VERIFICATION - Kiểm tra triggers đã tạo
-- =============================================

SELECT 
    TRIGGER_NAME,
    EVENT_MANIPULATION AS 'Event',
    EVENT_OBJECT_TABLE AS 'Table',
    ACTION_TIMING AS 'Timing',
    CREATED
FROM INFORMATION_SCHEMA.TRIGGERS
WHERE TRIGGER_SCHEMA = 'shoe_shop_basic'
  AND (TRIGGER_NAME LIKE '%flash_sale%')
ORDER BY EVENT_OBJECT_TABLE, ACTION_TIMING;


-- =============================================
-- NOTES FOR APPLICATION LAYER
-- =============================================

/*
ĐỐI VỚI VIỆC XÓA SẢN PHẨM KHỎI FLASH SALE (Trong giao diện quản lý):
- KHÔNG dùng trigger cho trường hợp này
- Xử lý ở tầng Service/Controller với query:

Java/JPA:
    @Transactional
    public void removeProductFromFlashSale(Long flashSaleId, Long productDetailId) {
        flashSaleItemRepository.deleteByFlashSaleIdAndProductDetailId(flashSaleId, productDetailId);
    }

SQL trực tiếp:
    DELETE FROM flash_sale_item 
    WHERE flash_sale_id = ? AND product_detail_id = ?;

→ Trigger after_delete_flash_sale_item_update_total sẽ tự động giảm total_items
*/


-- =============================================
-- TEST CASES
-- =============================================

/*
-- Test 1: Xóa toàn bộ flash sale
DELETE FROM flash_sale WHERE id = 1;
-- → Trigger before_delete_flash_sale_cascade sẽ xóa tất cả flash_sale_item

-- Test 2: Xóa 1 sản phẩm khỏi flash sale cụ thể (trong controller)
DELETE FROM flash_sale_item 
WHERE flash_sale_id = 1 AND product_detail_id = 123;
-- → Trigger after_delete_flash_sale_item_update_total sẽ giảm total_items

-- Test 3: Thêm sản phẩm vào flash sale
INSERT INTO flash_sale_item (flash_sale_id, product_detail_id, original_price, flash_sale_price)
VALUES (1, 123, 500000, 350000);
-- → Trigger after_insert_flash_sale_item_update_total sẽ tăng total_items
*/


-- =============================================
-- ROLLBACK - Xóa tất cả triggers nếu cần
-- =============================================

/*
DROP TRIGGER IF EXISTS before_delete_flash_sale_cascade;
DROP TRIGGER IF EXISTS after_insert_flash_sale_item_update_total;
DROP TRIGGER IF EXISTS after_delete_flash_sale_item_update_total;
*/
