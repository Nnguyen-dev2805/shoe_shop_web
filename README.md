# DeeG Shoe Shop - Website Bán Giày Trực Tuyến

<div align="center">
  <img src="src/main/resources/static/img/logo-1.png" alt="DeeG Shoe Shop Logo" width="200"/>
</div>

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

### Tính Năng Quản Trị

**1. Dashboard & Phân Tích**
- Thống kê realtime:
  - Tổng doanh thu (hôm nay, tháng này, năm nay)
  - Tổng đơn hàng theo trạng thái
  - Số lượng khách hàng mới
  - Tổng sản phẩm và cảnh báo sắp hết hàng
- Biểu đồ doanh thu:
  - Biểu đồ đường: Doanh thu theo thời gian
  - Biểu đồ cột: Doanh thu theo danh mục
  - Biểu đồ tròn: Phân bố trạng thái đơn hàng
- Sản phẩm bán chạy nhất (với hình ảnh và số lượng đã bán)
- Danh sách đơn hàng gần đây
- Thao tác nhanh: Thêm sản phẩm, Tạo Flash Sale, Xem báo cáo

**2. Quản Lý Sản Phẩm**
- Danh sách sản phẩm với phân trang và tìm kiếm
- Thao tác CRUD:
  - Tạo sản phẩm với nhiều size
  - Sửa thông tin sản phẩm
  - Xóa sản phẩm (xóa mềm)
  - Kích hoạt/vô hiệu hóa sản phẩm
- Thao tác hàng loạt:
  - Cập nhật giá hàng loạt
  - Gán danh mục hàng loạt
  - Xóa hàng loạt
- Quản lý biến thể sản phẩm (sizes):
  - Thêm/xóa size
  - Đặt giá theo size
  - Đặt tồn kho theo size
- Quản lý hình ảnh:
  - Upload nhiều ảnh lên Cloudinary
  - Đặt ảnh chính
  - Xóa ảnh
  - Tối ưu ảnh qua CDN
- Import/Export sản phẩm (Excel)

**3. Quản Lý Danh Mục & Thương Hiệu**
- CRUD Danh mục:
  - Tạo/sửa/xóa danh mục
  - Phân cấp danh mục (cha-con)
  - Upload ảnh danh mục
  - Cài đặt SEO (slug, meta description)
- CRUD Thương hiệu:
  - Thêm/sửa/xóa thương hiệu
  - Upload logo thương hiệu
  - Mô tả thương hiệu

**4. Quản Lý Đơn Hàng**
- Danh sách đơn hàng với bộ lọc nâng cao:
  - Trạng thái (Chờ xử lý, Đang giao, Đã giao, Hủy, Trả hàng)
  - Phương thức thanh toán (COD, PayOS)
  - Khoảng thời gian
  - Tìm kiếm khách hàng
  - Tìm kiếm mã đơn hàng
- Xem chi tiết đơn hàng:
  - Thông tin khách hàng
  - Sản phẩm trong đơn với giá
  - Địa chỉ giao hàng
  - Trạng thái thanh toán
  - Lịch sử dòng thời gian
- Thao tác đơn hàng:
  - Cập nhật trạng thái đơn hàng
  - Phân công shipper
  - In hóa đơn
  - Hủy đơn hàng (kèm lý do)
  - Xử lý hoàn tiền
- Xuất đơn hàng ra Excel:
  - Tùy chỉnh khoảng thời gian
  - Lọc theo trạng thái
  - Bao gồm chi tiết đơn hàng
- Thao tác hàng loạt:
  - Cập nhật trạng thái hàng loạt
  - Phân công shipper hàng loạt

**5. Quản Lý Flash Sale**
- Màn hình tạo Flash Sale:
  - Đặt tên, mô tả
  - Upload ảnh banner
  - Đặt khoảng thời gian (bắt đầu - kết thúc)
  - Chọn sản phẩm tham gia
- Thêm sản phẩm vào Flash Sale:
  - Tìm kiếm và chọn sản phẩm
  - Đặt phần trăm giảm giá cho từng sản phẩm
  - Đặt giới hạn tồn kho cho từng sản phẩm
- Danh sách Flash Sale:
  - Flash Sale đang hoạt động
  - Flash Sale đã lên lịch
  - Flash Sale đã kết thúc
- Thao tác Flash Sale:
  - Sửa chi tiết Flash Sale
  - Thêm/xóa sản phẩm
  - Kết thúc Flash Sale sớm
  - Sao chép Flash Sale
  - Xóa Flash Sale
