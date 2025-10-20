# Stage 1: Build stage - Maven build
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Copy source code
COPY src ./src

# Build application (skip tests for faster build)
# Maven will automatically download dependencies during build
RUN mvn clean package -DskipTests -B

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
# -Xmx350m: Max heap 350MB (tighter limit for 512MB container)
# -Xms128m: Initial heap 128MB (reduce startup memory)
# -XX:+UseSerialGC: Serial GC (lowest memory overhead)
# -XX:MaxMetaspaceSize=100m: Limit metaspace to 100MB
# -XX:CompressedClassSpaceSize=32m: Reduce class metadata space
# -XX:ReservedCodeCacheSize=64m: Reduce code cache
# -Xss256k: Reduce thread stack size
# -XX:+TieredCompilation -XX:TieredStopAtLevel=1: Faster startup, less memory
ENTRYPOINT ["sh", "-c", "java -Xmx350m -Xms128m -XX:+UseSerialGC -XX:MaxMetaspaceSize=100m -XX:CompressedClassSpaceSize=32m -XX:ReservedCodeCacheSize=64m -Xss256k -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -jar -Dserver.port=${PORT:-8080} app.jar"]