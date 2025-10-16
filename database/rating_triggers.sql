-- Triggers để tự động cập nhật thống kê đánh giá sản phẩm
-- Khi có thay đổi trong bảng rating, tự động cập nhật product

-- 1. Trigger khi INSERT rating mới
DELIMITER $$

CREATE TRIGGER tr_rating_insert
AFTER INSERT ON rating
FOR EACH ROW
BEGIN
    UPDATE product 
    SET 
        total_reviewers = total_reviewers + 1,
        total_stars = total_stars + NEW.star,
        average_stars = CASE 
            WHEN (total_reviewers + 1) > 0 THEN (total_stars + NEW.star) / (total_reviewers + 1)
            ELSE 0.0
        END
    WHERE id = NEW.product_id;
END$$


DELIMITER ;