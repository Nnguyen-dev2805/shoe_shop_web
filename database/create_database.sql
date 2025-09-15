create database shoe_store_basic
go 


--1. Role : Phan quyen , user nao chuc nang nao (khach, admin, shipper, ... )
CREATE TABLE Role (
    role_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL
);
go 

-- 2. User : Luu tai khoan , mat khau , thong tin va chuc vu 
CREATE TABLE [User](
    id INT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(100) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    address NVARCHAR(255),
    role_id INT NOT NULL,
    constraint fk_user_role FOREIGN KEY (role_id) REFERENCES Role(role_id)
);
go 


-- 3. Category : Mo ta ten loai giay (sneaker, sandal, ...)
CREATE TABLE Category (
    id INT IDENTITY(1,1) PRIMARY KEY,
    type NVARCHAR(50) NOT NULL,
    description NVARCHAR(255)
);

go 

-- 4. Product : Luu thong tin co ban cua san pham, tham chieu den catelory (biet loai giay)
CREATE TABLE Product (
    id INT IDENTITY(1,1) PRIMARY KEY,
    category_id INT NOT NULL,
    title NVARCHAR(100) NOT NULL,
    image NVARCHAR(255),
    description NVARCHAR(MAX),
    price DECIMAL(18,2) NOT NULL,
    voucher DECIMAL(18,2) DEFAULT 0, -- giam gia neu co 
    is_delete BIT DEFAULT 0, -- khi san pham het hoac xoa san pham , chi can bat bit len thay vi xoa truc tiep ttrong CSDL de truy van sau nay
    FOREIGN KEY (category_id) REFERENCES Category(id)
);


-- 5. Product Detail (siza , bien the) tham chieu den Product
CREATE TABLE ProductDetail (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    size NVARCHAR(20) NOT NULL, -- varchar cho linh hoat
    price_add DECIMAL(18,2) DEFAULT 0, -- tien them voi nhung doi giay lon, hoac giam voi con nit
    FOREIGN KEY (product_id) REFERENCES Product(id)
);


-- 6. Inventory : Hàng ton kho : se qui dinh theo size giay 
CREATE TABLE Inventory (
    product_id INT PRIMARY KEY,
    quantity INT NOT NULL,
    update_date DATETIME DEFAULT GETDATE(), -- ngay moi nhat cap nhat
    FOREIGN KEY (product_id) REFERENCES ProductDetail(id)
);


-- 7. Cart :Gio hang va user : Luu tong tien gio hang 
CREATE TABLE Cart (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    total_price DECIMAL(18,2) DEFAULT 0,
    created_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES [User](id)
);

--8. Cart Detail : Luu thoing tin chi tiet gio hang
CREATE TABLE CartDetail (
    id INT IDENTITY(1,1) PRIMARY KEY,
    cart_id INT NOT NULL,
    productdetail_id INT NOT NULL unique, -- khi them trung san pham thi tang quanity len
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES Cart(id),
    FOREIGN KEY (productdetail_id) REFERENCES ProductDetail(id)
);


--9. Adreess : Cho nguoiw dung chon tren web , dua tren API gg map, luu thong tin vao adrees de xac dinh
CREATE TABLE Address (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    address_line NVARCHAR(255) NOT NULL,
    district NVARCHAR(100),
    city NVARCHAR(100) NOT NULL,
    postal_code NVARCHAR(20),
    country NVARCHAR(50) DEFAULT 'Vietnam',
    FOREIGN KEY (user_id) REFERENCES [User](id)
);


--9. Order : Luu thong tin doon hang, tham chieu den user va dia chi giao hang 
CREATE TABLE [Order] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    delivery_address_id INT NOT NULL,
    shipment_id INT NULL,
    total_price DECIMAL(18,2) NOT NULL,
    status NVARCHAR(50) DEFAULT 'pending', -- pending, shipped, delivered
    pay_option NVARCHAR(50), -- hinh thuc thanh toan
    created_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES [User](id),
    FOREIGN KEY (delivery_address_id) REFERENCES Address(id)
);


-- 10. OrderDetail : Chi tiet trong don hang san pham
CREATE TABLE OrderDetail (
    id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    productdetail_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES [Order](id),
    FOREIGN KEY (productdetail_id) REFERENCES ProductDetail(id)
);


-- 11. Shipment : 
CREATE TABLE Shipment (
    id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    shipper_id INT NOT NULL, -- user có role = shipper
    shipment_address_id INT NULL, -- optional nếu muốn giao khác địa chỉ Order
    status NVARCHAR(50) DEFAULT 'shipping', -- shipping, delivered
    update_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES [Order](id),
    FOREIGN KEY (shipper_id) REFERENCES [User](id),
    FOREIGN KEY (shipment_address_id) REFERENCES Address(id)
);


--12. Rating 
CREATE TABLE Rating (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    star INT CHECK (star >= 1 AND star <= 5),
    description NVARCHAR(MAX),
    created_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (product_id) REFERENCES Product(id),
    FOREIGN KEY (user_id) REFERENCES [User](id)
);
