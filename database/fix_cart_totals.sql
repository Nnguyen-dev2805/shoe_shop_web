-- =============================================
-- Fix existing cart total_price values
-- =============================================

USE shoe_shop_basic;

-- Update all existing cart total_price based on current cart_detail data
UPDATE cart c
SET total_price = (
    SELECT COALESCE(SUM(cd.quantity * cd.price), 0)
    FROM cart_detail cd 
    WHERE cd.cart_id = c.id
);

-- Show results
SELECT 
    c.id as cart_id,
    c.user_id,
    c.total_price as calculated_total,
    COUNT(cd.id) as item_count,
    SUM(cd.quantity) as total_quantity
FROM cart c
LEFT JOIN cart_detail cd ON c.id = cd.cart_id
GROUP BY c.id, c.user_id, c.total_price
ORDER BY c.id;

-- Verify calculation
SELECT 
    cd.cart_id,
    SUM(cd.quantity * cd.price) as manual_total,
    c.total_price as stored_total,
    CASE 
        WHEN SUM(cd.quantity * cd.price) = c.total_price THEN 'CORRECT'
        ELSE 'MISMATCH'
    END as status
FROM cart_detail cd
JOIN cart c ON cd.cart_id = c.id
GROUP BY cd.cart_id, c.total_price;
