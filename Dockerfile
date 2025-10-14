# Stage 1: Build stage - Maven build
FROM maven:3.9-eclipse-temurin-22 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Verify WAR file was created
RUN ls -la /app/target/

# Stage 2: Runtime stage - Run application
FROM eclipse-temurin:22-jre-alpine

# Set working directory
WORKDIR /app

# Install wget for health check
RUN apk add --no-cache wget

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy WAR file from build stage (đúng tên artifact từ pom.xml)
COPY --from=build /app/target/shoe_shop_web-0.0.1-SNAPSHOT.war app.war

# Create uploads directory for file storage
RUN mkdir -p /app/uploads && chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port (Render will override with PORT env variable)
EXPOSE 8080

# Set active profile to production
ENV SPRING_PROFILES_ACTIVE=pro

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run application with dynamic PORT from environment
# Render sẽ inject PORT environment variable
ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=${PORT:-8080} app.war"]