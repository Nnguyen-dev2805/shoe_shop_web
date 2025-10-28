# Database Initialization Scripts

## 📦 Tổng quan
Folder này chứa các script SQL để khởi tạo dữ liệu cho Shoe Shop.

## 📁 Files

### `products_60.sql`
- **Mô tả**: Script tạo 60 sản phẩm với 10 danh mục
- **Sản phẩm**: 60 products × 8 sizes = 480 product details
- **Danh mục**: 10 categories (Thể Thao, Sneaker, Bóng Đá, Bóng Rổ, Sandals, Chạy Bộ, Đi Bộ, Cao Gót, Lười, Boots)
- **Brands**: 10 brands (Nike, Adidas, Puma, Converse, Vans, New Balance, Reebok, Fila, Under Armour, Skechers)

## 🚀 Cách sử dụng

### Option 1: Import thông qua MySQL Workbench
```sql
-- 1. Backup database hiện tại (nếu cần)
mysqldump -u root -p shoe_shop_db > backup.sql

-- 2. Mở MySQL Workbench
-- 3. File → Open SQL Script
-- 4. Chọn file products_60.sql
-- 5. Execute (Ctrl + Shift + Enter)
```

### Option 2: Import qua command line
```bash
mysql -u root -p shoe_shop_db < products_60.sql
```

### Option 3: Chạy ApplicationInitConfig
1. Comment dòng check trong ApplicationInitConfig.java:
```java
// if (roleRepository.count() > 0) {
//     return;
// }
```

2. Update method initCategories() với 10 categories:
```java
private void initCategories() {
    String[][] categories = {
        {"Giày Thể Thao", "Giày dành cho hoạt động thể thao"},
        {"Giày Sneaker", "Giày sneaker phong cách streetwear"},
        {"Giày Bóng Đá", "Giày chuyên dụng cho bóng đá"},
        {"Giày Bóng Rổ", "Giày thể thao chuyên cho bóng rổ"},
        {"Sandals & Dép", "Dép, sandals thoáng mát"},
        {"Giày Chạy Bộ", "Giày chuyên dụng chạy bộ, marathon"},
        {"Giày Đi Bộ", "Giày đi bộ thoải mái"},
        {"Giày Cao Gót", "Giày cao gót nữ thanh lịch"},
        {"Giày Lười", "Giày lười tiện lợi"},
        {"Giày Boots", "Giày boots thời trang thu đông"}
    };
    // ... rest of code
}
```

3. Update method initProducts() để tạo 60 products (thay vì 15)

## 📊 Cấu trúc dữ liệu

### Categories (10)
1. Giày Thể Thao - 6 products
2. Giày Sneaker - 8 products
3. Giày Bóng Đá - 6 products
4. Giày Bóng Rổ - 6 products
5. Sandals & Dép - 6 products
6. Giày Chạy Bộ - 8 products
7. Giày Đi Bộ - 6 products
8. Giày Cao Gót - 4 products
9. Giày Lười - 5 products
10. Giày Boots - 5 products

### Price Range
- **Dép/Sandals**: 550k - 1,150k
- **Giày Lười**: 990k - 1,650k
- **Giày Cao Gót**: 1,150k - 1,750k
- **Giày Thể Thao**: 1,650k - 2,890k
- **Giày Sneaker**: 1,290k - 2,590k
- **Giày Đi Bộ**: 1,650k - 3,490k
- **Giày Boots**: 1,990k - 3,250k
- **Giày Bóng Đá**: 2,750k - 3,450k
- **Giày Chạy Bộ**: 2,750k - 5,290k
- **Giày Bóng Rổ**: 3,890k - 5,290k

### Size & Price Add
- Size 38-40: +0đ
- Size 41-42: +50,000đ
- Size 43-44: +100,000đ
- Size 45: +150,000đ

## ⚠️ Lưu ý

1. **Backup trước khi import**: Luôn backup database trước khi chạy script
2. **Foreign Key**: Script tự động xử lý foreign key constraints
3. **Auto Increment**: IDs được set cố định để đảm bảo tính nhất quán
4. **Images**: Sử dụng Cloudinary URLs - đảm bảo images còn available

## 🔧 Troubleshooting

### Lỗi: Duplicate entry
```sql
-- Xóa data cũ trước
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE product_detail;
TRUNCATE TABLE product;
TRUNCATE TABLE category;
TRUNCATE TABLE brand;
SET FOREIGN_KEY_CHECKS = 1;
```

### Lỗi: Foreign key constraint
- Đảm bảo roles, categories, brands đã được tạo trước
- Check ApplicationInitConfig đã run initRoles(), initCategories(), initBrands()

## 📝 TODO
- [ ] Add more product images
- [ ] Create sample ratings data
- [ ] Create sample orders data
- [ ] Add inventory data script
