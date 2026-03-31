package com.testimonialcms.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String, String> uploadImage(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",    "testimonial-cms/images",
                            "resource_type", "image"
                    )
            );
            return Map.of(
                    "url",          (String) result.get("secure_url"),
                    "cloudinaryId", (String) result.get("public_id")
            );
        } catch (IOException e) {
            log.error("Error subiendo imagen a Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Error al subir imagen", e);
        }
    }

    public Map<String, String> uploadVideo(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",        "testimonial-cms/videos",
                            "resource_type", "video"
                    )
            );
            return Map.of(
                    "url",          (String) result.get("secure_url"),
                    "cloudinaryId", (String) result.get("public_id"),
                    "thumbnailUrl", buildVideoThumbnail((String) result.get("public_id"))
            );
        } catch (IOException e) {
            log.error("Error subiendo video a Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Error al subir video", e);
        }
    }

    public void deleteAsset(String cloudinaryId, String resourceType) {
        try {
            cloudinary.uploader().destroy(
                    cloudinaryId,
                    ObjectUtils.asMap("resource_type", resourceType)
            );
        } catch (IOException e) {
            log.warn("No se pudo eliminar asset de Cloudinary: {}", e.getMessage());
        }
    }

    private String buildVideoThumbnail(String publicId) {
        return cloudinary.url()
                .resourceType("video")
                .format("jpg")
                .transformation(new com.cloudinary.Transformation().width(640).height(360).crop("fill"))
                .generate(publicId);
    }
}
