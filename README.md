# DeeG Shoe Shop - Website Bán Giày Trực Tuyến

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

## Giới Thiệu

DeeG Shoe Shop là website bán và quản lý cửa hàng giày dép trực tuyến, được phát triển bằng Spring Boot 3.5.6 và Java 21. Website cung cấp đầy đủ tính năng mua sắm trực tuyến với tích hợp thanh toán online, AI chatbot hỗ trợ khách hàng, chương trình Flash Sale, hệ thống voucher giảm giá, và tự động tính phí vận chuyển dựa trên khoảng cách GPS.

### Mục Tiêu Dự Án

- Xây dựng website bán hàng giày dép trực tuyến đầy đủ tính năng
- Tích hợp các công nghệ hiện đại: AI, cổng thanh toán, lưu trữ đám mây, giao tiếp realtime
- Hỗ trợ đa vai trò người dùng với phân quyền chi tiết (Quản trị viên, Quản lý, Shipper, Khách hàng)
- Tối ưu trải nghiệm mua sắm với giao diện responsive và thân thiện
- Sẵn sàng triển khai production với Docker, CI/CD

## Mục Lục

- [Giới Thiệu](#giới-thiệu)
- [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
- [Tính Năng Nổi Bật](#tính-năng-nổi-bật)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [Database Schema](#database-schema)
- [Hướng Dẫn Cài Đặt](#hướng-dẫn-cài-đặt)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Contributors](#contributors)
- [License](#license)

---

## Công Nghệ Sử Dụng

### Backend Framework & Công Nghệ Nền Tảng

**Hệ sinh thái Spring Framework:**
- Spring Boot 3.5.6 - Framework ứng dụng chính
- Spring Data JPA - Thao tác cơ sở dữ liệu và ORM
- Spring Security 6 - Xác thực và phân quyền
- Spring Security OAuth2 Client - Tích hợp đăng nhập Google OAuth2
- Spring Boot Actuator - Giám sát sức khỏe hệ thống và metrics
- Spring WebSocket - Giao tiếp hai chiều realtime
- Spring Scheduler - Tác vụ tự động chạy nền
- Spring Boot Starter Mail - Dịch vụ gửi email
- Spring Boot DevTools - Hỗ trợ phát triển (hot reload)

**Cơ Sở Dữ Liệu & Persistence:**
- MySQL 8.0 - Hệ quản trị cơ sở dữ liệu quan hệ
- Hibernate ORM - Triển khai JPA
- HikariCP - Connection pooling hiệu năng cao (mặc định)
- Spring Data JPA Repositories - Tầng truy cập dữ liệu

**Bảo Mật & Xác Thực:**
- BCrypt Password Encoder - Mã hóa mật khẩu an toàn
- Google OAuth2 - Đăng nhập mạng xã hội
- Session-based Authentication - Xác thực dựa trên session
- Role-based Access Control (RBAC) - Phân quyền theo vai trò

**Validation & Xử Lý Dữ Liệu:**
- Jakarta Validation API 3.0.2 - Kiểm tra dữ liệu Bean
- Hibernate Validator 8.0.2.Final - Triển khai validation
- ModelMapper 3.2.0 - Chuyển đổi đối tượng (Entity ↔ DTO)

### Tích Hợp Bên Thứ Ba & APIs

**Cổng Thanh Toán:**
- PayOS Java SDK 2.0.1 - Cổng thanh toán Việt Nam
  - Thanh toán QR Code
  - Chuyển khoản ngân hàng
  - Xác minh thanh toán
  - Xử lý webhook

**Dịch Vụ Đám Mây:**
- Cloudinary HTTP44 1.36.0 - Quản lý ảnh trên cloud
  - Upload và lưu trữ ảnh
  - Tối ưu hóa ảnh tự động
  - Phân phối qua CDN
  - APIs biến đổi ảnh

**Trí Tuệ Nhân Tạo:**
- Google Gemini AI 1.21.0 - AI chatbot
  - Xử lý ngôn ngữ tự nhiên
  - Phản hồi theo ngữ cảnh
  - Tra cứu thông tin sản phẩm
  - Hỗ trợ theo dõi đơn hàng

**Bản Đồ & Định Vị:**
- Goong Maps API - Dịch vụ bản đồ Việt Nam
  - Tính khoảng cách (dựa trên GPS)
  - Geocoding và reverse geocoding
  - Ước tính phí vận chuyển
  - Xác thực địa chỉ

**Dịch Vụ Email:**
- Gmail SMTP với Spring Mail
  - Xác minh đăng ký tài khoản
  - Email đặt lại mật khẩu
  - Xác nhận đơn hàng
  - Email khuyến mãi

**Xử Lý File:**
- Apache POI 5.2.5 - Thao tác file Excel
- Apache POI OOXML 5.2.5 - Định dạng Excel hiện đại (.xlsx)
  - Xuất báo cáo đơn hàng
  - Import/Export sản phẩm
  - Báo cáo thống kê

**Tài Liệu API:**
- SpringDoc OpenAPI 2.3.0 - Đặc tả OpenAPI 3.0
  - Tích hợp Swagger UI
  - Test API tương tác
  - Tự động sinh tài liệu

### Công Nghệ Frontend

**Template Engine:**
- Thymeleaf - Template engine Java phía server
- Thymeleaf Extras Spring Security - Thẻ bảo mật và tiện ích

**UI Framework & Styling:**
- Bootstrap 5 - Framework CSS responsive
- Custom CSS - Styling riêng theo thương hiệu
- Font Awesome - Thư viện icon
- Lucide Icons - Bộ icon hiện đại

**JavaScript & Client-side:**
- Vanilla JavaScript - Ngôn ngữ kịch bản cốt lõi
- AJAX (XMLHttpRequest/Fetch API) - Yêu cầu bất đồng bộ
- WebSocket Client (STOMP.js) - Giao tiếp realtime
- jQuery - Thao tác DOM và AJAX

### Công Cụ Phát Triển & Build

**Build Tool:**
- Apache Maven 3.9+ - Quản lý dependencies và build tự động
- Maven Compiler Plugin - Biên dịch Java
- Spring Boot Maven Plugin - Đóng gói JAR thực thi

**Chất Lượng Code:**
- Lombok - Giảm code lặp lại
  - @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
  - @Getter, @Setter annotations
- Spring Boot Configuration Processor - Tạo metadata cấu hình

### DevOps & Triển Khai

**Containerization:**
- Docker - Nền tảng container
- Multi-stage Dockerfile - Build image tối ưu
- Docker Compose (tùy chọn) - Điều phối nhiều container

**Cloud Platform:**
- Render.com - Triển khai PaaS
  - Tự động scale
  - Triển khai không downtime
  - Kiểm tra sức khỏe
  - Quản lý biến môi trường

**CI/CD:**
- GitHub Actions - Luồng tự động hóa
  - Test tự động
  - Pipeline build và deploy
  - Kiểm tra chất lượng code

**Giám Sát:**
- Spring Boot Actuator endpoints
  - /actuator/health - Kiểm tra sức khỏe
  - /actuator/metrics - Số liệu ứng dụng
  - /actuator/info - Thông tin ứng dụng

---

## Tính Năng Nổi Bật

### Tính Năng Khách Hàng

**1. Quản Lý Tài Khoản & Xác Thực**
- Đăng ký tài khoản với xác minh email
- Đăng nhập bằng tên đăng nhập/mật khẩu (mã hóa BCrypt)
- Đăng nhập Google OAuth2 (đăng nhập 1 cú nhấp)
- Quên mật khẩu với token đặt lại qua email
- Quản lý thông tin cá nhân (tên, số điện thoại, ảnh đại diện)
- Hệ thống hạng thành viên: BẠC, VÀNG, BẠCH KIM, KIM CƯƠNG
- DeeG Xu (xu tích lũy): Kiếm và đổi xu (1 xu = 1 VND)
- Tích điểm thành viên (1 điểm cho mỗi 10,000 VND chi tiêu)

**2. Quản Lý Địa Chỉ Giao Hàng**
- Thêm/sửa/xóa nhiều địa chỉ giao hàng
- Đặt địa chỉ mặc định
- Tích hợp Goong Maps để chọn vị trí
- Tự động điền địa chỉ từ tọa độ
- Hiển thị bản đồ xem trước cho địa chỉ
- Tính toán khoảng cách từ kho hàng đến địa chỉ

**3. Danh Mục & Xem Sản Phẩm**
- Trang chủ với sản phẩm nổi bật và banner Flash Sale
- Danh sách sản phẩm với phân trang (có thể tùy chỉnh số lượng mỗi trang)
- Lọc đa tiêu chí:
  - Danh mục sản phẩm
  - Thương hiệu
  - Khoảng giá (min-max)
  - Size (35-45)
  - Tình trạng (còn hàng/hết hàng)
- Tìm kiếm toàn văn (theo tên, mô tả)
- Sắp xếp: Mới nhất, Giá (Thấp-Cao), Giá (Cao-Thấp), Bán chạy
- Trang chi tiết sản phẩm:
  - Thư viện ảnh với zoom
  - Mô tả và thông số sản phẩm
  - Bảng size
  - Tình trạng tồn kho theo size
  - Đánh giá của khách hàng
  - Sản phẩm liên quan gợi ý

**4. Giỏ Hàng**
- Thêm vào giỏ với lựa chọn size
- Cập nhật số lượng (kiểm tra tồn kho realtime)
- Xóa sản phẩm
- Chọn nhiều sản phẩm (checkbox)
- Tính tổng tiền tạm
- Áp dụng giá Flash Sale tự động
- Lưu trạng thái giỏ hàng (trong database)
- Badge đếm số lượng giỏ hàng (cập nhật realtime)
- Cảnh báo giỏ hàng trống

**5. Danh Sách Yêu Thích**
- Thêm/xóa sản phẩm vào danh sách yêu thích
- Xem danh sách với chi tiết sản phẩm
- Chuyển sang giỏ hàng
- Thông báo khi có hàng trở lại
- Chia sẻ danh sách (tùy chọn)

**6. Thanh Toán & Đặt Hàng**
- Quy trình thanh toán nhiều bước
- Chọn địa chỉ với thêm địa chỉ mới inline
- Lựa chọn đơn vị vận chuyển
- Tự động tính phí vận chuyển dựa trên khoảng cách GPS
- Áp dụng voucher giảm giá đơn hàng
- Áp dụng voucher giảm phí vận chuyển
- Đổi điểm thành viên
- Sử dụng DeeG Xu (xu) để giảm giá
- Xem trước tóm tắt đơn hàng
- Phương thức thanh toán:
  - COD (Thanh toán khi nhận hàng)
  - PayOS (Thanh toán online QR Code, chuyển khoản)
- Xác minh và xác nhận thanh toán
- Trang theo dõi đơn hàng

**7. Hệ Thống Flash Sale**
- Hiển thị Flash Sale đang diễn ra với đếm ngược thời gian
- Xem trước Flash Sale sắp tới
- Theo dõi tồn kho realtime (AJAX polling mỗi 3-5 giây)
- Thanh tiến trình: phần trăm đã bán
- Làm nổi bật giá Flash Sale
- Nút mua hàng với kiểm tra tồn kho
- Pessimistic locking để tránh bán quá hàng
- Lịch sử Flash Sale
- Thông báo khi Flash Sale bắt đầu

**8. Hệ Thống Voucher & Giảm Giá**
- Trang kho voucher
- Hai loại:
  - Voucher đơn hàng (giảm giá đơn hàng)
  - Voucher vận chuyển (giảm phí ship)
- Chi tiết voucher:
  - Loại giảm giá: Phần trăm hoặc Số tiền cố định
  - Yêu cầu giá trị đơn hàng tối thiểu
  - Yêu cầu hạng thành viên (BẠC, VÀNG, v.v.)
  - Giới hạn sử dụng mỗi người
  - Giới hạn tổng số lượng
  - Thời hạn sử dụng (ngày bắt đầu - kết thúc)
- Thu thập voucher (claim)
- Áp dụng voucher khi thanh toán
- Kiểm tra voucher realtime
- Xếp chồng nhiều voucher (đơn hàng + vận chuyển)

**9. Quản Lý Đơn Hàng**
- Lịch sử đơn hàng với bộ lọc:
  - Lọc trạng thái (Chờ xử lý, Đang giao, Đã giao, Hủy, Trả hàng)
  - Lọc khoảng thời gian
  - Tìm kiếm theo mã đơn hàng
- Trang chi tiết đơn hàng:
  - Sản phẩm trong đơn với hình ảnh
  - Chi tiết giá (tạm tính, phí ship, giảm giá, tổng)
  - Địa chỉ giao hàng
  - Phương thức thanh toán
  - Dòng thời gian đơn hàng (lịch sử trạng thái)
- Theo dõi đơn hàng realtime
- Đặt lại đơn hàng (mua lại 1 cú nhấp)
- Hủy đơn hàng (khi còn chờ xử lý)
- Yêu cầu trả hàng (cho đơn đã giao)

**10. Đánh Giá & Nhận Xét Sản Phẩm**
- Đánh giá sản phẩm (1-5 sao)
- Viết nhận xét bằng văn bản
- Upload hình ảnh đánh giá (tùy chọn)
- Sửa/xóa đánh giá của mình
- Lọc đánh giá:
  - Theo số sao (5 sao, 4 sao, v.v.)
  - Chỉ đánh giá có bình luận
  - Chỉ đánh giá có hình ảnh
- Sắp xếp đánh giá (Mới nhất, Hữu ích nhất)
- Vote hữu ích (upvote đánh giá)

**11. Hỗ Trợ AI Chatbot**
- Sử dụng Google Gemini 2.5 Flash AI
- Hoạt động 24/7
- Hiểu ngôn ngữ tự nhiên
- Trò chuyện theo ngữ cảnh
- Tính năng:
  - Tra cứu thông tin sản phẩm
  - Kiểm tra trạng thái đơn hàng
  - Trả lời câu hỏi thường gặp
  - Chính sách cửa hàng
  - Thông tin vận chuyển
- Lịch sử chat theo phiên
- Lưu trữ cuộc trò chuyện

**12. Thông Báo Realtime**
- Push notification dựa trên WebSocket
- Các loại thông báo:
  - Cập nhật trạng thái đơn hàng
  - Flash Sale bắt đầu
  - Chương trình khuyến mãi mới
  - Tin nhắn chat
  - Cảnh báo sắp hết hàng
- Badge thông báo với số lượng chưa đọc
- Trung tâm thông báo
- Đánh dấu đã đọc

### Tính Năng Quản Trị (Admin Features)

**1. Admin Dashboard & Analytics**
- Real-time statistics:
  - Total revenue (today, this month, this year)
  - Total orders by status
  - New customers count
  - Total products và low stock alerts
- Revenue charts:
  - Line chart: Revenue over time
  - Bar chart: Revenue by category
  - Pie chart: Order status distribution
- Top selling products (with images và sold count)
- Recent orders list
- Quick actions: Add Product, Create Flash Sale, View Reports

**2. Product Management**
- Product listing với pagination và search
- CRUD operations:
  - Create product với multiple sizes
  - Edit product information
  - Delete product (soft delete)
  - Activate/deactivate product
- Bulk operations:
  - Bulk price update
  - Bulk category assignment
  - Bulk delete
- Product variant management (sizes):
  - Add/remove sizes
  - Set price per size
  - Set stock per size
- Image management:
  - Upload multiple images to Cloudinary
  - Set primary image
  - Delete images
  - Image CDN optimization
- Product import/export (Excel)

**3. Category & Brand Management**
- Category CRUD:
  - Create/edit/delete categories
  - Category hierarchy (parent-child)
  - Category image upload
  - SEO settings (slug, meta description)
- Brand CRUD:
  - Add/edit/delete brands
  - Brand logo upload
  - Brand description

**4. Order Management**
- Order listing với advanced filters:
  - Status (IN_STOCK, SHIPPED, DELIVERED, CANCEL, RETURN)
  - Payment method (COD, PayOS)
  - Date range
  - Customer search
  - Order ID search
- Order details view:
  - Customer information
  - Order items với pricing
  - Delivery address
  - Payment status
  - Timeline history
- Order operations:
  - Update order status
  - Assign shipper
  - Print invoice
  - Cancel order (with reason)
  - Process refund
- Order export to Excel:
  - Custom date range
  - Filter by status
  - Include order details
- Bulk operations:
  - Bulk status update
  - Bulk shipper assignment

**5. Flash Sale Management**
- Flash sale creation wizard:
  - Set name, description
  - Upload banner image
  - Set time range (start - end)
  - Select products to include
- Add products to flash sale:
  - Search và select products
  - Set discount percentage per product
  - Set stock limit per product
- Flash sale listing:
  - Active flash sales
  - Scheduled flash sales
  - Ended flash sales
- Flash sale operations:
  - Edit flash sale details
  - Add/remove products
  - End flash sale early
  - Clone flash sale
  - Delete flash sale
- Real-time monitoring:
  - Total items sold
  - Revenue generated
  - Stock remaining
  - User participation

**6. Discount/Voucher Management**
- Voucher creation form:
  - Voucher name và code
  - Discount type (ORDER or SHIPPING)
  - Value type (PERCENTAGE or FIXED_AMOUNT)
  - Discount value
  - Minimum order value requirement
  - User tier requirement
  - Usage limit per user
  - Total quantity
  - Validity period (start - end date)
  - Status (ACTIVE, INACTIVE, EXPIRED)
- Voucher listing với filters
- Voucher operations:
  - Edit voucher details
  - Activate/deactivate
  - Delete voucher
  - Extend validity period
- Usage statistics:
  - Total uses
  - Total discount amount given
  - Users who used
  - Revenue impact analysis

**7. Inventory & Warehouse Management**
- Warehouse configuration:
  - Add/edit warehouses
  - Set GPS coordinates
  - Set operating hours
- Stock level monitoring:
  - Current stock per product detail
  - Low stock alerts (<10 items)
  - Out of stock products
- Inventory operations:
  - Stock adjustment (increase/decrease)
  - Stock import (receiving)
  - Stock transfer between warehouses
- Inventory history:
  - Movement logs
  - Import/export records
  - Adjustment reasons
  - Performed by user tracking
- Stock reports:
  - Current stock levels
  - Stock movement history
  - Low stock report

**8. Shipping Management**
- Shipping company configuration:
  - Add/edit shipping providers
  - Upload company logo
  - Set base rates
  - Set per-km rates
  - Define distance tiers với different rates
- Shipping rate calculator:
  - Test distance calculation
  - Verify pricing
- Shipper assignment:
  - Assign orders to shippers
  - Shipper performance tracking
  - Delivery completion rates

**9. User Management**
- User listing với search và filters:
  - Role filter
  - Registration date
  - Membership tier
  - Active/inactive status
- User operations:
  - View user profile
  - Edit user information
  - Change user role
  - Activate/deactivate account
  - View user orders
  - View user activity log
- Membership management:
  - Upgrade/downgrade tier
  - Adjust loyalty points
  - Adjust DeeG Xu balance
- Bulk operations:
  - Send email to users
  - Export user list

**10. Return Request Management**
- Return request listing
- Return request details:
  - Order information
  - Return reason
  - Return images
  - Customer notes
- Return operations:
  - Approve return
  - Reject return (with reason)
  - Process refund
  - Arrange return shipment
- Return statistics:
  - Total returns
  - Return rate
  - Common return reasons

**11. Permission & Role Management**
- Role-based access control (RBAC)
- Four main roles:
  - ADMIN: Full system access
  - MANAGER: Product, order, report access
  - SHIPPER: Delivery management only
  - USER: Customer features only
- Permission assignment per role
- Custom permission creation (optional)

**12. System Configuration**
- General settings:
  - Site name, logo, favicon
  - Contact information
  - Social media links
- Email templates:
  - Order confirmation
  - Shipping notification
  - Password reset
- Payment gateway settings:
  - PayOS credentials
  - Test/production mode
- API key management:
  - Cloudinary
  - Goong Maps
  - Gemini AI

### Tính Năng Shipper (Shipper Features)

**1. Shipper Dashboard**
- Today's delivery statistics:
  - Total assigned orders
  - Completed deliveries
  - Pending deliveries
  - Failed deliveries
- Performance metrics:
  - Success rate
  - Average delivery time
  - Customer ratings

**2. Order Assignment & Management**
- Assigned orders listing với filters:
  - Status (SHIPPED, DELIVERED)
  - Delivery date
  - Area/district
- Order details:
  - Customer information
  - Phone number (call directly)
  - Delivery address với map
  - Order items
  - Payment method (COD amount)
  - Delivery notes
- Map integration:
  - View delivery location on map
  - Get directions (Goong Maps)
  - Optimal route planning (optional)

**3. Delivery Operations**
- Update delivery status:
  - Mark as SHIPPED (picked up)
  - Mark as DELIVERED (with confirmation)
  - Mark as FAILED (with reason)
- Failed delivery reasons:
  - Customer not available
  - Wrong address
  - Customer refused
  - Other (custom reason)
- COD collection:
  - Confirm cash received
  - Record payment
- Delivery proof:
  - Upload delivery photo
  - Customer signature (optional)

**4. Performance Tracking**
- Delivery history
- Customer feedback
- Rating và reviews from customers
- Earnings tracking (if commission-based)

### 📦 Cấu Trúc Dự Án

```
shoe_shop_web/
├── src/
│   ├── main/
│   │   ├── java/com/dev/shoeshop/
│   │   │   ├── config/              # Cấu hình (Security, WebSocket, Cloudinary, etc.)
│   │   │   ├── controller/          # Controllers (Web + REST API)
│   │   │   │   ├── admin/           # Admin controllers
│   │   │   │   ├── manager/         # Manager controllers
│   │   │   │   ├── shipper/         # Shipper controllers
│   │   │   │   ├── payment/         # Payment & Checkout
│   │   │   │   └── api/             # REST APIs
│   │   │   ├── entity/              # JPA Entities (27 entities)
│   │   │   ├── repository/          # Spring Data JPA Repositories
│   │   │   ├── service/             # Business logic services
│   │   │   │   └── impl/            # Service implementations
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── enums/               # Enums (Status, Type, etc.)
│   │   │   ├── security/            # Security (AuthProvider, Handlers)
│   │   │   ├── scheduler/           # Scheduled tasks
│   │   │   └── utils/               # Utilities
│   │   └── resources/
│   │       ├── application.properties          # Main config
│   │       ├── application-uat.properties      # Development config
│   │       ├── application-pro.properties      # Production config
│   │       ├── templates/                      # Thymeleaf templates
│   │       │   ├── admin/           # Admin pages
│   │       │   ├── manager/         # Manager pages
│   │       │   ├── shipper/         # Shipper pages
│   │       │   ├── user/            # User pages
│   │       │   └── fragments/       # Reusable fragments
│   │       └── static/              # CSS, JS, images
├── database/                        # Database schema & diagrams
├── uploads/                         # Local file uploads
├── Dockerfile                       # Docker multi-stage build
├── render.yaml                      # Render deployment config
├── .env.example                     # Environment variables template
└── pom.xml                          # Maven dependencies
```

### 🗄 Database Schema

Hệ thống sử dụng **27 entities** chính:

**Core Entities:**
- `Users`, `Role`, `Address`, `UserAddress`
- `Product`, `ProductDetail`, `Brand`, `Category`
- `Inventory`, `Cart`, `CartDetail`, `WishList`
- `Order`, `OrderDetail`, `Rating`

**Discount & Flash Sale:**
- `Discount`, `DiscountUsed`
- `FlashSale`, `FlashSaleItem`

**Shipping:**
- `ShippingCompany`, `ShippingRate`, `Shipment`, `Shipper`
- `ShopWarehouse`, `DistanceCache`

**Others:**
- `PasswordResetToken`

### 🚀 Hướng Dẫn Cài Đặt

#### Yêu Cầu Hệ Thống
- **Java**: 21 trở lên
- **Maven**: 3.9+
- **MySQL**: 8.0+
- **IDE**: IntelliJ IDEA / Eclipse / VSCode

#### Bước 1: Clone Repository
```bash
git clone https://github.com/your-repo/shoe_shop_web.git
cd shoe_shop_web
```

#### Bước 2: Cấu Hình Database
Tạo database MySQL:
```sql
CREATE DATABASE shoe_shop_basic CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Bước 3: Cấu Hình Environment Variables
Copy file `.env.example` thành `.env` hoặc cấu hình trong `application-uat.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/shoe_shop_basic
spring.datasource.username=root
spring.datasource.password=your_password

# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=your_client_id
spring.security.oauth2.client.registration.google.client-secret=your_client_secret

# Gemini AI
gemini.api.key=your_gemini_api_key

# Goong Maps
goong.api.key=your_goong_api_key

# Email
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

# Cloudinary
cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret

# PayOS
payos.client-id=your_client_id
payos.api-key=your_api_key
payos.checksum-key=your_checksum_key
```

#### Bước 4: Build & Run
```bash
# Build project
mvn clean install -DskipTests

# Run application
mvn spring-boot:run

# Hoặc chạy file JAR
java -jar target/shoe_shop_web-0.0.1-SNAPSHOT.jar
```

Ứng dụng sẽ chạy tại: `http://localhost:8081`

#### Bước 5: Truy Cập Hệ Thống

**User Page:**
- Homepage: `http://localhost:8081/`
- Shop: `http://localhost:8081/product/list`
- Login: `http://localhost:8081/login`

**Admin Dashboard:**
- URL: `http://localhost:8081/admin`
- Default account: admin/admin (tạo trong database)

### 🐳 Deploy với Docker

#### Build Docker Image
```bash
docker build -t shoe-shop-web:latest .
```

#### Run Container
```bash
docker run -d \
  -p 8080:8080 \
  -e DATABASE_URL="jdbc:mysql://host:port/db" \
  -e DATABASE_USERNAME="user" \
  -e DATABASE_PASSWORD="pass" \
  -e SPRING_PROFILES_ACTIVE="pro" \
  --name shoe-shop \
  shoe-shop-web:latest
```

### ☁️ Deploy lên Render

1. **Tạo Web Service** trên Render Dashboard
2. **Connect Repository** từ GitHub
3. **Configure Environment Variables** trong Render Dashboard (theo `.env.example`)
4. **Deploy**: Render sẽ tự động build từ `Dockerfile` và deploy

**Health Check**: `/actuator/health`

### 📚 API Documentation

#### REST APIs

**Voucher APIs:**
```
GET  /api/vouchers/order              # Lấy voucher đơn hàng
GET  /api/vouchers/shipping           # Lấy voucher ship
POST /api/vouchers/shipping/calculate # Tính giảm giá ship
POST /api/vouchers/shipping/validate  # Validate voucher ship
```

**Flash Sale APIs:**
```
GET  /api/flash-sale/active           # Flash sale đang diễn ra
GET  /api/flash-sale/upcoming         # Flash sale sắp diễn
GET  /api/flash-sale/{id}/items       # Sản phẩm trong flash sale
GET  /api/flash-sale/item/{id}/stock  # Lấy stock realtime
```

**Cart APIs:**
```
POST /api/cart/add                    # Thêm vào giỏ
PUT  /api/cart/update                 # Cập nhật số lượng
POST /api/cart/remove                 # Xóa khỏi giỏ
```

**Shipping APIs:**
```
POST /api/shipping/calculate-fee      # Tính phí ship
```

**Address APIs:**
```
GET  /api/address/user/{userId}       # Danh sách địa chỉ
POST /api/address/add                 # Thêm địa chỉ mới
```

### 🔐 Phân Quyền (Roles)

| Role | Quyền Truy Cập |
|------|----------------|
| **ADMIN** | Full quyền: Dashboard, quản lý sản phẩm, đơn hàng, voucher, flash sale, kho, user, phân quyền |
| **MANAGER** | Quản lý sản phẩm, danh mục, đơn hàng, xem báo cáo |
| **SHIPPER** | Xem và cập nhật đơn hàng cần giao |
| **USER** | Mua sắm, quản lý tài khoản, đặt hàng, đánh giá |

### 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=ServiceTest
```

### 📊 Performance & Optimization

- **Lazy Loading**: Entity relationships sử dụng `LAZY` fetch
- **Connection Pooling**: HikariCP (Spring Boot default)
- **Caching**: Distance Cache (Goong API), 30 days TTL
- **Image Optimization**: Cloudinary auto-optimization
- **Database Indexing**: Index trên foreign keys và search columns
- **Transaction Management**: `@Transactional` cho operations quan trọng
- **Pessimistic Locking**: Flash Sale to prevent overselling

### 🐛 Troubleshooting

#### Lỗi kết nối Database
```
spring.jpa.hibernate.ddl-auto=update
```
Đảm bảo MySQL đang chạy và credentials đúng.

#### Lỗi OAuth2 Google Login
- Kiểm tra redirect URI trong Google Console
- Production: `https://yourdomain.com/login/oauth2/code/google`
- Local: `http://localhost:8081/login/oauth2/code/google`

#### Lỗi PayOS Webhook
- Webhook URL phải là HTTPS (production)
- Local testing: Dùng ngrok hoặc skip webhook

### 📖 Tài Liệu Tham Khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2)
- [PayOS Documentation](https://payos.vn/docs)
- [Goong Maps API](https://docs.goong.io)
- [Cloudinary Upload API](https://cloudinary.com/documentation)

### 👥 Tác Giả

Dự án được phát triển bởi nhóm sinh viên:

- **Trương Nhất Nguyên** - 23110273
- **Nguyễn Hoàng Hà** - 23110207  
- **Nghiêm Quang Huy** - 23110222
- **Nguyễn Tấn Yên** - 23110369

**Trường**: Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)  
**Môn học**: Công nghệ Web  
**Năm học**: 2024-2025

### 🖼️ Hướng Dẫn Thêm Screenshots

Để thêm ảnh demo vào README, tạo cấu trúc thư mục sau:

```bash
mkdir -p docs/images
```

Sau đó thêm các file ảnh với tên tương ứng:

**Trang User:**
- `banner.png` - Banner/Logo project
- `video-thumbnail.png` - Thumbnail video demo
- `homepage.png` - Trang chủ
- `shop.png` - Trang danh sách sản phẩm
- `product-detail.png` - Chi tiết sản phẩm
- `cart.png` - Giỏ hàng
- `checkout.png` - Trang thanh toán
- `payment.png` - Màn hình thanh toán PayOS
- `flashsale.png` - Trang Flash Sale
- `voucher.png` - Kho voucher
- `chatbot.png` - AI Chatbot
- `profile.png` - Trang tài khoản

**Trang Admin:**
- `admin-dashboard.png` - Dashboard admin
- `admin-products.png` - Quản lý sản phẩm
- `admin-orders.png` - Quản lý đơn hàng
- `admin-flashsale.png` - Quản lý Flash Sale
- `admin-voucher.png` - Quản lý voucher
- `admin-inventory.png` - Quản lý kho

**Trang Shipper:**
- `shipper-orders.png` - Danh sách đơn hàng shipper
- `shipper-detail.png` - Chi tiết đơn hàng shipper

**Mobile:**
- `mobile-responsive.png` - Responsive design

> **Lưu ý**: Ảnh nên có định dạng PNG hoặc JPG, kích thước tối đa 1920x1080px để tối ưu hiển thị trên GitHub.

---

## 🤝 Contributing

Chúng tôi rất hoan nghênh mọi đóng góp! Nếu bạn muốn contribute:

1. Fork repository này
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push lên branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

---

## 📞 Liên Hệ & Hỗ Trợ

- 📧 **Email**: deegshop.support@gmail.com
- 🌐 **Website**: [Coming Soon]
- 📱 **Facebook**: [DeeG Shoe Shop Official]
- 💬 **Discord**: [Join our community]

Nếu gặp vấn đề hoặc có câu hỏi, vui lòng tạo [Issue](https://github.com/your-repo/shoe_shop_web/issues) trên GitHub.

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

### MIT License Summary
- ✅ Commercial use
- ✅ Modification
- ✅ Distribution
- ✅ Private use
- ❌ Liability
- ❌ Warranty

---

## 🙏 Acknowledgments

Chúng tôi xin gửi lời cảm ơn đến:

- **Spring Boot Team** - Framework Java mạnh mẽ và dễ sử dụng
- **PayOS** - Payment gateway hỗ trợ thanh toán QR Code
- **Google** - Gemini AI API và OAuth2 authentication
- **Goong Maps** - Maps API và tính phí ship theo GPS
- **Cloudinary** - Cloud storage cho hình ảnh
- **Bootstrap** - UI framework responsive
- **Font Awesome & Lucide** - Icon libraries
- **MySQL** - Hệ quản trị CSDL mã nguồn mở
- **Docker** - Containerization platform
- **GitHub** - Version control và CI/CD
- **Render** - Cloud platform cho deployment

Và tất cả các thư viện open-source đã được sử dụng trong dự án này! 🎉

---

## 📊 Project Statistics

![GitHub repo size](https://img.shields.io/github/repo-size/your-username/shoe_shop_web)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/your-username/shoe_shop_web)
![GitHub language count](https://img.shields.io/github/languages/count/your-username/shoe_shop_web)
![GitHub top language](https://img.shields.io/github/languages/top/your-username/shoe_shop_web)

---

<div align="center">

### 🌟 Nếu thấy project hữu ích, hãy cho chúng tôi một Star! ⭐

---

**Made with ❤️ by HCMUTE Students**

**Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh**

**© 2024-2025 DeeG Shoe Shop. All Rights Reserved.**

---

[![GitHub followers](https://img.shields.io/github/followers/your-username?style=social)](https://github.com/your-username)
[![GitHub stars](https://img.shields.io/github/stars/your-username/shoe_shop_web?style=social)](https://github.com/your-username/shoe_shop_web)
[![GitHub forks](https://img.shields.io/github/forks/your-username/shoe_shop_web?style=social)](https://github.com/your-username/shoe_shop_web/fork)

</div>
