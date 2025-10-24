package com.dev.shoeshop.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file, String fileName);

    String storeFile(MultipartFile file);
    
    /**
     * Upload rating image to Cloudinary/Local storage
     * @param file Rating image file
     * @return Image URL
     */
    String storeRatingImage(MultipartFile file);
}
