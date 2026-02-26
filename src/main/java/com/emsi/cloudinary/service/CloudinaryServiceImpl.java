package com.emsi.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public Map<String, String> upload(MultipartFile file, String folder) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap("folder", folder, "resource_type", "auto");
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
        Map<String, String> response = new HashMap<>();
        response.put("url", result.get("secure_url").toString());
        response.put("publicId", result.get("public_id").toString());
        return response;
    }

    @Override
    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