- Giám sát realtime:
  - Tổng số lượng đã bán
  - Doanh thu tạo ra
  - Tồn kho còn lại
  - Số người tham gia

**6. Quản Lý Voucher/Giảm Giá**
- Form tạo voucher:
  - Tên và mã voucher
  - Loại giảm giá (Đơn hàng hoặc Vận chuyển)
  - Kiểu giá trị (Phần trăm hoặc Số tiền cố định)
  - Giá trị giảm giá
  - Yêu cầu giá trị đơn hàng tối thiểu
  - Yêu cầu hạng thành viëa
  - Giới hạn sử dụng mỗi người
  - Tổng số lượng
  - Thời hạn hiệu lực (ngày bắt đầu - kết thúc)
  - Trạng thái (Hoạt động, Ngừng, Hết hạn)
- Danh sách voucher với bộ lọc
- Thao tác voucher:
  - Sửa chi tiết voucher
  - Kích hoạt/vô hiệu hóa
  - Xóa voucher
  - Gia hạn thời hạn hiệu lực
- Thống kê sử dụng:
  - Tổng số lần sử dụng
  - Tổng số tiền giảm giá đã cho
  - Người dùng đã sử dụng
  - Phân tích tác động doanh thu

**7. Quản Lý Tồn Kho & Kho Hàng**
- Cấu hình kho hàng:
  - Thêm/sửa kho hàng
  - Đặt tọa độ GPS
  - Đặt giờ hoạt động
- Giám sát mức tồn kho:
  - Tồn kho hiện tại theo chi tiết sản phẩm
  - Cảnh báo sắp hết hàng (<10 sản phẩm)
  - Sản phẩm hết hàng
- Thao tác tồn kho:
  - Điều chỉnh tồn kho (tăng/giảm)
  - Nhập kho (tiếp nhận hàng)
  - Chuyển kho giữa các kho
- Lịch sử tồn kho:
  - Nhật ký di chuyển
  - Bản ghi nhập/xuất
  - Lý do điều chỉnh
  - Theo dõi người thực hiện
- Báo cáo tồn kho:
  - Mức tồn kho hiện tại
  - Lịch sử di chuyển tồn kho
  - Báo cáo sắp hết hàng

**8. Quản Lý Vận Chuyển**
- Cấu hình đơn vị vận chuyển:
  - Thêm/sửa nhà cung cấp vận chuyển
  - Upload logo công ty
  - Đặt mức giá cơ bản
  - Đặt giá theo km
  - Định nghĩa bậc khoảng cách với mức giá khác nhau
- Công cụ tính phí vận chuyển:
  - Kiểm tra tính khoảng cách
  - Xác minh giá
- Phân công shipper:
  - Phân công đơn hàng cho shipper
  - Theo dõi hiệu suất shipper
  - Tỷ lệ hoàn thành giao hàng

**9. Quản Lý Người Dùng**
- Danh sách người dùng với tìm kiếm và bộ lọc:
  - Lọc vai trò
  - Ngày đăng ký
  - Hạng thành viên
  - Trạng thái hoạt động/ngừng
- Thao tác người dùng:
  - Xem hồ sơ người dùng
  - Sửa thông tin người dùng
  - Thay đổi vai trò người dùng
  - Kích hoạt/vô hiệu hóa tài khoản
  - Xem đơn hàng của người dùng
  - Xem nhật ký hoạt động
- Quản lý thành viên:
  - Nâng/hạ hạng
  - Điều chỉnh điểm thành viên
  - Điều chỉnh số dư DeeG Xu
- Thao tác hàng loạt:
  - Gửi email cho người dùng
  - Xuất danh sách người dùng

**10. Quản Lý Yêu Cầu Trả Hàng**
- Danh sách yêu cầu trả hàng
- Chi tiết yêu cầu trả hàng:
  - Thông tin đơn hàng
  - Lý do trả hàng
  - Hình ảnh trả hàng
  - Ghi chú của khách hàng
- Thao tác trả hàng:
  - Phê duyệt trả hàng
  - Từ chối trả hàng (kèm lý do)
  - Xử lý hoàn tiền
  - Sắp xếp vận chuyển trả hàng
- Thống kê trả hàng:
  - Tổng số trả hàng
  - Tỷ lệ trả hàng
  - Lý do trả hàng phổ biến

