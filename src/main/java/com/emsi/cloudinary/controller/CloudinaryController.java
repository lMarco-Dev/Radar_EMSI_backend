package com.emsi.cloudinary.controller;

import com.emsi.cloudinary.dto.UploadResponseDTO;
import com.emsi.cloudinary.service.CloudinaryService;
import com.emsi.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/publico/upload")
@RequiredArgsConstructor
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    @PostMapping
    public ResponseEntity<ApiResponse<UploadResponseDTO>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, String> result = cloudinaryService.upload(file, "emsi/reportes");
        return ResponseEntity.ok(ApiResponse.ok(UploadResponseDTO.builder()
                .url(result.get("url")).publicId(result.get("publicId")).build()));
    }
}
