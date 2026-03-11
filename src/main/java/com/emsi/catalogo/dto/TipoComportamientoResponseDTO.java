package com.emsi.catalogo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TipoComportamientoResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
}
