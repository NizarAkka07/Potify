package com.alphateckplus.potify.pool.infrastructure.secondary.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${application.cloudinary.cloud-name}") String cloudName,
            @Value("${application.cloudinary.api-key}") String apiKey,
            @Value("${application.cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        return uploadVideo(file.getBytes());
    }

    public String uploadVideo(byte[] videoBytes) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(videoBytes, ObjectUtils.asMap(
                "resource_type", "video"
        ));
        return (String) uploadResult.get("secure_url");
    }

    public String uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file.getBytes());
    }

    public String uploadImage(byte[] imageBytes) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                "resource_type", "image"
        ));
        return (String) uploadResult.get("secure_url");
    }
}
