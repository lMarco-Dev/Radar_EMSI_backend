package com.emsi.reporte.dto;

import com.emsi.shared.enums.EstadoReporte;
import com.emsi.shared.enums.Turno;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReporteResponseDTO {
    private Long id;
    private Long empresaId;
    private String empresaNombre;
    private Long tipoComportamientoId;
    private String tipoComportamientoNombre;
    private Long causaId;
    private String causaNombre;
    private EstadoReporte estado;
    private String nombreReportante;
    private String area;
    private Turno turno;
    private String descripcionComportamiento;
    private String medidaContencion;
    private LocalDate fechaOcurrido;
    private String lugarEspecifico;
    private String camposDinamicos;
    private List<EvidenciaDTO> evidencias;
    private List<HistorialDTO> historial;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class EvidenciaDTO {
        private Long id;
        private String urlCloudinary;
        private String publicIdCloudinary;
    }

    @Data
    @Builder
    public static class HistorialDTO {
        private Long id;
        private EstadoReporte estadoAnterior;
        private EstadoReporte estadoNuevo;
        private String comentario;
        private String cambiadoPorNombre;
        private LocalDateTime fechaCambio;
    }
}
