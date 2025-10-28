# DeeG Shoe Shop - E-commerce Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

## Giới Thiệu

DeeG Shoe Shop là hệ thống quản lý và bán hàng giày dép trực tuyến toàn diện, được phát triển bằng Spring Boot 3.5.6 và Java 21. Đây là nền tảng e-commerce đầy đủ tính năng với tích hợp thanh toán trực tuyến, AI chatbot, flash sale, hệ thống voucher phức tạp, và tính phí vận chuyển tự động dựa trên GPS.

### Mục Tiêu Dự Án

- Xây dựng nền tảng thương mại điện tử hoàn chỉnh cho ngành bán lẻ giày dép
- Tích hợp các công nghệ hiện đại: AI, Payment Gateway, Cloud Storage, Real-time Communication
- Hỗ trợ đa vai trò người dùng với phân quyền chi tiết (Admin, Manager, Shipper, User)
- Tối ưu trải nghiệm mua sắm với giao diện responsive và UX thân thiện
- Sẵn sàng triển khai production với Docker, CI/CD và Cloud Platform

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

### Backend Framework & Core Technologies

**Spring Framework Ecosystem:**
- Spring Boot 3.5.6 - Application framework
- Spring Data JPA - ORM và database operations
- Spring Security 6 - Authentication và authorization
- Spring Security OAuth2 Client - Google OAuth2 integration
- Spring Boot Actuator - Health monitoring và metrics
- Spring WebSocket - Real-time bidirectional communication
- Spring Scheduler - Automated background tasks
- Spring Boot Starter Mail - Email service
- Spring Boot DevTools - Development productivity

**Database & Persistence:**
- MySQL 8.0 - Relational database
- Hibernate ORM - JPA implementation
- HikariCP - High-performance connection pooling (default)
- Spring Data JPA Repositories - Data access layer

**Security & Authentication:**
- BCrypt Password Encoder - Secure password hashing
- Google OAuth2 - Social login integration
- Session-based Authentication - Stateful authentication
- Role-based Access Control (RBAC) - Authorization

**Validation & Data Processing:**
- Jakarta Validation API 3.0.2 - Bean validation
- Hibernate Validator 8.0.2.Final - Validation implementation
- ModelMapper 3.2.0 - Object mapping (Entity ↔ DTO)

### Third-party Integrations & APIs

**Payment Gateway:**
- PayOS Java SDK 2.0.1 - Vietnamese payment gateway
  - QR Code payment
  - Bank transfer
  - Payment verification
  - Webhook handling

**Cloud Services:**
- Cloudinary HTTP44 1.36.0 - Cloud image management
  - Image upload và storage
  - Image optimization
  - CDN delivery
  - Transformation APIs

**Artificial Intelligence:**
- Google Gemini AI 1.21.0 - AI-powered chatbot
  - Natural language processing
  - Context-aware responses
  - Product queries
  - Order tracking assistance

**Maps & Geolocation:**
- Goong Maps API - Vietnamese maps service
  - Distance calculation (GPS-based)
  - Geocoding và reverse geocoding
  - Shipping fee estimation
  - Address validation

**Email Service:**
- Gmail SMTP với Spring Mail
  - User registration verification
  - Password reset emails
  - Order confirmation
  - Promotional emails

**File Processing:**
- Apache POI 5.2.5 - Excel manipulation
- Apache POI OOXML 5.2.5 - Modern Excel format (.xlsx)
  - Order report export
  - Product import/export
  - Statistics reports

### API Documentation:**
- SpringDoc OpenAPI 2.3.0 - OpenAPI 3.0 specification
  - Swagger UI integration
  - Interactive API testing
  - Auto-generated documentation

### Frontend Technologies

**Template Engine:**
- Thymeleaf - Server-side Java template engine
- Thymeleaf Extras Spring Security - Security tags và utilities

**UI Framework & Styling:**
- Bootstrap 5 - Responsive CSS framework
- Custom CSS - Brand-specific styling
- Font Awesome - Icon library
- Lucide Icons - Modern icon set

**JavaScript & Client-side:**
- Vanilla JavaScript - Core scripting
- AJAX (XMLHttpRequest/Fetch API) - Asynchronous requests
- WebSocket Client (STOMP.js) - Real-time communication
- jQuery - DOM manipulation và AJAX

### Development & Build Tools

**Build Tool:**
- Apache Maven 3.9+ - Dependency management và build automation
- Maven Compiler Plugin - Java compilation
- Spring Boot Maven Plugin - Executable JAR packaging

**Code Quality:**
- Lombok - Boilerplate code reduction
  - @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
  - @Getter, @Setter annotations
- Spring Boot Configuration Processor - Metadata generation

### DevOps & Deployment

**Containerization:**
- Docker - Container platform
- Multi-stage Dockerfile - Optimized image building
- Docker Compose (optional) - Multi-container orchestration

**Cloud Platform:**
- Render.com - PaaS deployment
  - Auto-scaling
  - Zero-downtime deployment
  - Health checks
  - Environment variables management

**CI/CD:**
- GitHub Actions - Automated workflows
  - Automated testing
  - Build và deployment pipeline
  - Code quality checks

