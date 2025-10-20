package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.service.CloudinaryService;
import com.dev.shoeshop.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StorageServiceImpl implements StorageService {

    @Autowired(required = false)
    private CloudinaryService cloudinaryService;

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

            // Try Cloudinary first (if configured)
            if (cloudinaryService != null) {
                try {
                    log.info("Uploading to Cloudinary: {}", file.getOriginalFilename());
                    String cloudinaryUrl = cloudinaryService.uploadImage(file, "products");
                    log.info("Uploaded to Cloudinary successfully: {}", cloudinaryUrl);
                    return cloudinaryUrl;
                } catch (Exception e) {
                    log.error("Cloudinary upload failed, falling back to local storage: {}", e.getMessage());
                    // Fall through to local storage
                }
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
