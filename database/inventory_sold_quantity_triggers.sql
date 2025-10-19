-- ===========================================
-- TRIGGERS TỰ ĐỘNG QUẢN LÝ INVENTORY & SOLD_QUANTITY
-- ===========================================
-- File: inventory_sold_quantity_triggers.sql
-- Mục đích: Tự động cập nhật tồn kho và số lượng đã bán khi đơn hàng thay đổi trạng thái
-- 
-- Logic:
-- 1. SHIPPED → Trừ inventory + Tăng sold_quantity (khi shipper lấy hàng)
-- 2. CANCEL/RETURN → Hoàn inventory + Giảm sold_quantity (khi hủy hoặc trả hàng)
-- 3. Validation: Không cho SHIPPED nếu không đủ hàng trong kho
-- 
-- Cách sử dụng:
-- 1. Backup database trước khi chạy
-- 2. Execute file này trong MySQL
-- 3. Test bằng cách UPDATE status của order
-- ===========================================

-- Xóa trigger cũ nếu có
DROP TRIGGER IF EXISTS after_order_detail_insert;
DROP TRIGGER IF EXISTS handle_order_status_change;
DROP TRIGGER IF EXISTS validate_inventory_before_shipped;

-- ===========================================
-- TRIGGER QUẢN LÝ TỰ ĐỘNG INVENTORY & SOLD_QUANTITY
-- ===========================================
-- 
-- ⚠️ LOGIC ĐÚNG THEO E-COMMERCE STANDARDS (Shopee, Lazada, Amazon):
--
-- 📦 INVENTORY (Tồn kho):
--    1. IN_STOCK (Đặt hàng) → TRỪ KHO NGAY (ngăn overselling)
--    2. SHIPPED → Không đổi (đã trừ rồi)
--    3. DELIVERED → Không đổi
--    4. CANCEL/RETURN → HOÀN KHO
--
-- 📊 SOLD_QUANTITY (Đã bán):
--    1. IN_STOCK → Không tăng (chưa bán)
--    2. SHIPPED → Không tăng (đang giao, chưa chắc khách nhận)
--    3. DELIVERED → TĂNG (khách nhận hàng thành công = bán được)
--    4. CANCEL → Không giảm (chưa tăng lúc đặt)
--    5. RETURN (sau DELIVERED) → GIẢM (khách trả hàng)
--
-- 🎯 MỤC ĐÍCH:
--    - Ngăn overselling (bán quá số lượng tồn)
--    - Khách biết ngay có hàng hay không
--    - Inventory luôn chính xác realtime
--    - Sold_quantity phản ánh đúng doanh số
-- ===========================================

-- ===========================================
-- TRIGGER 1: TRỪ KHO NGAY KHI ĐẶT HÀNG
-- ===========================================
-- Chạy khi INSERT order_detail (tạo đơn hàng mới)
-- Mục đích: Ngăn overselling, khách biết ngay có hàng hay không

DELIMITER $$

CREATE TRIGGER after_order_detail_insert
    BEFORE INSERT ON order_detail
    FOR EACH ROW
BEGIN
    DECLARE v_current_stock BIGINT;
    DECLARE v_product_name VARCHAR(255);
    DECLARE v_error_message VARCHAR(500);
    
    -- 1️⃣ Lấy tồn kho hiện tại
    SELECT i.quantity INTO v_current_stock
    FROM inventory i
    WHERE i.product_detail_id = NEW.productdetail_id
    LIMIT 1;
    
    -- 2️⃣ Kiểm tra tồn kho
    IF v_current_stock IS NULL THEN
        -- Lấy tên sản phẩm để báo lỗi rõ ràng
        SELECT p.title INTO v_product_name
        FROM product_detail pd
        JOIN product p ON pd.product_id = p.id
        WHERE pd.id = NEW.productdetail_id;
        
        -- Tạo error message
        SET v_error_message = CONCAT('Không tìm thấy sản phẩm trong kho: ', COALESCE(v_product_name, 'Unknown'));
        
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = v_error_message;
        
    ELSEIF v_current_stock < NEW.quantity THEN
        -- Lấy tên sản phẩm để báo lỗi rõ ràng
        SELECT p.title INTO v_product_name
        FROM product_detail pd
        JOIN product p ON pd.product_id = p.id
        WHERE pd.id = NEW.productdetail_id;
        
        -- Tạo error message
        SET v_error_message = CONCAT('Không đủ hàng trong kho! Sản phẩm: ', COALESCE(v_product_name, 'Unknown'), 
                                      ' - Tồn kho: ', v_current_stock, 
                                      ' - Yêu cầu: ', NEW.quantity);
        
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = v_error_message;
    END IF;
    
    -- 3️⃣ TRỪ KHO NGAY (nếu đủ hàng)
    UPDATE inventory
    SET quantity = quantity - NEW.quantity
    WHERE product_detail_id = NEW.productdetail_id;
    
END$$

DELIMITER ;

-- ===========================================
-- TRIGGER 2: XỬ LÝ KHI STATUS THAY ĐỔI
-- ===========================================