**Monitoring:**
- Spring Boot Actuator endpoints
  - /actuator/health - Health check
  - /actuator/metrics - Application metrics
  - /actuator/info - Application info

---

## Tính Năng Nổi Bật

### Tính Năng Người Dùng (User Features)

**1. Quản Lý Tài Khoản & Xác Thực**
- Đăng ký tài khoản local với email verification
- Đăng nhập local với username/password (BCrypt hashing)
- Đăng nhập Google OAuth2 (one-click login)
- Quên mật khẩu với reset token qua email
- Quản lý thông tin cá nhân (tên, số điện thoại, avatar)
- Hệ thống membership tiers: SILVER, GOLD, PLATINUM, DIAMOND
- DeeG Xu (loyalty coins): Earn và redeem (1 xu = 1 VND)
- Loyalty points accumulation (1 point per 10,000 VND spent)

**2. Quản Lý Địa Chỉ Giao Hàng**
- Thêm/sửa/xóa nhiều địa chỉ giao hàng
- Đặt địa chỉ mặc định
- Tích hợp Goong Maps để pick location
- Tự động điền địa chỉ từ coordinates
- Hiển thị map preview cho địa chỉ
- Tính toán khoảng cách từ warehouse đến địa chỉ

**3. Catalog & Product Browsing**
- Trang chủ với featured products và flash sale banner
- Product listing với pagination (configurable items per page)
- Multi-criteria filtering:
  - Category (danh mục sản phẩm)
  - Brand (thương hiệu)
  - Price range (khoảng giá min-max)
  - Size (35-45)
  - Availability (còn hàng/hết hàng)
- Full-text search (tìm kiếm theo tên, mô tả)
- Sort options: Newest, Price (Low-High), Price (High-Low), Best Selling
- Product detail page:
  - Image gallery với zoom
  - Product description và specifications
  - Size chart
  - Stock availability per size
  - Customer reviews và ratings
  - Related products suggestions

**4. Shopping Cart**
- Add to cart với size selection
- Update quantity (real-time stock validation)
- Remove items
- Multiple item selection (checkbox)
- Calculate subtotal
- Apply flash sale prices automatically
- Save cart state (persisted in database)
- Cart count badge (real-time update)
- Empty cart warning

**5. Wishlist**
- Add/remove products to wishlist
- View wishlist với product details
- Move to cart functionality
- Stock notification when available
- Share wishlist (optional)

**6. Checkout & Payment**
- Multi-step checkout process
- Address selection với add new address inline
- Shipping company selection
- Automatic shipping fee calculation based on GPS distance
- Apply order discount voucher
- Apply shipping discount voucher
- Redeem loyalty points
- Use DeeG Xu (coins) for discount
- Order summary preview
- Payment methods:
  - COD (Cash on Delivery)
  - PayOS Online Payment (QR Code, Bank Transfer)
- Payment verification và confirmation
- Order tracking page

**7. Flash Sale System**
- Active flash sale display với countdown timer
- Upcoming flash sale preview
- Real-time stock tracking (AJAX polling every 3-5 seconds)
- Progress bar: sold percentage
- Flash sale price highlight
- Purchase button với stock validation
- Pessimistic locking để prevent overselling
- Flash sale history
- Notification khi flash sale starts

**8. Voucher & Discount System**
- Voucher collection page
- Two types:
  - Order vouchers (giảm giá đơn hàng)
  - Shipping vouchers (giảm phí vận chuyển)
- Voucher details:
  - Discount type: Percentage or Fixed Amount
  - Minimum order value requirement
  - User tier requirement (SILVER, GOLD, etc.)
  - Usage limit per user
  - Total quantity limit
  - Validity period (start date - end date)
- Collect voucher (claim)
- Apply voucher at checkout
- Voucher validation real-time
- Stack multiple vouchers (order + shipping)

**9. Order Management**
- Order history với filters:
  - Status filter (IN_STOCK, SHIPPED, DELIVERED, CANCEL, RETURN)
  - Date range filter
  - Search by order ID
- Order details page:
  - Order items với images
  - Pricing breakdown (subtotal, shipping, discount, total)
  - Delivery address
  - Payment method
  - Order timeline (status history)
- Order tracking real-time
- Reorder functionality (one-click re-purchase)
- Cancel order (khi còn IN_STOCK)
- Return request (cho đơn đã DELIVERED)

**10. Product Reviews & Ratings**
- Rate products (1-5 stars)
- Write text review
- Upload review images (optional)
- Edit/delete own reviews
- Filter reviews:
  - By star rating (5-star, 4-star, etc.)
  - Reviews with comments only
  - Reviews with images only
- Sort reviews (Most Recent, Most Helpful)
- Helpful vote (upvote reviews)

**11. AI Chatbot Support**
- Powered by Google Gemini 2.5 Flash AI
- 24/7 availability
- Natural language understanding
- Context-aware conversations
- Features:
  - Product information queries
  - Order status checking
  - FAQ responses
  - Store policies
  - Shipping information
- Chat history per session
- Conversation persistence

**12. Real-time Notifications**
- WebSocket-based push notifications
- Notification types:
  - Order status updates
  - Flash sale starts
  - New promotions
  - Chat messages
  - Low stock alerts
- Notification badge với unread count
- Notification center
- Mark as read functionality

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
