-- ===========================================
-- TRIGGER QUẢN LÝ TỰ ĐỘNG INVENTORY & SOLD_QUANTITY
-- ===========================================
-- Logic:
-- 1. Khi status chuyển sang SHIPPED → Trừ inventory + Tăng sold_quantity
-- 2. Khi status chuyển sang CANCEL/RETURN → Hoàn inventory + Giảm sold_quantity
-- 3. Tự động cập nhật realtime, không cần code Java
-- ===========================================

DELIMITER $$

DROP TRIGGER IF EXISTS handle_order_status_change$$

CREATE TRIGGER handle_order_status_change
    AFTER UPDATE ON orders
    FOR EACH ROW
BEGIN
    -- ========================================
    -- CASE 1: Chuyển sang SHIPPED
    -- → Trừ inventory, Tăng sold_quantity
    -- ========================================
    IF NEW.status = 'SHIPPED' AND OLD.status != 'SHIPPED' THEN
        
        -- Trừ inventory cho từng sản phẩm trong đơn
        UPDATE inventory i
        INNER JOIN order_detail od ON i.product_detail_id = od.productdetail_id
        SET i.quantity = i.quantity - od.quantity
        WHERE od.order_id = NEW.id;
        
        -- Tăng sold_quantity trong product
        UPDATE product p
        INNER JOIN product_detail pd ON p.id = pd.product_id
        INNER JOIN order_detail od ON pd.id = od.productdetail_id
        SET p.sold_quantity = p.sold_quantity + od.quantity
        WHERE od.order_id = NEW.id;
        
    END IF;
    
    -- ========================================
    -- CASE 2: Chuyển sang CANCEL hoặc RETURN
    -- → Hoàn lại inventory, Giảm sold_quantity
    -- ========================================
    IF (NEW.status = 'CANCEL' OR NEW.status = 'RETURN') 
       AND (OLD.status = 'SHIPPED' OR OLD.status = 'DELIVERED') THEN
        
        -- Hoàn lại inventory
        UPDATE inventory i
        INNER JOIN order_detail od ON i.product_detail_id = od.productdetail_id
        SET i.quantity = i.quantity + od.quantity
        WHERE od.order_id = NEW.id;
        
        -- Giảm sold_quantity trong product
        UPDATE product p
        INNER JOIN product_detail pd ON p.id = pd.product_id
        INNER JOIN order_detail od ON pd.id = od.productdetail_id
        SET p.sold_quantity = GREATEST(0, p.sold_quantity - od.quantity)
        WHERE od.order_id = NEW.id;
        
    END IF;
    
END$$

DELIMITER ;

-- ===========================================
-- VALIDATION TRIGGER: Kiểm tra inventory trước khi SHIPPED
-- ===========================================
-- Ngăn không cho chuyển sang SHIPPED nếu không đủ hàng

DELIMITER $$

DROP TRIGGER IF EXISTS validate_inventory_before_shipped$$

CREATE TRIGGER validate_inventory_before_shipped
    BEFORE UPDATE ON orders
    FOR EACH ROW
BEGIN
    DECLARE v_product_name VARCHAR(255);
    DECLARE v_current_stock BIGINT;
    DECLARE v_required_quantity INT;
    
    -- Chỉ kiểm tra khi chuyển sang SHIPPED
    IF NEW.status = 'SHIPPED' AND OLD.status != 'SHIPPED' THEN
        
        -- Kiểm tra từng sản phẩm trong đơn hàng
        SELECT 
            p.title,
            i.quantity,
            od.quantity
        INTO 
            v_product_name,
            v_current_stock,
            v_required_quantity
        FROM order_detail od
        INNER JOIN product_detail pd ON od.productdetail_id = pd.id
        INNER JOIN product p ON pd.product_id = p.id
        LEFT JOIN inventory i ON i.product_detail_id = pd.id
        WHERE od.order_id = NEW.id
          AND (i.quantity IS NULL OR i.quantity < od.quantity)
        LIMIT 1;
        
        -- Nếu không đủ hàng → Báo lỗi
        IF v_product_name IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = CONCAT('Không đủ hàng trong kho! Sản phẩm: ', v_product_name, 
                                      ' - Tồn kho: ', COALESCE(v_current_stock, 0), 
                                      ' - Yêu cầu: ', v_required_quantity);
        END IF;
        
    END IF;
    
END$$

DELIMITER ;