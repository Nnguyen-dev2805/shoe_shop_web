package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.service.StorageService;
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
public class StorageServiceImpl implements StorageService {

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

            String filename = baseName + "_" + System.currentTimeMillis() + extension;

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
