package com.emsi.shared.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Lista blanca estricta de formatos permitidos (MIME Types)
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map upload(MultipartFile multipartFile, String folder) throws IOException {

        // 1. Validar que el archivo no venga vacío
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío.");
        }

        // 2. Validación estricta Anti-Malware (Content-Type)
        String contentType = multipartFile.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Formato de archivo denegado. Solo se admiten imágenes JPG, PNG o WEBP.");
        }

        return cloudinary.uploader().upload(multipartFile.getBytes(),
                ObjectUtils.asMap("folder", folder));
    }

    public Map delete(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}