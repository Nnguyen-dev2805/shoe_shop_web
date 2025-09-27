package com.dev.shoeshop.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file, String fileName);

    String storeFile(MultipartFile file);
}
