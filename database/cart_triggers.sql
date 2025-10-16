-- =============================================
-- Cart Detail Triggers for Auto-updating Cart Total Price
-- =============================================

USE shoe_shop_basic;


-- trigger insert
DELIMITER $$
    CREATE TRIGGER cart_detail_after_insert
        AFTER INSERT ON cart_detail
        FOR EACH ROW
    BEGIN
        UPDATE cart
        SET total_price = total_price + (NEW.quantity * NEW.price)
        WHERE id = NEW.cart_id;

        END$$
DELIMITER ;


DELIMITER $$
        CREATE TRIGGER cart_detail_after_delete
            AFTER DELETE ON cart_detail
            FOR EACH ROW
        BEGIN
            UPDATE cart
            SET total_price = total_price - (OLD.quantity * OLD.price)
            WHERE id = OLD.cart_id;
        END$$
DELIMITER ;


DELIMITER $$
            CREATE TRIGGER cart_detail_after_update
                AFTER UPDATE ON cart_detail
                FOR EACH ROW
            BEGIN
                UPDATE cart
                SET total_price = total_price
                                      - (OLD.quantity * OLD.price)
                    + (NEW.quantity * NEW.price)
                WHERE id = NEW.cart_id;
                END$$
DELIMITER ;