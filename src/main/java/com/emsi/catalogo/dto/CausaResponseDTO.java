package com.emsi.catalogo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CausaResponseDTO {
    private Long id;
    private String nombre;
    private Boolean activo;
}
