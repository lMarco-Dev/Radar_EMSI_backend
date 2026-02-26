package com.emsi.cloudinary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResponseDTO {
    private String url;
    private String publicId;
}
