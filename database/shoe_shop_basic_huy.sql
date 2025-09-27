drop database shoe_shop_basic;
CREATE DATABASE shoe_shop_basic;
USE shoe_shop_basic;

-- 1. roles
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. users
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

-- Dữ liệu mẫu
INSERT INTO roles (role_name) VALUES ('admin');
INSERT INTO roles (role_name) VALUES ('shipper');
INSERT INTO roles (role_name) VALUES ('user');
INSERT INTO roles (role_name) VALUES ('manager');

INSERT INTO users (email, password, full_name, address, phone, role_id)
VALUES (
    'huy211020055@gmail.com',
    '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm',
    'Quang Huy',
    'HCM City',
    '0918924918',
    2
);

INSERT INTO users (email, password, full_name, address, phone, role_id)
VALUES (
    'tnhatnguyen.dev2805@gmail.com',
    '$2a$10$Hb8AASrv81xAk4CiXMlX0ebTlS1NMhUaTnUFaf.CnHUQcVHv9rgLm',
    'Nhất Nguyên',
    'HCM City',
    '0123456789',
    1
);

-- 3. category
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 

-- 4. brand
CREATE TABLE brand (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. product
CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    brand_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    image VARCHAR(255),
    description TEXT,
    price DECIMAL(18,2) NOT NULL,
    voucher DECIMAL(18,2) DEFAULT 0,
    is_delete BOOLEAN DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (brand_id) REFERENCES brand(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. product_detail
CREATE TABLE product_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    size VARCHAR(20) NOT NULL,
    price_add DECIMAL(18,2) DEFAULT 0,
    quantity INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. cart
CREATE TABLE cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_price DECIMAL(18,2) DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. cart_detail
CREATE TABLE cart_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    productdetail_id INT NOT NULL UNIQUE,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES cart(id),
    FOREIGN KEY (productdetail_id) REFERENCES product_detail(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. address
CREATE TABLE address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    address_line VARCHAR(255) NOT NULL,
    district VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(50) DEFAULT 'VietNam'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. user_address
CREATE TABLE user_address (
    user_id INT NOT NULL,
    address_id INT NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id, address_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES address(id)
);

-- 11. orders
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    delivery_address_id INT NOT NULL,
    total_price DECIMAL(18,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    pay_option VARCHAR(50) NOT NULL DEFAULT 'tiền mặt',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (delivery_address_id) REFERENCES address(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. order_detail
CREATE TABLE order_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    productdetail_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 1),
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (productdetail_id) REFERENCES product_detail(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng shipping_company
CREATE TABLE IF NOT EXISTS shipping_company (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    hotline VARCHAR(50),
    email VARCHAR(100),
    address VARCHAR(255),
    website VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. shipment
CREATE TABLE IF NOT EXISTS shipment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    shipping_company_id INT NOT NULL,
    shipper_id INT NOT NULL,
    shipment_address_id INT NULL,
    status VARCHAR(50) DEFAULT 'shipping',
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (shipping_company_id) REFERENCES shipping_company(id),
    FOREIGN KEY (shipper_id) REFERENCES users(id),
    FOREIGN KEY (shipment_address_id) REFERENCES address(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. rating
CREATE TABLE rating (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    star INT CHECK (star >= 1 AND star <= 5),
    description TEXT,
    image VARCHAR(255),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. discount
CREATE TABLE discount (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 1000,
    discount_percent DECIMAL(5,4) NOT NULL CHECK (discount_percent >= 0 AND discount_percent <= 1),
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'COMING', 'EXPIRED')),
    min_order_value DECIMAL(18,2),
    created_date DATE NOT NULL DEFAULT (CURDATE()),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_delete BOOLEAN NOT NULL DEFAULT FALSE,
    created_by INT,
    updated_by INT,
    updated_date DATE,
    CHECK (end_date > start_date),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16. discount_usage (track ai đã dùng discount nào)
CREATE TABLE discount_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    discount_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    order_id INT,
    used_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (discount_id) REFERENCES discount(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


