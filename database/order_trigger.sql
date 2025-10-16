

-- trigger giam so luong ton kho khi dat hang va xoa san pham trong cart
DELIMITER $$

CREATE TRIGGER after_order_detail_insert
AFTER INSERT ON order_detail
FOR EACH ROW
BEGIN
    DECLARE v_customer_id BIGINT;
    
    -- 1. Giảm số lượng trong inventory (product_detail)
    UPDATE inventory
    SET quantity = quantity - NEW.quantity
    WHERE product_detail_id = NEW.productdetail_id;
    
    -- Optional: Đảm bảo quantity không âm
    UPDATE product_detail
    SET quantity = 0
    WHERE id = NEW.productdetail_id 
      AND quantity < 0;
    
END$$

DELIMITER ;