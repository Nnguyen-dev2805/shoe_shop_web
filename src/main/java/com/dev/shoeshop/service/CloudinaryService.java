package com.dev.shoeshop.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;
    
    // ✅ Folder constants để tách biệt ảnh
    public static final String PRODUCT_FOLDER = "shoe_shop/products";
    public static final String RATING_FOLDER = "shoe_shop/ratings";
    public static final String AVATAR_FOLDER = "shoe_shop/avatars";

    /**
     * Upload image to Cloudinary
     * @param file MultipartFile to upload
     * @param folder Folder name in Cloudinary (e.g., PRODUCT_FOLDER, RATING_FOLDER)
     * @return Image URL from Cloudinary
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // ✅ Giữ nguyên tên file gốc (không thêm timestamp)
        String originalFilename = file.getOriginalFilename();
        String filenameWithoutExt = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(0, originalFilename.lastIndexOf("."))
            : (originalFilename != null ? originalFilename : "image");
        
        // Tạo public_id: shoe_shop/products/abc
        String publicId = folder + "/" + filenameWithoutExt;

        // Upload to Cloudinary
        // overwrite=false: Nếu file đã tồn tại, Cloudinary sẽ tự động thêm suffix (abc_1, abc_2...)
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", publicId,
                        "resource_type", "image",
                        "overwrite", false,
                        "unique_filename", true
                ));

        // Return secure URL
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Delete image from Cloudinary
     * @param imageUrl Full Cloudinary URL
     * @return true if deleted successfully
     */
    public boolean deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
                return false;
            }

            // Extract public_id from URL
            // URL format: https://res.cloudinary.com/cloud-name/image/upload/v123456/folder/filename.jpg
            String publicId = extractPublicId(imageUrl);
            
            if (publicId != null) {
                Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                return "ok".equals(result.get("result"));
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extract public_id from Cloudinary URL
     */
    private String extractPublicId(String imageUrl) {
        try {
            // Example URL: https://res.cloudinary.com/demo/image/upload/v1234567890/products/abc123.jpg
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) return null;
            
            String afterUpload = parts[1];
            // Remove version (v1234567890/)
            afterUpload = afterUpload.replaceFirst("v\\d+/", "");
            // Remove extension
            afterUpload = afterUpload.replaceFirst("\\.[^.]+$", "");
            
            return afterUpload;
        } catch (Exception e) {
            return null;
        }
    }
}
