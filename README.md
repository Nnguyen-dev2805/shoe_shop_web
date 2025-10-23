# 👟 DeeG Shoe Shop - Online Shoe Store Management System

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-orange" alt="Java">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue" alt="MySQL">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

## 📝 Giới Thiệu

**DeeG Shoe Shop** là hệ thống website bán hàng và quản lý cửa hàng giày dép, được xây dựng bằng Spring Boot 3.5.6 và các công nghệ hiện đại. Hệ thống cung cấp đầy đủ tính năng từ quản lý sản phẩm, đặt hàng, thanh toán trực tuyến, đến các tính năng nâng cao như Flash Sale, Voucher System, AI Chatbot và tích hợp bản đồ để phục vụ khách hàng mua sắm trực tuyến.

### ✨ Tính Năng Nổi Bật

#### 🛒 Tính Năng Người Dùng
- **Quản lý tài khoản**: Đăng ký, đăng nhập (Local + Google OAuth2), quên mật khẩu qua email
- **Mua sắm thông minh**:
  - Tìm kiếm và lọc sản phẩm theo thương hiệu, danh mục, giá, size
  - Xem chi tiết sản phẩm với hình ảnh, mô tả, đánh giá
  - Giỏ hàng với tính năng chọn nhiều sản phẩm
  - Wishlist (danh sách yêu thích)
- **Đặt hàng & Thanh toán**:
  - Quản lý nhiều địa chỉ giao hàng với tích hợp Goong Maps
  - Tính phí ship tự động dựa trên khoảng cách GPS
  - Thanh toán COD hoặc trực tuyến qua PayOS
  - Theo dõi trạng thái đơn hàng realtime
- **Flash Sale & Voucher**:
  - Flash Sale với countdown timer
  - Voucher giảm giá đơn hàng & phí ship
  - Hệ thống voucher phân loại (% hoặc số tiền cố định)
- **Đánh giá & Tương tác**:
  - Đánh giá sản phẩm với sao và nội dung
  - AI Chatbot hỗ trợ 24/7 (Gemini AI)
  - Thông báo realtime qua WebSocket

#### 👨‍💼 Tính Năng Quản Trị
- **Admin Dashboard**: Thống kê doanh thu, đơn hàng, sản phẩm bán chạy
- **Quản lý sản phẩm**: CRUD sản phẩm với upload ảnh lên Cloudinary
- **Quản lý đơn hàng**: Xem, cập nhật trạng thái, xuất báo cáo Excel
- **Quản lý Flash Sale**: Tạo, sửa, xóa flash sale và thêm sản phẩm
- **Quản lý Voucher/Discount**: Tạo voucher với điều kiện và giới hạn
- **Quản lý kho**: Theo dõi tồn kho, nhập xuất hàng
- **Quản lý vận chuyển**: Cấu hình công ty vận chuyển và phí ship theo khoảng cách
- **Phân quyền**: Hệ thống multi-role (Admin, Manager, Shipper, User)

#### 🚚 Tính Năng Shipper
- Xem danh sách đơn hàng cần giao
- Cập nhật trạng thái giao hàng
- Xác nhận hoàn thành đơn

### 🛠 Công Nghệ Sử Dụng

#### Backend
- **Framework**: Spring Boot 3.5.6 (Java 21)
- **Security**: Spring Security 6 + OAuth2 Client (Google Login)
- **Database**: MySQL 8.0 + Spring Data JPA + Hibernate
- **Validation**: Jakarta Validation API 3.0 + Hibernate Validator 8.0
- **Mapping**: ModelMapper 3.2
- **Realtime**: WebSocket + STOMP
- **Task Scheduling**: Spring Scheduler (Auto update Flash Sale status)

#### Integration & Services
- **Payment Gateway**: PayOS (Thanh toán QR, chuyển khoản)
- **Cloud Storage**: Cloudinary (Lưu trữ hình ảnh)
- **Email Service**: Gmail SMTP (Gửi email xác thực, reset password)
- **AI Chatbot**: Google Gemini 2.5 Flash API
- **Maps & Geolocation**: Goong Maps API (Tính khoảng cách, phí ship)
- **Export**: Apache POI (Xuất báo cáo Excel)

#### Frontend
- **Template Engine**: Thymeleaf + Thymeleaf Extras Spring Security
- **UI Framework**: Bootstrap 5, Custom CSS
- **JavaScript**: Vanilla JS + AJAX, WebSocket Client
- **Icons**: Font Awesome, Lucide Icons

#### DevOps & Deployment
- **Build Tool**: Maven 3.9
- **Containerization**: Docker (Multi-stage build)
- **Cloud Platform**: Render (Production deployment)
- **CI/CD**: GitHub Actions (Auto deploy)
- **Monitoring**: Spring Boot Actuator (Health check)

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

### 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### 🙏 Acknowledgments

- Spring Boot Team cho framework tuyệt vời
- PayOS cho payment gateway
- Google cho Gemini AI & OAuth2
- Goong cho Maps API
- Cloudinary cho image storage
- Tất cả open-source libraries được sử dụng trong dự án

---

<p align="center">
  Made with ❤️ by HCMUTE Students
</p>
