package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.service.CloudinaryService;
import com.dev.shoeshop.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final CloudinaryService cloudinaryService;

    @Value("${storage.upload-dir}")
    private String uploadDir;

    @Value("${storage.base-url:/images/}")
    private String baseUrl;

    @Override
    public String uploadFile(MultipartFile file, String fileName) {
        return "";
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File trống!");
            }

            if (cloudinaryService != null) {
                try {
                    log.info("📤 Uploading to Cloudinary CDN: {}", file.getOriginalFilename());
                    String cloudinaryUrl = cloudinaryService.uploadImage(file, "products");
                    log.info("✅ Cloudinary upload SUCCESS: {}", cloudinaryUrl);
                    log.info("🚀 Image will be served via CDN (fast worldwide delivery)");
                    return cloudinaryUrl;
                } catch (Exception e) {
                    log.error("❌ Cloudinary upload FAILED: {}", e.getMessage());
                    log.warn("⚠️ Falling back to LOCAL storage (slower, no CDN)");
                    log.warn("⚠️ Check Render env vars: CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET");
                }
            } else {
                log.warn("⚠️ CloudinaryService is NULL - using LOCAL storage");
                log.warn("⚠️ This means Cloudinary credentials are NOT set in Render!");
            }

            // Fallback to local storage
            log.info("Using local storage for: {}", file.getOriginalFilename());
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            String baseName = "";

            if (originalFilename != null) {
                int dotIndex = originalFilename.lastIndexOf(".");
                if (dotIndex > 0) {
                    baseName = originalFilename.substring(0, dotIndex)
                            .replaceAll("[^a-zA-Z0-9_-]", "");
                    extension = originalFilename.substring(dotIndex);
                } else {
                    baseName = originalFilename;
                }
            }

            String filename = originalFilename;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + filename;

        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file: " + e.getMessage(), e);
        }
    }


}