**11. Quản Lý Phân Quyền & Vai Trò**
- Kiểm soát truy cập dựa trên vai trò (RBAC)
- Bốn vai trò chính:
  - QUẢN TRỊ VIÊN: Truy cập toàn bộ hệ thống
  - QUẢN LÝ: Truy cập sản phẩm, đơn hàng, báo cáo
  - SHIPPER: Chỉ quản lý giao hàng
  - KHÁCH HÀNG: Chỉ các tính năng khách hàng
- Phân quyền cho từng vai trò
- Tạo quyền tùy chỉnh (tùy chọn)

**12. Cấu Hình Hệ Thống**
- Cài đặt chung:
  - Tên website, logo, favicon
  - Thông tin liên hệ
  - Liên kết mạng xã hội
- Mẫu email:
  - Xác nhận đơn hàng
  - Thông báo vận chuyển
  - Đặt lại mật khẩu
- Cài đặt cổng thanh toán:
  - Thông tin xác thực PayOS
  - Chế độ test/production
- Quản lý API key:
  - Cloudinary
  - Goong Maps
  - Gemini AI

### Tính Năng Shipper

**1. Dashboard Shipper**
- Thống kê giao hàng hôm nay:
  - Tổng đơn hàng được phân công
  - Đơn hàng đã giao thành công
  - Đơn hàng chờ giao
  - Đơn hàng giao thất bại
- Chỉ số hiệu suất:
  - Tỷ lệ thành công
  - Thời gian giao hàng trung bình
  - Đánh giá của khách hàng

**2. Phân Công & Quản Lý Đơn Hàng**
- Danh sách đơn hàng được phân công với bộ lọc:
  - Trạng thái (Đang giao, Đã giao)
  - Ngày giao hàng
  - Khu vực/quận
- Chi tiết đơn hàng:
  - Thông tin khách hàng
  - Số điện thoại (gọi trực tiếp)
  - Địa chỉ giao hàng với bản đồ
  - Sản phẩm trong đơn
  - Phương thức thanh toán (số tiền COD)
  - Ghi chú giao hàng
- Tích hợp bản đồ:
  - Xem vị trí giao hàng trên bản đồ
  - Lấy hướng dẫn (Goong Maps)
  - Lên lộ trình tối ưu (tùy chọn)

**3. Thao Tác Giao Hàng**
- Cập nhật trạng thái giao hàng:
  - Đánh dấu đang giao (đã lấy hàng)
  - Đánh dấu đã giao (với xác nhận)
  - Đánh dấu giao thất bại (kèm lý do)
- Lý do giao thất bại:
  - Khách hàng không có sẵn
  - Địa chỉ sai
  - Khách hàng từ chối nhận
  - Lý do khác (tùy chỉnh)
- Thu tiền COD:
  - Xác nhận đã nhận tiền mặt
  - Ghi nhận thanh toán
- Bằng chứng giao hàng:
  - Upload ảnh giao hàng
  - Chữ ký khách hàng (tùy chọn)

**4. Theo Dõi Hiệu Suất**
- Lịch sử giao hàng
- Phản hồi của khách hàng
- Đánh giá và nhận xét từ khách hàng
- Theo dõi thu nhập (nếu trả hoa hồng)

## Cấu Trúc Dự Án

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

## Database Schema

Hệ thống sử dụng **33 entities** chính:

**Entities Cốt Lõi:**
- `Users`, `Role`, `Address`, `UserAddress`, `PasswordResetToken`
- `Product`, `ProductDetail`, `Brand`, `Category`
- `Inventory`, `InventoryHistory`, `Cart`, `CartDetail`, `WishList`
- `Order`, `OrderDetail`, `Rating`

**Giảm Giá & Flash Sale:**
- `Discount`, `DiscountUsed`
- `FlashSale`, `FlashSaleItem`

**Vận Chuyển:**
- `ShippingCompany`, `ShippingRate`, `Shipment`, `Shipper`
- `ShopWarehouse`, `DistanceCache`

**Trả Hàng:**
- `ReturnRequest`, `ReturnShipment`

**Giao Tiếp:**
- `ChatConversation`, `ChatMessage`

**Thành Viên:**
- `CoinTransaction`

## Hướng Dẫn Cài Đặt

### Yêu Cầu Hệ Thống
- **Java**: 21 trở lên
- **Maven**: 3.9+
- **MySQL**: 8.0+
- **IDE**: IntelliJ IDEA / Eclipse / VSCode

