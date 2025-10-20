# Stage 1: Build stage - Maven build
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
# chạy maven để tải toàn bộ dependency về local repository
# -T 1C: Build với parallel threads (1 thread per CPU core)
RUN mvn dependency:go-offline -B -T 1C

# Copy source code
COPY src ./src

# Build application (skip tests for faster build)
# -T 1C: Parallel build để compile nhanh hơn
# Note: Không dùng -o (offline) để tránh lỗi missing dependencies lần đầu
RUN mvn clean package -DskipTests -T 1C

# Verify JAR file was created
# dùng để debug có thể xóa
RUN ls -la /app/target/ && \
    echo "=== Build complete ===" && \
    ls -lh /app/target/*.jar

# Stage 2: Runtime stage - Run application
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Install wget for health check
RUN apk add --no-cache wget

# Create non-root user for security
# Chạy chương trình bằng user chứ không phải root
RUN addgroup -S spring && adduser -S spring -G spring

# Copy JAR file from build
# copy file jar từ bước 1 đặt tên nội bộ là app.jar
COPY --from=build /app/target/shoe_shop_web-0.0.1-SNAPSHOT.jar app.jar

# Create uploads directory for file storage
# tạo thư mục để chứa ảnh và chuyển quyền sở hữu /app sang user spring để user non-root có quyền ghi
RUN mkdir -p /app/uploads && chown -R spring:spring /app

# Switch to non-root user
# Từ đây mọi quyền xử lý đều là thuộc user chứ không phải root
USER spring:spring

# Expose port (Render will override with PORT env variable)
EXPOSE 8080

# Set active profile to production
ENV SPRING_PROFILES_ACTIVE=pro

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run application with dynamic PORT from environment
# Render will inject PORT environment variable (usually 10000)
# Optimized JVM settings for Render Free Tier (512MB RAM):
# -Xmx400m: Max heap 400MB (leave 112MB for native memory)
# -Xms200m: Initial heap 200MB (reduce startup memory)
# -XX:+UseG1GC: G1 Garbage Collector (better for low pause time)
# -XX:MaxGCPauseMillis=200: Target max GC pause 200ms
# -XX:MaxMetaspaceSize=128m: Limit metaspace to 128MB
ENTRYPOINT ["sh", "-c", "java -Xmx400m -Xms200m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:MaxMetaspaceSize=128m -jar -Dserver.port=${PORT:-8080} app.jar"]