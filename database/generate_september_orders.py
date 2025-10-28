import random
from datetime import datetime, timedelta

# ==========================================
# SCRIPT GENERATE ĐƠN HÀNG THÁNG 9/2025
# ==========================================

# Product mapping (ID -> [price, cost_price])
products = {
    1: [1890000, 1323000], 2: [2350000, 1645000], 3: [1750000, 1225000],
    4: [1590000, 1113000], 5: [1450000, 1015000], 6: [1690000, 1183000],
    7: [2890000, 2023000], 8: [3250000, 2275000], 9: [3150000, 2205000],
    10: [4590000, 3213000], 11: [4150000, 2905000], 12: [3790000, 2653000],
    13: [590000, 413000], 14: [750000, 525000], 15: [1150000, 805000],
    16: [890000, 623000], 17: [1290000, 903000], 18: [950000, 665000],
    19: [1150000, 805000], 20: [990000, 693000]
}

# ProductDetail mapping (Product ID -> list of PD IDs for sizes 38-42)
product_details = {
    1: [1, 2, 3, 4, 5], 2: [9, 10, 11, 12, 13], 3: [17, 18, 19, 20, 21],
    4: [25, 26, 27, 28, 29], 5: [33, 34, 35, 36, 37], 6: [41, 42, 43, 44, 45],
    7: [49, 50, 51, 52, 53], 8: [57, 58, 59, 60, 61], 9: [65, 66, 67, 68, 69],
    10: [73, 74, 75, 76, 77], 11: [81, 82, 83, 84, 85], 12: [89, 90, 91, 92, 93],
    13: [97, 98, 99, 100, 101], 14: [105, 106, 107, 108, 109], 15: [113, 114, 115, 116, 117],
    16: [121, 122, 123, 124, 125], 17: [129, 130, 131, 132, 133], 18: [137, 138, 139, 140, 141],
    19: [145, 146, 147, 148, 149], 20: [153, 154, 155, 156, 157]
}

users = list(range(3, 19))  # User IDs: 3-18 (16 users)
pay_options = ['COD', 'PAYOS']

# Generate 120 orders
output = []
order_counter = 1
start_date = datetime(2025, 9, 1)

for day in range(30):  # 30 days in September
    current_date = start_date + timedelta(days=day)
    # Random 2-7 orders per day
    num_orders = random.randint(2, 7)
    
    for order_in_day in range(num_orders):
        # Random user
        user_id = random.choice(users)
        
        # Random time in day
        hour = random.randint(8, 20)
        minute = random.randint(0, 59)
        order_time = current_date.replace(hour=hour, minute=minute)
        
        # Random pay option
        pay_option = random.choice(pay_options)
        
        # Random 1-3 products
        num_items = random.randint(1, 3)
        selected_products = random.sample(list(products.keys()), num_items)
        
        # Calculate order details
        order_details = []
        total_price = 0
        total_profit = 0
        
        for product_id in selected_products:
            # Random product detail (size)
            pd_id = random.choice(product_details[product_id])
            price = products[product_id][0]
            cost = products[product_id][1]
            
            # Random quantity 1-5
            qty = random.randint(1, 5)
            
            item_total = price * qty
            item_cost = cost * qty
            item_profit = item_total - item_cost
            
            total_price += item_total
            total_profit += item_profit
            
            order_details.append({
                'pd_id': pd_id,
                'qty': qty,
                'price': price,
                'cost': cost,
                'profit': item_profit
            })
        
        # Generate SQL
        order_sql = f"-- Đơn {order_counter}: {order_time.strftime('%d/09')} - User {user_id}\n"
        order_sql += f"INSERT INTO orders (customer_id, total_price, created_date, status, pay_option, delivery_address_id) VALUES\n"
        order_sql += f"({user_id}, {total_price}, '{order_time.strftime('%Y-%m-%d %H:%M:00')}', 'DELIVERED', '{pay_option}', 1);\n"
        order_sql += f"SET @o{order_counter} = LAST_INSERT_ID();\n"
        order_sql += f"INSERT INTO order_detail (order_id, productdetail_id, quantity, price, cost_price_at_sale, profit) VALUES\n"
        
        detail_lines = []
        for detail in order_details:
            detail_lines.append(f"(@o{order_counter}, {detail['pd_id']}, {detail['qty']}, {detail['price']}, {detail['cost']}, {detail['profit']})")
        
        order_sql += ",\n".join(detail_lines) + ";\n\n"
        output.append(order_sql)
        
        order_counter += 1
        
        # Stop if reached 120 orders
        if order_counter > 120:
            break
    
    if order_counter > 120:
        break

# Write to file
with open('d:/CODE/WEB/shoe_shop_web/database/september_orders_generated.sql', 'w', encoding='utf-8') as f:
    f.write("-- ==========================================\n")
    f.write("-- 🛒 ĐƠN HÀNG THÁNG 9/2025 (AUTO-GENERATED)\n")
    f.write("-- 120 đơn rải đều 30 ngày\n")
    f.write("-- ==========================================\n\n")
    f.write("".join(output))
    f.write("-- ==========================================\n")
    f.write("-- ✅ TỔNG KẾT\n")
    f.write(f"-- Đã tạo {order_counter-1} đơn hàng cho tháng 9/2025\n")
    f.write("-- ==========================================\n")

print(f"✅ Đã generate {order_counter-1} đơn hàng!")
print(f"📁 File: d:/CODE/WEB/shoe_shop_web/database/september_orders_generated.sql")
print("\n🚀 Cách sử dụng:")
print("1. Chạy file: python generate_september_orders.py")
print("2. Import SQL: september_2025_data.sql (nhập kho)")
print("3. Import SQL: september_orders_generated.sql (đơn hàng)")
