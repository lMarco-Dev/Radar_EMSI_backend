package com.emsi.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CausaRequestDTO {
    @NotBlank(message = "El nombre de la causa es obligatorio")
    private String nombre;
}
