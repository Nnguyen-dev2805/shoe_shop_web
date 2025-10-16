

-- trigger giam so luong ton kho khi dat hang va xoa san pham trong cart
DELIMITER $$

CREATE TRIGGER after_order_detail_insert
    BEFORE INSERT ON order_detail
    FOR EACH ROW
BEGIN
    DECLARE v_current_stock INT;

    -- 1️⃣ Lấy số lượng tồn hiện tại của sản phẩm
    SELECT quantity INTO v_current_stock
    FROM inventory
    WHERE product_detail_id = NEW.productdetail_id
        LIMIT 1;

    -- 2️⃣ Kiểm tra xem có tồn đủ không
    IF v_current_stock IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không tìm thấy sản phẩm trong kho (inventory)';
    ELSEIF v_current_stock < NEW.quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không đủ hàng trong kho để đặt đơn hàng này';
END IF;

-- 3️⃣ Nếu đủ, trừ số lượng ngay
UPDATE inventory
SET quantity = quantity - NEW.quantity
WHERE product_detail_id = NEW.productdetail_id;

END$$

DELIMITER ;



-- cap nhat lai inventory khi cancel don hang
DELIMITER $$

CREATE TRIGGER after_order_status_cancel
    AFTER UPDATE ON orders
    FOR EACH ROW
BEGIN
    -- Kiểm tra xem đơn hàng vừa được cập nhật sang trạng thái 'cancel'
    IF NEW.status = 'cancel' AND OLD.status <> 'cancel' THEN

        -- Cộng lại tồn kho theo từng sản phẩm trong đơn
    UPDATE inventory i
        JOIN order_detail od ON i.product_detail_id = od.productdetail_id
        SET i.quantity = i.quantity + od.quantity
    WHERE od.order_id = NEW.id;

END IF;
END$$

DELIMITER ;