package com.emsi.reporte.dto;

import com.emsi.shared.enums.Turno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReporteRequestDTO {

    @NotNull(message = "La empresa es obligatoria")
    private Long empresaId;

    @NotNull(message = "El tipo de comportamiento es obligatorio")
    private Long tipoComportamientoId;

    private Long causaId;
    private String nombreReportante;

    @NotBlank(message = "El area es obligatoria")
    private String area;

    private Turno turno;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcionComportamiento;

    private String medidaContencion;

    @NotNull(message = "La fecha del ocurrido es obligatoria")
    private LocalDate fechaOcurrido;

    private String lugarEspecifico;
    private String camposDinamicos;
    private List<EvidenciaDTO> evidencias;

    @Data
    public static class EvidenciaDTO {
        private String urlCloudinary;
        private String publicIdCloudinary;
    }
}