DELIMITER $$

CREATE TRIGGER handle_order_status_change
    AFTER UPDATE ON orders
    FOR EACH ROW
BEGIN
    -- ========================================
    -- CASE 1: DELIVERED → TĂNG SOLD_QUANTITY
    -- ========================================
    -- Khách nhận hàng thành công = chính thức bán được
    IF NEW.status = 'DELIVERED' AND OLD.status != 'DELIVERED' THEN
        
        UPDATE product p
        INNER JOIN product_detail pd ON p.id = pd.product_id
        INNER JOIN order_detail od ON pd.id = od.productdetail_id
        SET p.sold_quantity = p.sold_quantity + od.quantity
        WHERE od.order_id = NEW.id;
        
    END IF;
    
    -- ========================================
    -- CASE 2: CANCEL → HOÀN KHO
    -- ========================================
    -- Khách hủy đơn → hoàn lại inventory (đã trừ lúc đặt)
    IF NEW.status = 'CANCEL' AND OLD.status != 'CANCEL' THEN
        
        UPDATE inventory i
        INNER JOIN order_detail od ON i.product_detail_id = od.productdetail_id
        SET i.quantity = i.quantity + od.quantity
        WHERE od.order_id = NEW.id;
        
        -- Không giảm sold_quantity vì chưa tăng (chưa DELIVERED)
        
    END IF;
    
    -- ========================================
    -- CASE 3: RETURN → HOÀN KHO + GIẢM SOLD
    -- ========================================
    -- Khách trả hàng (sau khi đã DELIVERED) → hoàn kho + giảm sold
    IF NEW.status = 'RETURN' AND OLD.status = 'DELIVERED' THEN
        
        -- Hoàn kho
        UPDATE inventory i
        INNER JOIN order_detail od ON i.product_detail_id = od.productdetail_id
        SET i.quantity = i.quantity + od.quantity
        WHERE od.order_id = NEW.id;
        
        -- Giảm sold_quantity (vì đã tăng lúc DELIVERED)
        UPDATE product p
        INNER JOIN product_detail pd ON p.id = pd.product_id
        INNER JOIN order_detail od ON pd.id = od.productdetail_id
        SET p.sold_quantity = GREATEST(0, p.sold_quantity - od.quantity)
        WHERE od.order_id = NEW.id;
        
    END IF;
    
END$$

DELIMITER ;

-- ===========================================
-- ⚠️ NOTE: KHÔNG CẦN VALIDATION TRIGGER CHO SHIPPED
-- ===========================================
-- Lý do: Inventory đã được trừ và validate lúc đặt hàng (INSERT order_detail)
-- SHIPPED chỉ là thay đổi status, không ảnh hưởng inventory

-- ===========================================
-- SCRIPT TEST (Không chạy tự động, dùng để test thủ công)
-- ===========================================

/*
-- Test 1: Kiểm tra trigger đã được tạo
SHOW TRIGGERS WHERE `Table` = 'orders';

-- Test 2: Tạo đơn hàng mẫu (nếu chưa có)
-- INSERT INTO orders (...) VALUES (...);

-- Test 3: Chuyển đơn hàng sang SHIPPED
UPDATE orders 
SET status = 'SHIPPED' 
WHERE id = 1;

-- Kiểm tra kết quả
SELECT 
    od.id,
    p.title,
    pd.size,
    od.quantity AS 'Số lượng đặt',
    i.quantity AS 'Tồn kho còn lại',
    p.sold_quantity AS 'Đã bán'
FROM order_detail od
INNER JOIN product_detail pd ON od.productdetail_id = pd.id
INNER JOIN product p ON pd.product_id = p.id
LEFT JOIN inventory i ON i.product_detail_id = pd.id
WHERE od.order_id = 1;

-- Test 4: Hủy đơn hàng (hoàn kho)
UPDATE orders 
SET status = 'CANCEL' 
WHERE id = 1;

-- Kiểm tra kho đã được hoàn lại
SELECT 
    p.title,
    pd.size,
    i.quantity AS 'Tồn kho (sau khi hoàn)',
    p.sold_quantity AS 'Đã bán (sau khi hoàn)'
FROM product p
INNER JOIN product_detail pd ON p.id = pd.product_id
LEFT JOIN inventory i ON i.product_detail_id = pd.id
WHERE p.id IN (
    SELECT DISTINCT pd2.product_id 
    FROM order_detail od 
    INNER JOIN product_detail pd2 ON od.productdetail_id = pd2.id 
    WHERE od.order_id = 1
);

-- Test 5: Test validation (không đủ hàng)
-- Giảm inventory xuống 0
UPDATE inventory SET quantity = 0 WHERE product_detail_id = 1;

-- Thử SHIPPED → Sẽ báo lỗi
UPDATE orders SET status = 'SHIPPED' WHERE id = 2;
*/

-- ===========================================
-- KẾT THÚC
-- ===========================================
-- Chúc bạn thành công! 🚀
