package com.emsi.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TipoComportamientoRequestDTO {
    @NotBlank(message = "El nombre del tipo es obligatorio")
    private String nombre;
    private String descripcion;
}
