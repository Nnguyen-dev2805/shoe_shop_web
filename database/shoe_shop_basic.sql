create database shoe_shop_basic
go 

use shoe_shop_basic
go

-- 1. Role : Phân quyền , user nào chức năng nào (khách, admin, shipper, ... )
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

go

-- 2. User : Lưu tài khoản , mật khẩu , thông tin và chức vụ 
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(255),
    role_id INT NOT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

go

INSERT INTO roles (role_name) VALUES ('admin');
INSERT INTO users (email, password, full_name, address, phone, role_id)
VALUES ('tnhatnguyen.dev2805@gmail.com',
        '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm',
        'Nhất Nguyên',
        'HCM City',
        '0123456789',
        1);
        
-------------------------------


-- 3. Category : Mô tả loại giày
CREATE TABLE Category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 

-- 4. Product
CREATE TABLE Product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    brand_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    image VARCHAR(255),
    description TEXT,
    price DECIMAL(18,2) NOT NULL,
    voucher DECIMAL(18,2) DEFAULT 0,
    is_delete BOOLEAN DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES Category(id),
    FOREIGN KEY (brand_id) REFERENCES Brand(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 5. Brand
CREATE TABLE Brand (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- Mã thương hiệu
    name VARCHAR(100) NOT NULL UNIQUE         -- Tên thương hiệu
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 6. Product Detail
CREATE TABLE ProductDetail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    size VARCHAR(20) NOT NULL,
    price_add DECIMAL(18,2) DEFAULT 0,
    quantity INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES Product(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Cart
CREATE TABLE Cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_price DECIMAL(18,2) DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Cart Detail
CREATE TABLE CartDetail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    productdetail_id INT NOT NULL UNIQUE,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES Cart(id),
    FOREIGN KEY (productdetail_id) REFERENCES ProductDetail(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Address
CREATE TABLE Address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    address_line VARCHAR(255) NOT NULL,
    district VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(50) DEFAULT 'VietNam'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. User_Address
CREATE TABLE User_Address (
    user_id INT NOT NULL,
    address_id INT NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT 0,  -- 0 = không mặc định, 1 = mặc định
    PRIMARY KEY (user_id, address_id),      -- khóa chính kép
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES Address(id)
);

-- 11. Order
CREATE TABLE `Order` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    delivery_address_id INT NOT NULL,
    total_price DECIMAL(18,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    pay_option VARCHAR(50) NOT NULL default 'tiền mặt',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (delivery_address_id) REFERENCES Address(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. Order Detail
CREATE TABLE OrderDetail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    productdetail_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 1),
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `Order`(id),
    FOREIGN KEY (productdetail_id) REFERENCES ProductDetail(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. Shipment
CREATE TABLE Shipment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    shipper_id INT NOT NULL,
    shipment_address_id INT NULL, -- địa chỉ giao hàng khác với Order
    status VARCHAR(50) DEFAULT 'shipping',
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES `Order`(id),
    FOREIGN KEY (shipper_id) REFERENCES users(id),
    FOREIGN KEY (shipment_address_id) REFERENCES Address(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. Rating
CREATE TABLE Rating (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    star INT CHECK (star >= 1 AND star <= 5),
    description TEXT,
    image varchar(255),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES Product(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;







