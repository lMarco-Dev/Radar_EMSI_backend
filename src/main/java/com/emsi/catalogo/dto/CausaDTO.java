package com.emsi.catalogo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CausaDTO {
    private Long id;
    private String nombre;
    private Boolean activo;
}
