package com.emsi.empresa.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmpresaResponseDTO {
    private Long id;
    private String nombre;
    private String ruc;
    private String logoUrl;
    private String tokenPublico;
    private Boolean activo;
    private LocalDateTime createdAt;
}
