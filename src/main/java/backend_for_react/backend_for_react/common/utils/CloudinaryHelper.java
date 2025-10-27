package backend_for_react.backend_for_react.common.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CloudinaryHelper {
    private Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }

    // Upload file với resource type tự động phát hiện
    public String upload(MultipartFile file) throws IOException {
        Map<String, String> options = new HashMap<>();

        if (file.getContentType() != null) {
            if (file.getContentType().startsWith("video/")) {
                options.put("resource_type", "video");
            } else if (file.getContentType().startsWith("application/")) {
                options.put("resource_type", "raw");
            }
        }

        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) result.get("secure_url");
    }

    // Upload nhiều file cùng lúc
    public List<String> uploadAll(List<MultipartFile> files) throws IOException {
        return files.stream()
                .map(file -> {
                    try {
                        return this.upload(file);
                    } catch (IOException e) {
                        throw new RuntimeException("Upload failed for file: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    // Xóa file bằng public ID
    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    // Xóa nhiều file
    public void deleteAll(List<String> publicIds) {
        publicIds.forEach(id -> {
            try {
                this.delete(id);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + id, e);
            }
        });
    }

    // Trích xuất public ID từ URL Cloudinary
    public String extractPublicId(String url) {
        try {
            String[] parts = url.split("/upload/")[1].split("/");
            String lastPart = parts[parts.length - 1];
            return lastPart.split("\\.")[0];
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Cloudinary URL: " + url);
        }
    }

    // Xóa file bằng URL
    public void deleteByUrl(List <String> files) throws IOException {
        files.forEach(url -> {
            if (url != null && url.contains("cloudinary.com")) {
                String publicId = extractPublicId(url);
                try {
                    this.delete(publicId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}