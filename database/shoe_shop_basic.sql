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
