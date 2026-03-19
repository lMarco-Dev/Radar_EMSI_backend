package com.emsi.reporte.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardDTO {
    private Map<String, Long> contadores;
    private List<Map<String, Object>> porArea;
    private List<Map<String, Object>> porEmpresa;
    private List<Map<String, Object>> porTipo;
    private List<Map<String, Object>> tendencia;
    private String mesInicioSistema;
}