package com.emsi.reporte.dto;

import com.emsi.shared.enums.EstadoReporte;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambioEstadoDTO {
    @NotNull(message = "El estado es obligatorio")
    private EstadoReporte estado;
    private String comentario;
}