### Bước 1: Clone Repository
```bash
git clone https://github.com/Nnguyen-dev2805/shoe_shop_web.git
cd shoe_shop_web
```

### Bước 2: Cấu Hình Database
Tạo database MySQL:
```sql
CREATE DATABASE shoe_shop_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Bước 3: Cấu Hình Biến Môi Trường
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

#### Bước 4: Build & Chạy Ứng Dụng
```bash
# Build project
mvn clean install -DskipTests

# Chạy ứng dụng
mvn spring-boot:run

# Hoặc chạy file JAR
java -jar target/shoe_shop_web-0.0.1-SNAPSHOT.jar
```

Ứng dụng sẽ chạy tại: `http://localhost:8081`

### Bước 5: Truy Cập Hệ Thống

**Trang Khách Hàng:**
- Trang chủ: `http://localhost:8081/`
- Cửa hàng: `http://localhost:8081/product/list`
- Đăng nhập: `http://localhost:8081/login`

**Trang Quản Trị:**
- URL: `http://localhost:8081/admin`
- Tài khoản mặc định: admin/admin (tạo trong database)

---

## Triển Khai Với Docker

### Build Docker Image
```bash
docker build -t shoe-shop-web:latest .
```

### Chạy Container
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

## Triển Khai Lên Render

1. **Tạo Web Service** trên Render Dashboard
2. **Kết nối Repository** từ GitHub
3. **Cấu hình biến môi trường** trong Render Dashboard (theo `.env.example`)
4. **Deploy**: Render sẽ tự động build từ `Dockerfile` và triển khai

**Endpoint kiểm tra sức khỏe**: `/actuator/health`

---

## Tài Liệu API

Hệ thống cung cấp tài liệu API đầy đủ thông qua **Swagger UI**:

- **Swagger UI**: `http://localhost:8081/swagger-ui/index.html`
- **API Docs JSON**: `http://localhost:8081/v3/api-docs`

Swagger UI cung cấp:
- Danh sách đầy đủ các REST API endpoints
- Mô tả chi tiết tham số và response
- Tính năng test API trực tiếp trên trình duyệt
- Ví dụ request/response cho từng endpoint

---

## Phân Quyền Hệ Thống

| Vai Trò | Quyền Truy Cập |
|------|----------------|
| **QUẢN TRỊ VIÊN** | Toàn quyền: Dashboard, quản lý sản phẩm, đơn hàng, voucher, flash sale, kho, người dùng, phân quyền |
| **QUẢN LÝ** | Quản lý sản phẩm, danh mục, đơn hàng, xem báo cáo |
| **SHIPPER** | Xem và cập nhật đơn hàng cần giao |
| **KHÁCH HÀNG** | Mua sắm, quản lý tài khoản, đặt hàng, đánh giá |

---

## Kiểm Thử

```bash
# Chạy tất cả test
mvn test

# Chạy test cụ thể
mvn test -Dtest=ServiceTest
```

---

## Hiệu Suất & Tối Ư u Hóa

- **Lazy Loading**: Entity relationships sử dụng `LAZY` fetch
- **Connection Pooling**: HikariCP (mặc định của Spring Boot)
- **Caching**: Distance Cache (Goong API), thời gian sống 30 ngày
- **Tối ưu hóa ảnh**: Cloudinary tự động tối ưu hóa
- **Database Indexing**: Index trên foreign keys và cột tìm kiếm
- **Quản lý transaction**: `@Transactional` cho các thao tác quan trọng
- **Pessimistic Locking**: Flash Sale để tránh bán quá hàng

---

## Khắc Phục Sự Cố

### Lỗi kết nối Database
```properties
spring.jpa.hibernate.ddl-auto=update
```
Đảm bảo MySQL đang chạy và thông tin xác thực đúng.

### Lỗi Đăng Nhập Google OAuth2
- Kiểm tra redirect URI trong Google Console
- Production: `https://yourdomain.com/login/oauth2/code/google`
- Local: `http://localhost:8081/login/oauth2/code/google`

### Lỗi PayOS Webhook
- Webhook URL phải là HTTPS (production)
- Kiểm thử local: Sử dụng ngrok hoặc bỏ qua webhook

---

## Giao Diện Website

### Screenshots

**Trang Khách Hàng:**

#### Trang Chủ
<!-- ![Trang chủ](docs/images/homepage.png) -->

