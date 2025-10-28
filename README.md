<div align="center">

# 👟 DeeG Shoe Shop
### Hệ Thống Quản Lý Cửa Hàng Giày Dép Trực Tuyến

<!-- Thêm logo/banner ở đây -->
![DeeG Shoe Shop Banner](./docs/images/banner.png)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

[🎥 Demo Video](#-demo-video) • [✨ Tính Năng](#-tính-năng-nổi-bật) • [🚀 Cài Đặt](#-hướng-dẫn-cài-đặt) • [📸 Screenshots](#-screenshots) • [📚 API Docs](#-api-documentation)

</div>

---

## 📋 Mục Lục

- [📝 Giới Thiệu](#-giới-thiệu)
- [🎥 Demo Video](#-demo-video)
- [📸 Screenshots](#-screenshots)
- [✨ Tính Năng Nổi Bật](#-tính-năng-nổi-bật)
- [🛠 Công Nghệ Sử Dụng](#-công-nghệ-sử-dụng)
- [📦 Cấu Trúc Dự Án](#-cấu-trúc-dự-án)
- [🗄 Database Schema](#-database-schema)
- [🚀 Hướng Dẫn Cài Đặt](#-hướng-dẫn-cài-đặt)
- [🐳 Deploy với Docker](#-deploy-với-docker)
- [☁️ Deploy lên Cloud](#️-deploy-lên-render)
- [📚 API Documentation](#-api-documentation)
- [🔐 Phân Quyền](#-phân-quyền-roles)
- [👥 Tác Giả](#-tác-giả)
- [📄 License](#-license)

---

## 📝 Giới Thiệu

**DeeG Shoe Shop** là hệ thống website bán hàng và quản lý cửa hàng giày dép toàn diện, được xây dựng bằng **Spring Boot 3.5.6** và các công nghệ hiện đại. Hệ thống cung cấp đầy đủ tính năng từ quản lý sản phẩm, đặt hàng, thanh toán trực tuyến, đến các tính năng nâng cao như **Flash Sale**, **Voucher System**, **AI Chatbot** (Gemini AI), và tích hợp **Goong Maps** để tính phí ship tự động.

### 🎯 Mục Tiêu Dự Án

- ✅ Xây dựng hệ thống e-commerce hoàn chỉnh với đầy đủ tính năng mua sắm trực tuyến
- ✅ Tích hợp các công nghệ hiện đại: AI Chatbot, Payment Gateway, Cloud Storage
- ✅ Hỗ trợ đa vai trò: Admin, Manager, Shipper, User
- ✅ Tối ưu trải nghiệm người dùng với UI/UX thân thiện
- ✅ Sẵn sàng deploy production với Docker và Cloud Platform

---

## 🎥 Demo Video

<!-- Thêm link YouTube demo video ở đây -->
<div align="center">

[![Demo Video](./docs/images/video-thumbnail.png)](https://www.youtube.com/watch?v=YOUR_VIDEO_ID)

**[▶️ Xem Video Demo Đầy Đủ Trên YouTube](https://www.youtube.com/watch?v=YOUR_VIDEO_ID)**

*Video demo hướng dẫn sử dụng hệ thống từ góc nhìn User, Admin, Manager và Shipper*

</div>

---

## 📸 Screenshots

### 🏠 Trang Người Dùng

#### Homepage & Shop
<div align="center">
  
![Homepage](./docs/images/homepage.png)
*Trang chủ với banner, sản phẩm nổi bật và Flash Sale*

![Shop Page](./docs/images/shop.png)
*Trang danh sách sản phẩm với bộ lọc thông minh*

</div>

#### Chi Tiết Sản Phẩm & Giỏ Hàng
<div align="center">

![Product Detail](./docs/images/product-detail.png)
*Chi tiết sản phẩm với hình ảnh, mô tả, size và đánh giá*

![Shopping Cart](./docs/images/cart.png)
*Giỏ hàng với chọn nhiều sản phẩm và áp dụng voucher*

</div>

#### Checkout & Thanh Toán
<div align="center">

![Checkout](./docs/images/checkout.png)
*Trang thanh toán với chọn địa chỉ và phương thức thanh toán*

![PayOS Payment](./docs/images/payment.png)
*Thanh toán qua PayOS với QR Code*

</div>

#### Flash Sale & Voucher
<div align="center">

![Flash Sale](./docs/images/flashsale.png)
*Trang Flash Sale với countdown timer và stock realtime*

![Voucher Collection](./docs/images/voucher.png)
*Kho voucher giảm giá đơn hàng và phí ship*

</div>

#### AI Chatbot & Thông Báo
<div align="center">

![AI Chatbot](./docs/images/chatbot.png)
*AI Chatbot hỗ trợ 24/7 powered by Gemini AI*

![User Profile](./docs/images/profile.png)
*Trang quản lý tài khoản và đơn hàng*

</div>

---

### 👨‍💼 Trang Quản Trị

#### Admin Dashboard
<div align="center">

![Admin Dashboard](./docs/images/admin-dashboard.png)
*Dashboard với thống kê doanh thu, đơn hàng và biểu đồ*

![Product Management](./docs/images/admin-products.png)
*Quản lý sản phẩm với CRUD và upload ảnh*

</div>

#### Quản Lý Đơn Hàng & Flash Sale
<div align="center">

![Order Management](./docs/images/admin-orders.png)
*Quản lý đơn hàng với lọc trạng thái và xuất Excel*

![Flash Sale Management](./docs/images/admin-flashsale.png)
*Tạo và quản lý Flash Sale với thêm sản phẩm*

</div>

#### Quản Lý Voucher & Kho Hàng
<div align="center">

![Voucher Management](./docs/images/admin-voucher.png)
*Quản lý voucher với điều kiện và giới hạn sử dụng*

![Inventory Management](./docs/images/admin-inventory.png)
*Quản lý tồn kho và nhập xuất hàng*

</div>

---

### 🚚 Trang Shipper

<div align="center">

![Shipper Orders](./docs/images/shipper-orders.png)
*Danh sách đơn hàng cần giao với bộ lọc trạng thái*

![Shipper Order Detail](./docs/images/shipper-detail.png)
*Chi tiết đơn hàng với thông tin người nhận và cập nhật trạng thái*

</div>

---

### 📱 Responsive Design

<div align="center">

![Mobile Responsive](./docs/images/mobile-responsive.png)
*Giao diện responsive hoàn hảo trên mọi thiết bị*

</div>

---

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