#### Danh Sách Sản Phẩm
<!-- ![Cửa hàng](docs/images/shop.png) -->

#### Chi Tiết Sản Phẩm
<!-- ![Chi tiết sản phẩm](docs/images/product-detail.png) -->

#### Giỏ Hàng
<!-- ![Giỏ hàng](docs/images/cart.png) -->

#### Thanh Toán
<!-- ![Thanh toán](docs/images/checkout.png) -->

#### Flash Sale
<!-- ![Flash Sale](docs/images/flashsale.png) -->

#### Kho Voucher
<!-- ![Kho voucher](docs/images/voucher.png) -->

#### AI Chatbot
<!-- ![AI Chatbot](docs/images/chatbot.png) -->

**Trang Quản Trị:**

#### Dashboard Admin
<!-- ![Dashboard Admin](docs/images/admin-dashboard.png) -->

#### Quản Lý Sản Phẩm
<!-- ![Quản lý sản phẩm](docs/images/admin-products.png) -->

#### Quản Lý Đơn Hàng
<!-- ![Quản lý đơn hàng](docs/images/admin-orders.png) -->

#### Quản Lý Flash Sale
<!-- ![Quản lý Flash Sale](docs/images/admin-flashsale.png) -->

**Trang Shipper:**

#### Danh Sách Đơn Giao
<!-- ![Danh sách đơn shipper](docs/images/shipper-orders.png) -->

### Video Demo

**Link YouTube:**

<!-- [![Video Demo](https://img.youtube.com/vi/YOUR_VIDEO_ID/maxresdefault.jpg)](https://www.youtube.com/watch?v=YOUR_VIDEO_ID) -->

> **Cách thêm video**: Thay `YOUR_VIDEO_ID` bằng ID video YouTube của bạn

---

## Tài Liệu Tham Khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2)
- [PayOS Documentation](https://payos.vn/docs)
- [Goong Maps API](https://docs.goong.io)
- [Cloudinary Upload API](https://cloudinary.com/documentation)

---

## Tác Giả

Dự án được phát triển bởi nhóm sinh viên:

- **Trương Nhất Nguyên** - 23110273
- **Nguyễn Hoàng Hà** - 23110207  
- **Nghiêm Quang Huy** - 23110222
- **Nguyễn Tấn Yên** - 23110369

**Trường**: Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE)  
**Môn học**: Lập trình Web  
**Năm học**: 2024-2025

---

## Đóng Góp

Chúng tôi rất hoan nghênh mọi đóng góp! Nếu bạn muốn đóng góp:

1. Fork repository này
2. Tạo branch mới (`git checkout -b feature/TinhNangMoi`)
3. Commit thay đổi (`git commit -m 'Thêm tính năng mới'`)
4. Push lên branch (`git push origin feature/TinhNangMoi`)
5. Tạo Pull Request

---

## Liên Hệ & Hỗ Trợ

- **Email**: tnhatnguyen.dev2805@gmail.com
- **GitHub**: [Nnguyen-dev2805/shoe_shop_web](https://github.com/Nnguyen-dev2805/shoe_shop_web)

Nếu gặp vấn đề hoặc có câu hỏi, vui lòng tạo [Issue](https://github.com/Nnguyen-dev2805/shoe_shop_web/issues) trên GitHub.

---

## Giấy Phép

Dự án này được phân phối dưới **Giấy phép MIT** - xem file [LICENSE](LICENSE) để biết thêm chi tiết.

### Tóm Tắt Giấy Phép MIT
- ✅ Sử dụng thương mại
- ✅ Sửa đổi
- ✅ Phân phối
- ✅ Sử dụng cá nhân
- ❌ Không chịu trách nhiệm
- ❌ Không bảo hành

---

## Thống Kê Dự Án

![GitHub repo size](https://img.shields.io/github/repo-size/Nnguyen-dev2805/shoe_shop_web)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/Nnguyen-dev2805/shoe_shop_web)
![GitHub language count](https://img.shields.io/github/languages/count/Nnguyen-dev2805/shoe_shop_web)
![GitHub top language](https://img.shields.io/github/languages/top/Nnguyen-dev2805/shoe_shop_web)

---

<div align="center">

### Nếu thấy dự án hữu ích, hãy cho chúng tôi một Star!

**Made with ❤️ by HCMUTE Students**

**Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh**

**© 2024-2025 DeeG Shoe Shop. All Rights Reserved.**

</div>
